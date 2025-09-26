 package com.cloudsuites.framework.modules.auth.service.impl;

import com.cloudsuites.framework.services.auth.RateLimitService;
import com.cloudsuites.framework.services.auth.entities.RateLimitResult;
import com.cloudsuites.framework.services.auth.entities.RateLimitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis-based rate limiting service implementation.
 * Uses sliding window algorithm for accurate rate limiting and supports account lockout.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
@Service
public class RedisRateLimitServiceImpl implements RateLimitService {

    private static final Logger logger = LoggerFactory.getLogger(RedisRateLimitServiceImpl.class);

    private static final String RATE_LIMIT_KEY_PREFIX = "rl:";
    private static final String LOCKOUT_KEY_PREFIX = "lo:";
    private static final String CONFIG_KEY_PREFIX = "rlc:";

    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

    // Fallback to in-memory storage if Redis is not available
    private final Map<String, RateLimitData> inMemoryRateLimits = new ConcurrentHashMap<>();
    private final Map<String, LockoutData> inMemoryLockouts = new ConcurrentHashMap<>();
    private final Map<String, RateLimitConfig> rateLimitConfigs = new ConcurrentHashMap<>();

    // Lua script for atomic sliding window rate limiting
    private static final String SLIDING_WINDOW_SCRIPT = 
        "local key = KEYS[1]\n" +
        "local window = tonumber(ARGV[1])\n" +
        "local limit = tonumber(ARGV[2])\n" +
        "local now = tonumber(ARGV[3])\n" +
        "local windowStart = now - window\n" +
        "\n" +
        "-- Remove expired entries\n" +
        "redis.call('ZREMRANGEBYSCORE', key, '-inf', windowStart)\n" +
        "\n" +
        "-- Count current entries\n" +
        "local current = redis.call('ZCARD', key)\n" +
        "\n" +
        "-- Check if limit exceeded\n" +
        "if current >= limit then\n" +
        "    -- Set TTL and return count\n" +
        "    redis.call('EXPIRE', key, window)\n" +
        "    return {current, 0}\n" +
        "else\n" +
        "    -- Add current request\n" +
        "    redis.call('ZADD', key, now, now)\n" +
        "    redis.call('EXPIRE', key, window)\n" +
        "    return {current + 1, 1}\n" +
        "end";

    /**
     * Data class for in-memory rate limit tracking.
     */
    private static class RateLimitData {
        final Map<Long, Integer> windows = new ConcurrentHashMap<>();
        
        synchronized int getCountInWindow(long windowStart, long windowEnd) {
            // Remove expired windows (convert to seconds for comparison)
            windows.entrySet().removeIf(entry -> entry.getKey() < windowStart / 1000);
            
            return windows.entrySet().stream()
                    .filter(entry -> entry.getKey() >= windowStart / 1000 && entry.getKey() <= windowEnd / 1000)
                    .mapToInt(Map.Entry::getValue)
                    .sum();
        }
        
        synchronized int checkAndRecord(long windowStart, long windowEnd, long timestamp, int limit) {
            // Remove expired windows (convert to seconds for comparison)
            windows.entrySet().removeIf(entry -> entry.getKey() < windowStart / 1000);
            
            int currentCount = windows.entrySet().stream()
                    .filter(entry -> entry.getKey() >= windowStart / 1000 && entry.getKey() <= windowEnd / 1000)
                    .mapToInt(Map.Entry::getValue)
                    .sum();
            
            if (currentCount < limit) {
                windows.merge(timestamp / 1000, 1, Integer::sum);
                return currentCount + 1; // Return new count after recording
            } else {
                // Don't record, return current count with negative sign to indicate denial
                return -currentCount;
            }
        }
    }

    /**
     * Data class for in-memory lockout tracking.
     */
    private static class LockoutData {
        final LocalDateTime lockoutUntil;
        final int failedAttempts;
        final String reason;
        
        LockoutData(LocalDateTime lockoutUntil, int failedAttempts, String reason) {
            this.lockoutUntil = lockoutUntil;
            this.failedAttempts = failedAttempts;
            this.reason = reason;
        }
        
        boolean isLocked() {
            return LocalDateTime.now().isBefore(lockoutUntil);
        }
        
