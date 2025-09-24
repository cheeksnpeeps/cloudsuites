package com.cloudsuites.framework.services.auth;

import com.cloudsuites.framework.services.auth.dto.RateLimitResult;
import com.cloudsuites.framework.services.auth.dto.RateLimitConfig;

import java.time.Duration;

/**
 * Redis-based rate limiting service for authentication operations.
 * Implements sliding window rate limiting with account lockout functionality.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public interface RateLimitService {

    /**
     * Checks if a rate limit is exceeded for a given key.
     * Uses sliding window algorithm for accurate rate limiting.
     * 
     * @param key unique identifier (e.g., "auth:login:user@example.com", "otp:send:+1234567890")
     * @param limit maximum number of requests allowed
     * @param window time window for the rate limit
     * @return RateLimitResult with current count, remaining, and reset time
     */
    RateLimitResult checkRateLimit(String key, int limit, Duration window);

    /**
     * Records a request for rate limiting purposes.
     * Increments the counter for the given key within the time window.
     * 
     * @param key unique identifier for the operation
     * @param limit maximum number of requests allowed
     * @param window time window for the rate limit
     * @return RateLimitResult with updated count and remaining requests
     */
    RateLimitResult recordRequest(String key, int limit, Duration window);

    /**
     * Checks and records a request atomically.
     * Preferred method for most use cases as it combines check and record operations.
     * 
     * @param key unique identifier for the operation
     * @param limit maximum number of requests allowed  
     * @param window time window for the rate limit
     * @return RateLimitResult indicating if request is allowed and current state
     */
    RateLimitResult checkAndRecord(String key, int limit, Duration window);

    /**
     * Implements account lockout logic for failed authentication attempts.
     * Uses exponential backoff: 1min, 5min, 15min, 1hour, 24hours.
     * 
     * @param userId user identifier
     * @param failedAttempts current number of failed attempts
     * @return RateLimitResult with lockout status and unlock time
     */
    RateLimitResult lockoutUser(String userId, int failedAttempts);

    /**
     * Checks if a user account is currently locked out.
     * 
     * @param userId user identifier
     * @return true if account is locked, false otherwise
     */
    boolean isUserLockedOut(String userId);

    /**
     * Clears rate limit data for a specific key.
     * Used when resetting after successful authentication.
     * 
     * @param key unique identifier to clear
     */
    void clearRateLimit(String key);

    /**
     * Clears all lockout data for a user.
     * Used after successful authentication to reset failed attempt counters.
     * 
     * @param userId user identifier
     */
    void clearUserLockout(String userId);

    /**
     * Gets current rate limit status without modifying counters.
     * 
     * @param key unique identifier
     * @param limit maximum number of requests allowed
     * @param window time window for the rate limit
     * @return RateLimitResult with current status
     */
    RateLimitResult getRateLimitStatus(String key, int limit, Duration window);

    /**
     * Configures custom rate limiting rules for specific operations.
     * 
     * @param operation operation name (e.g., "login", "otp_send", "password_reset")
     * @param config rate limit configuration
     */
    void configureRateLimit(String operation, RateLimitConfig config);

    /**
     * Gets the configuration for a specific operation.
     * 
     * @param operation operation name
     * @return RateLimitConfig or null if not configured
     */
    RateLimitConfig getRateLimitConfig(String operation);
}
