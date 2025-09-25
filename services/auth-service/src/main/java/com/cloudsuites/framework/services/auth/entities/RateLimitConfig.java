package com.cloudsuites.framework.services.auth.entities;

/**
 * Configuration for rate limiting rules.
 * Contains the parameters that define rate limiting behavior.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class RateLimitConfig {

    /**
     * Maximum number of operations allowed in the time window.
     */
    private long maxOperations;

    /**
     * Time window duration in seconds.
     */
    private long windowSeconds;

    /**
     * Operation type or key for the rate limit.
     */
    private String operationType;

    /**
     * Whether this rate limit is enabled.
     */
    private boolean enabled;

    /**
     * Block duration in seconds when limit is exceeded.
     */
    private long blockDurationSeconds;

    /**
     * Description of this rate limit rule.
     */
    private String description;

    // Constructors
    public RateLimitConfig() {}

    public RateLimitConfig(String operationType, long maxOperations, long windowSeconds) {
        this.operationType = operationType;
        this.maxOperations = maxOperations;
        this.windowSeconds = windowSeconds;
        this.enabled = true;
        this.blockDurationSeconds = windowSeconds;
    }

    public RateLimitConfig(String operationType, long maxOperations, long windowSeconds, long blockDurationSeconds) {
        this.operationType = operationType;
        this.maxOperations = maxOperations;
        this.windowSeconds = windowSeconds;
        this.blockDurationSeconds = blockDurationSeconds;
        this.enabled = true;
    }

    // Getters and Setters
    public long getMaxOperations() { return maxOperations; }
    public void setMaxOperations(long maxOperations) { this.maxOperations = maxOperations; }

    public long getWindowSeconds() { return windowSeconds; }
    public void setWindowSeconds(long windowSeconds) { this.windowSeconds = windowSeconds; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public long getBlockDurationSeconds() { return blockDurationSeconds; }
    public void setBlockDurationSeconds(long blockDurationSeconds) { this.blockDurationSeconds = blockDurationSeconds; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Utility methods
    public double getOperationsPerSecond() {
        return windowSeconds > 0 ? (double) maxOperations / windowSeconds : 0.0;
    }

    public long getWindowMinutes() {
        return windowSeconds / 60;
    }

    public long getBlockDurationMinutes() {
        return blockDurationSeconds / 60;
    }
}