        int getFailedAttempts() {
            return failedAttempts;
        }
    }

    @Override
    public RateLimitResult checkRateLimit(String key, int limit, Duration window) {
        logger.debug("Checking rate limit (status only) for key: {}, limit: {}, window: {}", key, limit, window);
        
        // Status check - doesn't modify counters
        return getRateLimitStatus(key, limit, window);
    }

    @Override
    public RateLimitResult recordRequest(String key, int limit, Duration window) {
        return checkAndRecord(key, limit, window);
    }

    @Override
    public RateLimitResult checkAndRecord(String key, int limit, Duration window) {
        logger.debug("Checking and recording request for key: {}, limit: {}, window: {}", key, limit, window);
        
        // First check if user is locked out
        if (isUserLockedOut(key)) {
            LockoutData lockout = getLockoutData(key);
            return RateLimitResult.locked(key, lockout.lockoutUntil, lockout.reason);
        }
        
        String rateLimitKey = RATE_LIMIT_KEY_PREFIX + key;
        LocalDateTime now = LocalDateTime.now();
        long nowSeconds = now.toEpochSecond(ZoneOffset.UTC);
        long windowSeconds = window.getSeconds();
        
        if (redisTemplate != null) {
            return checkRateLimitWithRedis(rateLimitKey, limit, windowSeconds, nowSeconds, now);
        } else {
            return checkRateLimitInMemory(key, limit, window, now);
        }
    }

    @Override
    public RateLimitResult lockoutUser(String userId, int failedAttempts) {
        logger.info("Locking out user: {}, failed attempts: {}", userId, failedAttempts);
        
        Duration lockoutDuration = calculateLockoutDuration(failedAttempts);
        LocalDateTime lockoutUntil = LocalDateTime.now().plus(lockoutDuration);
        String message = String.format("Account locked due to %d failed attempts. Try again after %s", 
                                      failedAttempts, lockoutUntil);
        
        String lockoutKey = LOCKOUT_KEY_PREFIX + userId;
        LockoutData lockoutData = new LockoutData(lockoutUntil, failedAttempts, message);
        
        if (redisTemplate != null) {
            // Store lockout in Redis
            redisTemplate.opsForValue().set(
                lockoutKey, 
                String.format("%d:%d:%s", lockoutUntil.toEpochSecond(ZoneOffset.UTC), failedAttempts, message),
                lockoutDuration
            );
        } else {
            // Store lockout in memory
            inMemoryLockouts.put(userId, lockoutData);
        }
        
        return RateLimitResult.locked(userId, lockoutUntil, message);
    }

    @Override
    public boolean isUserLockedOut(String userId) {
        LockoutData lockout = getLockoutData(userId);
        return lockout != null && lockout.isLocked();
    }

    @Override
    public void clearRateLimit(String key) {
        logger.debug("Clearing rate limit for key: {}", key);
        
        String rateLimitKey = key.startsWith(RATE_LIMIT_KEY_PREFIX) ? key : RATE_LIMIT_KEY_PREFIX + key;
        
        if (redisTemplate != null) {
            redisTemplate.delete(rateLimitKey);
        } else {
            // Remove from in-memory storage - handle both with and without prefix
            String cleanKey = key.startsWith(RATE_LIMIT_KEY_PREFIX) ? 
                            key.substring(RATE_LIMIT_KEY_PREFIX.length()) : key;
            inMemoryRateLimits.remove(cleanKey);
        }
    }

    @Override
    public void clearUserLockout(String userId) {
        logger.debug("Clearing lockout for user: {}", userId);
        
        String lockoutKey = LOCKOUT_KEY_PREFIX + userId;
        
        if (redisTemplate != null) {
            redisTemplate.delete(lockoutKey);
        } else {
            inMemoryLockouts.remove(userId);
        }
    }

