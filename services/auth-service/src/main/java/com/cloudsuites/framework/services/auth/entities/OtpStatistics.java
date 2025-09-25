package com.cloudsuites.framework.services.auth.entities;

import java.time.LocalDateTime;

/**
 * Statistics for OTP operations.
 * Contains metrics and usage information for OTP services.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class OtpStatistics {

    /**
     * User identifier.
     */
    private String userIdentifier;

    /**
     * Total OTPs sent.
     */
    private long totalSent;

    /**
     * Total OTPs verified successfully.
     */
    private long totalVerified;

    /**
     * Total failed verification attempts.
     */
    private long totalFailed;

    /**
     * OTPs sent in the last hour.
     */
    private long sentLastHour;

    /**
     * OTPs sent today.
     */
    private long sentToday;

    /**
     * Last OTP sent timestamp.
     */
    private LocalDateTime lastSentAt;

    /**
     * Last successful verification timestamp.
     */
    private LocalDateTime lastVerifiedAt;

    /**
     * Whether user is currently rate limited.
     */
    private boolean rateLimited;

    /**
     * When rate limit will be reset.
     */
    private LocalDateTime rateLimitResetAt;

    // Constructors
    public OtpStatistics() {}

    public OtpStatistics(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    // Getters and Setters
    public String getUserIdentifier() { return userIdentifier; }
    public void setUserIdentifier(String userIdentifier) { this.userIdentifier = userIdentifier; }

    public long getTotalSent() { return totalSent; }
    public void setTotalSent(long totalSent) { this.totalSent = totalSent; }

    public long getTotalVerified() { return totalVerified; }
    public void setTotalVerified(long totalVerified) { this.totalVerified = totalVerified; }

    public long getTotalFailed() { return totalFailed; }
    public void setTotalFailed(long totalFailed) { this.totalFailed = totalFailed; }

    public long getSentLastHour() { return sentLastHour; }
    public void setSentLastHour(long sentLastHour) { this.sentLastHour = sentLastHour; }

    public long getSentToday() { return sentToday; }
    public void setSentToday(long sentToday) { this.sentToday = sentToday; }

    public LocalDateTime getLastSentAt() { return lastSentAt; }
    public void setLastSentAt(LocalDateTime lastSentAt) { this.lastSentAt = lastSentAt; }

    public LocalDateTime getLastVerifiedAt() { return lastVerifiedAt; }
    public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) { this.lastVerifiedAt = lastVerifiedAt; }

    public boolean isRateLimited() { return rateLimited; }
    public void setRateLimited(boolean rateLimited) { this.rateLimited = rateLimited; }

    public LocalDateTime getRateLimitResetAt() { return rateLimitResetAt; }
    public void setRateLimitResetAt(LocalDateTime rateLimitResetAt) { this.rateLimitResetAt = rateLimitResetAt; }

    // Utility methods
    public double getSuccessRate() {
        return totalSent > 0 ? (double) totalVerified / totalSent : 0.0;
    }

    public double getFailureRate() {
        return totalSent > 0 ? (double) totalFailed / totalSent : 0.0;
    }
}
