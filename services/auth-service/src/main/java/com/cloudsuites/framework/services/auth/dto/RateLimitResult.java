package com.cloudsuites.framework.services.auth.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Result of a rate limiting operation.
 * Contains current state, remaining quota, and timing information.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public class RateLimitResult {

    @NotNull
    private final String key;

    @Min(0)
    private final int currentCount;

    @Min(0)
    private final int limit;

    @Min(0)
    private final int remaining;

    @NotNull
    private final boolean allowed;

    @NotNull
    private final LocalDateTime windowStart;

    @NotNull
    private final LocalDateTime windowEnd;

    private final Duration resetTime;

    private final boolean locked;

    private final LocalDateTime lockoutUntil;

    private final String message;

    /**
     * Constructor for successful rate limit check.
     */
    public RateLimitResult(String key, int currentCount, int limit, int remaining,
                          boolean allowed, LocalDateTime windowStart, LocalDateTime windowEnd,
                          Duration resetTime) {
        this(key, currentCount, limit, remaining, allowed, windowStart, windowEnd,
             resetTime, false, null, null);
    }

    /**
     * Constructor with lockout information.
     */
    public RateLimitResult(String key, int currentCount, int limit, int remaining,
                          boolean allowed, LocalDateTime windowStart, LocalDateTime windowEnd,
                          Duration resetTime, boolean locked, LocalDateTime lockoutUntil,
                          String message) {
        this.key = key;
        this.currentCount = currentCount;
        this.limit = limit;
        this.remaining = remaining;
        this.allowed = allowed;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.resetTime = resetTime;
        this.locked = locked;
        this.lockoutUntil = lockoutUntil;
        this.message = message;
    }

    // Getters
    public String getKey() {
        return key;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public int getLimit() {
        return limit;
    }

    public int getRemaining() {
        return remaining;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public LocalDateTime getWindowStart() {
        return windowStart;
    }

    public LocalDateTime getWindowEnd() {
        return windowEnd;
    }

    public Duration getResetTime() {
        return resetTime;
    }

    public boolean isLocked() {
        return locked;
    }

    public LocalDateTime getLockoutUntil() {
        return lockoutUntil;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Convenience method to check if rate limit exceeded.
     */
    public boolean isRateLimitExceeded() {
        return !allowed && !locked;
    }

    /**
     * Convenience method to check if account is locked out.
     */
    public boolean isAccountLocked() {
        return locked;
    }

    /**
     * Gets seconds until rate limit resets.
     */
    public long getSecondsUntilReset() {
        return resetTime != null ? resetTime.getSeconds() : 0;
    }

    // Static factory methods for common scenarios

    /**
     * Creates a result for an allowed request.
     */
    public static RateLimitResult allowed(String key, int currentCount, int limit,
                                        LocalDateTime windowStart, LocalDateTime windowEnd,
                                        Duration resetTime) {
        int remaining = Math.max(0, limit - currentCount);
        return new RateLimitResult(key, currentCount, limit, remaining, true,
                                 windowStart, windowEnd, resetTime);
    }

    /**
     * Creates a result for a denied request due to rate limiting.
     */
    public static RateLimitResult denied(String key, int currentCount, int limit,
                                       LocalDateTime windowStart, LocalDateTime windowEnd,
                                       Duration resetTime) {
        return new RateLimitResult(key, currentCount, limit, 0, false,
                                 windowStart, windowEnd, resetTime);
    }

    /**
     * Creates a result for a locked account.
     */
    public static RateLimitResult locked(String key, LocalDateTime lockoutUntil, String message) {
        LocalDateTime now = LocalDateTime.now();
        return new RateLimitResult(key, 0, 0, 0, false, now, now,
                                 Duration.ZERO, true, lockoutUntil, message);
    }

    /**
     * Creates a result for no rate limiting (unlimited).
     */
    public static RateLimitResult unlimited(String key) {
        LocalDateTime now = LocalDateTime.now();
        return new RateLimitResult(key, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, true,
                                 now, now.plusYears(1), Duration.ZERO);
    }

    @Override
    public String toString() {
        if (locked) {
            return String.format("RateLimitResult{key='%s', locked=true, lockoutUntil=%s, message='%s'}",
                               key, lockoutUntil, message);
        }
        return String.format("RateLimitResult{key='%s', allowed=%s, currentCount=%d, limit=%d, remaining=%d, resetTime=%s}",
                           key, allowed, currentCount, limit, remaining, resetTime);
    }
}