    @Override
    public RateLimitResult getRateLimitStatus(String key, int limit, Duration window) {
        // Return status without incrementing counter
        if (redisTemplate != null) {
            try {
                String rateLimitKey = RATE_LIMIT_KEY_PREFIX + key;
                long windowSeconds = window.getSeconds();
                LocalDateTime now = LocalDateTime.now();
                long currentTimeMs = now.toInstant(ZoneOffset.UTC).toEpochMilli();
                
                // Just check the current count without incrementing
                Long count = redisTemplate.opsForZSet().count(rateLimitKey, 
                    currentTimeMs - (windowSeconds * 1000), currentTimeMs);
                
                int currentCount = count != null ? count.intValue() : 0;
                
                LocalDateTime windowStart = now.minusSeconds(windowSeconds);
                LocalDateTime windowEnd = now;
                Duration resetTime = Duration.between(now, windowEnd.plusSeconds(windowSeconds));
                
                if (currentCount < limit) {
                    return RateLimitResult.allowed(currentCount, limit, windowEnd, windowSeconds);
                } else {
                    return RateLimitResult.denied("Rate limit exceeded", currentCount, limit, windowEnd, resetTime.getSeconds());
                }
            } catch (Exception e) {
                logger.error("Redis rate limit status check failed for key: {}, falling back to in-memory", key, e);
                return checkRateLimitStatusInMemory(key.replace(RATE_LIMIT_KEY_PREFIX, ""), limit, window, LocalDateTime.now());
            }
        } else {
            return checkRateLimitStatusInMemory(key, limit, window, LocalDateTime.now());
        }
    }

    @Override
    public void configureRateLimit(String operation, RateLimitConfig config) {
        logger.info("Configuring rate limit for operation: {}, config: {}", operation, config);
        
        rateLimitConfigs.put(operation, config);
        
        if (redisTemplate != null) {
            // Store configuration in Redis for cluster environments
            String configKey = CONFIG_KEY_PREFIX + operation;
            String configValue = String.format("%d:%d:%s:%s", 
                                              config.getMaxOperations(), 
                                              config.getWindowSeconds(),
                                              config.isEnabled(),
                                              config.getDescription() != null ? config.getDescription() : "");
            redisTemplate.opsForValue().set(configKey, configValue, Duration.ofDays(30));
        }
    }

    @Override
    public RateLimitConfig getRateLimitConfig(String operation) {
        RateLimitConfig config = rateLimitConfigs.get(operation);
        
        if (config == null && redisTemplate != null) {
            // Try to load from Redis
            String configKey = CONFIG_KEY_PREFIX + operation;
            String configValue = redisTemplate.opsForValue().get(configKey);
            if (configValue != null) {
                config = parseConfigFromRedis(operation, configValue);
                rateLimitConfigs.put(operation, config);
            }
        }
        
        return config;
    }

    // Private helper methods

    private RateLimitResult checkRateLimitWithRedis(String key, int limit, long windowSeconds, 
                                                   long nowSeconds, LocalDateTime now) {
        try {
            @SuppressWarnings("rawtypes")
            RedisScript<List> script = RedisScript.of(SLIDING_WINDOW_SCRIPT, List.class);
            @SuppressWarnings("unchecked")
            List<Long> result = (List<Long>) redisTemplate.execute(script, 
                Arrays.asList(key), 
                String.valueOf(windowSeconds), 
                String.valueOf(limit), 
                String.valueOf(nowSeconds));
            
            int currentCount = result.get(0).intValue();
            boolean allowed = result.get(1) == 1;
            
            LocalDateTime windowStart = now.minusSeconds(windowSeconds);
            LocalDateTime windowEnd = now;
            Duration resetTime = Duration.between(now, windowEnd.plusSeconds(windowSeconds));
            
            if (allowed) {
                return RateLimitResult.allowed(currentCount, limit, windowEnd, windowSeconds);
            } else {
                return RateLimitResult.denied("Rate limit exceeded", currentCount, limit, windowEnd, resetTime.getSeconds());
            }
            
        } catch (Exception e) {
            logger.error("Redis rate limiting failed for key: {}, falling back to in-memory", key, e);
            return checkRateLimitInMemory(key.replace(RATE_LIMIT_KEY_PREFIX, ""), limit, 
                                        Duration.ofSeconds(windowSeconds), now);
        }
    }

