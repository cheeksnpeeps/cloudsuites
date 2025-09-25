package com.cloudsuites.framework.services.auth.entities;

import java.time.LocalDateTime;

/**
 * Result of a rate limiting check.
 * Contains information about whether an operation is allowed and rate limit status.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class RateLimitResult {

    /**
     * Whether the operation is allowed.
     */
    private boolean allowed;

    /**
     * Reason for rate limiting (if not allowed).
     */
    private String reason;

    /**
     * Current count of operations in the time window.
     */
    private long currentCount;

    /**
     * Maximum allowed operations in the time window.
     */
    private long maxAllowed;

    /**
     * Remaining operations allowed in the current window.
     */
    private long remaining;

    /**
     * When the rate limit window resets.
     */
    private LocalDateTime resetAt;

    /**
     * Time to wait before retrying (in seconds).
     */
    private Long retryAfterSeconds;

    /**
     * Rate limit window duration in seconds.
     */
    private long windowSeconds;

    // Constructors
    public RateLimitResult() {}

    public RateLimitResult(boolean allowed) {
        this.allowed = allowed;
    }

    // Static factory methods
    public static RateLimitResult allowed(long currentCount, long maxAllowed, LocalDateTime resetAt, long windowSeconds) {
        RateLimitResult result = new RateLimitResult(true);
        result.currentCount = currentCount;
        result.maxAllowed = maxAllowed;
        result.remaining = maxAllowed - currentCount;
        result.resetAt = resetAt;
        result.windowSeconds = windowSeconds;
        return result;
    }

    public static RateLimitResult denied(String reason, long currentCount, long maxAllowed, LocalDateTime resetAt, long retryAfterSeconds) {
        RateLimitResult result = new RateLimitResult(false);
        result.reason = reason;
        result.currentCount = currentCount;
        result.maxAllowed = maxAllowed;
        result.remaining = 0;
        result.resetAt = resetAt;
        result.retryAfterSeconds = retryAfterSeconds;
        return result;
    }

    // Getters and Setters
    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public long getCurrentCount() { return currentCount; }
    public void setCurrentCount(long currentCount) { this.currentCount = currentCount; }

    public long getMaxAllowed() { return maxAllowed; }
    public void setMaxAllowed(long maxAllowed) { this.maxAllowed = maxAllowed; }

    public long getRemaining() { return remaining; }
    public void setRemaining(long remaining) { this.remaining = remaining; }

    public LocalDateTime getResetAt() { return resetAt; }
    public void setResetAt(LocalDateTime resetAt) { this.resetAt = resetAt; }

    public Long getRetryAfterSeconds() { return retryAfterSeconds; }
    public void setRetryAfterSeconds(Long retryAfterSeconds) { this.retryAfterSeconds = retryAfterSeconds; }

    public long getWindowSeconds() { return windowSeconds; }
    public void setWindowSeconds(long windowSeconds) { this.windowSeconds = windowSeconds; }
}
