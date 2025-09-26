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
     * First OTP sent timestamp.
     */
    private LocalDateTime firstSentAt;

    /**
     * Last successful verification timestamp.
     */
    private LocalDateTime lastVerifiedAt;

    /**
     * Current rate limit count.
     */
    private int currentRateLimitCount;

    /**
     * Rate limit reset time.
     */
    private LocalDateTime rateLimitResetTime;

    /**
     * Successful verifications count.
     */
    private long successfulVerifications;

    /**
     * Failed verifications count.
     */
    private long failedVerifications;

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

    public LocalDateTime getFirstSentAt() { return firstSentAt; }
    public void setFirstSentAt(LocalDateTime firstSentAt) { this.firstSentAt = firstSentAt; }

    public LocalDateTime getFirstOtpSent() { return firstSentAt; }
    public void setFirstOtpSent(LocalDateTime firstSentAt) { this.firstSentAt = firstSentAt; }

    public LocalDateTime getLastOtpSent() { return lastSentAt; }
    public void setLastOtpSent(LocalDateTime lastSentAt) { this.lastSentAt = lastSentAt; }

    public int getCurrentRateLimitCount() { return currentRateLimitCount; }
    public void setCurrentRateLimitCount(int currentRateLimitCount) { this.currentRateLimitCount = currentRateLimitCount; }

    public LocalDateTime getRateLimitResetTime() { return rateLimitResetTime; }
    public void setRateLimitResetTime(LocalDateTime rateLimitResetTime) { this.rateLimitResetTime = rateLimitResetTime; }

    public long getSuccessfulVerifications() { return successfulVerifications; }
    public void setSuccessfulVerifications(long successfulVerifications) { this.successfulVerifications = successfulVerifications; }

    public long getFailedVerifications() { return failedVerifications; }
    public void setFailedVerifications(long failedVerifications) { this.failedVerifications = failedVerifications; }

    public long getTotalOtpsSent() { return totalSent; }
    public void setTotalOtpsSent(long totalSent) { this.totalSent = totalSent; }

    public long getTotalOtpsVerified() { return totalVerified; }
    public void setTotalOtpsVerified(long totalVerified) { this.totalVerified = totalVerified; }

    // Utility methods
    public double getSuccessRate() {
        return totalSent > 0 ? (double) totalVerified / totalSent : 0.0;
    }

    public double calculateSuccessRate() {
        return getSuccessRate();
    }

    public double getFailureRate() {
        return totalSent > 0 ? (double) totalFailed / totalSent : 0.0;
    }
}