    private RateLimitResult checkRateLimitInMemory(String key, int limit, Duration window, LocalDateTime now) {
        RateLimitData data = inMemoryRateLimits.computeIfAbsent(key, k -> new RateLimitData());
        
        long nowSeconds = now.toEpochSecond(ZoneOffset.UTC);
        long windowStart = nowSeconds - window.getSeconds();
        long windowEnd = nowSeconds;
        long timestamp = nowSeconds * 1000;
        
        // Use atomic check-and-record operation
        int resultCount = data.checkAndRecord(windowStart * 1000, windowEnd * 1000, timestamp, limit);
        
        if (resultCount > 0) {
            // Request was allowed, resultCount is the new count after recording
            long windowSecs = window.getSeconds();
            return RateLimitResult.allowed(resultCount, limit, now.plus(window), windowSecs);
        } else {
            // Request was denied, resultCount is negative current count
            long currentCount = Math.abs(resultCount);
            long windowSecs = window.getSeconds();
            return RateLimitResult.denied("Rate limit exceeded", currentCount, limit, now.plus(window), windowSecs);
        }
    }
    
    private RateLimitResult checkRateLimitStatusInMemory(String key, int limit, Duration window, LocalDateTime now) {
        RateLimitData data = inMemoryRateLimits.get(key);
        if (data == null) {
            return RateLimitResult.allowed(0, limit, now, window.getSeconds());
        }
        
        long nowSeconds = now.toEpochSecond(ZoneOffset.UTC);
        long windowStart = nowSeconds - window.getSeconds();
        
        synchronized (data) {
            int currentCount = data.getCountInWindow(windowStart * 1000, nowSeconds * 1000);
            
            if (currentCount < limit) {
                return RateLimitResult.allowed(currentCount, limit, now, window.getSeconds());
            } else {
                return RateLimitResult.denied("Rate limit exceeded", currentCount, limit, now, window.getSeconds());
            }
        }
    }

    private LockoutData getLockoutData(String userId) {
        if (redisTemplate != null) {
            String lockoutKey = LOCKOUT_KEY_PREFIX + userId;
            String lockoutValue = redisTemplate.opsForValue().get(lockoutKey);
            if (lockoutValue != null) {
                return parseLockoutFromRedis(lockoutValue);
            }
        } else {
            return inMemoryLockouts.get(userId);
        }
        return null;
    }

    private LockoutData parseLockoutFromRedis(String value) {
        String[] parts = value.split(":", 3);
        if (parts.length >= 3) {
            long lockoutUntilSeconds = Long.parseLong(parts[0]);
            int failedAttempts = Integer.parseInt(parts[1]);
            String reason = parts[2];
            
            LocalDateTime lockoutUntil = LocalDateTime.ofEpochSecond(lockoutUntilSeconds, 0, ZoneOffset.UTC);
            return new LockoutData(lockoutUntil, failedAttempts, reason);
        }
        return null;
    }

    private RateLimitConfig parseConfigFromRedis(String operation, String value) {
        String[] parts = value.split(":", 4);
        if (parts.length >= 4) {
            int limit = Integer.parseInt(parts[0]);
            long windowSeconds = Long.parseLong(parts[1]);
            boolean enableLockout = Boolean.parseBoolean(parts[2]);
            String description = parts[3];
            
            RateLimitConfig config = new RateLimitConfig(operation, limit, windowSeconds);
            config.setDescription(description);
            config.setEnabled(true);
            return config;
        }
        return null;
    }

    private Duration calculateLockoutDuration(int failedAttempts) {
        // Exponential backoff: 1min, 5min, 15min, 1hour, 24hours
        switch (failedAttempts) {
            case 1:
            case 2:
            case 3:
                return Duration.ofMinutes(1);
            case 4:
            case 5:
                return Duration.ofMinutes(5);
            case 6:
            case 7:
                return Duration.ofMinutes(15);
            case 8:
            case 9:
                return Duration.ofHours(1);
            default:
                return Duration.ofHours(24);
        }
    }

    // Initialize default configurations
    public void initializeDefaultConfigs() {
        configureRateLimit("login", RateLimitConfig.loginConfig());
        configureRateLimit("otp_send", RateLimitConfig.otpSendConfig());
        configureRateLimit("otp_verify", RateLimitConfig.otpVerifyConfig());
        configureRateLimit("password_reset", RateLimitConfig.passwordResetConfig());
        configureRateLimit("api_access", RateLimitConfig.apiAccessConfig());
        configureRateLimit("registration", RateLimitConfig.registrationConfig());
        
        logger.info("Initialized default rate limit configurations");
    }
}
