package com.cloudsuites.framework.services.auth.dto;

import java.time.LocalDateTime;

/**
 * Statistics DTO for OTP usage monitoring.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public class OtpStatistics {

    private String recipient;
    private long totalOtpsSent;
    private long totalOtpsVerified;
    private long successfulVerifications;
    private long failedVerifications;
    private double successRate;
    private LocalDateTime firstOtpSent;
    private LocalDateTime lastOtpSent;
    private LocalDateTime lastSuccessfulVerification;
    private int currentRateLimitCount;
    private LocalDateTime rateLimitResetTime;

    /**
     * Default constructor for framework use.
     */
    public OtpStatistics() {}

    /**
     * Constructor for creating OTP statistics.
     * 
     * @param recipient phone number or email (can be null for global stats)
     */
    public OtpStatistics(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Gets the recipient for these statistics.
     * 
     * @return recipient identifier or null for global stats
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Sets the recipient.
     * 
     * @param recipient recipient identifier
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Gets the total number of OTPs sent.
     * 
     * @return total OTPs sent count
     */
    public long getTotalOtpsSent() {
        return totalOtpsSent;
    }

    /**
     * Sets the total OTPs sent.
     * 
     * @param totalOtpsSent OTPs sent count
     */
    public void setTotalOtpsSent(long totalOtpsSent) {
        this.totalOtpsSent = totalOtpsSent;
    }

    /**
     * Gets the total number of OTP verification attempts.
     * 
     * @return total verification attempts
     */
    public long getTotalOtpsVerified() {
        return totalOtpsVerified;
    }

    /**
     * Sets the total verification attempts.
     * 
     * @param totalOtpsVerified verification attempts count
     */
    public void setTotalOtpsVerified(long totalOtpsVerified) {
        this.totalOtpsVerified = totalOtpsVerified;
    }

    /**
     * Gets the number of successful verifications.
     * 
     * @return successful verifications count
     */
    public long getSuccessfulVerifications() {
        return successfulVerifications;
    }

    /**
     * Sets the successful verifications count.
     * 
     * @param successfulVerifications successful count
     */
    public void setSuccessfulVerifications(long successfulVerifications) {
        this.successfulVerifications = successfulVerifications;
    }

    /**
     * Gets the number of failed verifications.
     * 
     * @return failed verifications count
     */
    public long getFailedVerifications() {
        return failedVerifications;
    }

    /**
     * Sets the failed verifications count.
     * 
     * @param failedVerifications failed count
     */
    public void setFailedVerifications(long failedVerifications) {
        this.failedVerifications = failedVerifications;
    }

    /**
     * Gets the success rate as a percentage.
     * 
     * @return success rate (0.0 to 100.0)
     */
    public double getSuccessRate() {
        return successRate;
    }

    /**
     * Sets the success rate.
     * 
     * @param successRate success percentage
     */
    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    /**
     * Calculates and updates the success rate based on verification counts.
     */
    public void calculateSuccessRate() {
        if (totalOtpsVerified > 0) {
            this.successRate = (double) successfulVerifications / totalOtpsVerified * 100.0;
        } else {
            this.successRate = 0.0;
        }
    }

    /**
     * Gets the timestamp of the first OTP sent.
     * 
     * @return first OTP timestamp
     */
    public LocalDateTime getFirstOtpSent() {
        return firstOtpSent;
    }

    /**
     * Sets the first OTP timestamp.
     * 
     * @param firstOtpSent first OTP timestamp
     */
    public void setFirstOtpSent(LocalDateTime firstOtpSent) {
        this.firstOtpSent = firstOtpSent;
    }

    /**
     * Gets the timestamp of the last OTP sent.
     * 
     * @return last OTP timestamp
     */
    public LocalDateTime getLastOtpSent() {
        return lastOtpSent;
    }

    /**
     * Sets the last OTP timestamp.
     * 
     * @param lastOtpSent last OTP timestamp
     */
    public void setLastOtpSent(LocalDateTime lastOtpSent) {
        this.lastOtpSent = lastOtpSent;
    }

    /**
     * Gets the timestamp of the last successful verification.
     * 
     * @return last successful verification timestamp
     */
    public LocalDateTime getLastSuccessfulVerification() {
        return lastSuccessfulVerification;
    }

    /**
     * Sets the last successful verification timestamp.
     * 
     * @param lastSuccessfulVerification last success timestamp
     */
    public void setLastSuccessfulVerification(LocalDateTime lastSuccessfulVerification) {
        this.lastSuccessfulVerification = lastSuccessfulVerification;
    }

    /**
     * Gets the current rate limit count.
     * 
     * @return current rate limit count
     */
    public int getCurrentRateLimitCount() {
        return currentRateLimitCount;
    }

    /**
     * Sets the current rate limit count.
     * 
     * @param currentRateLimitCount rate limit count
     */
    public void setCurrentRateLimitCount(int currentRateLimitCount) {
        this.currentRateLimitCount = currentRateLimitCount;
    }

    /**
     * Gets the rate limit reset time.
     * 
     * @return rate limit reset timestamp
     */
    public LocalDateTime getRateLimitResetTime() {
        return rateLimitResetTime;
    }

    /**
     * Sets the rate limit reset time.
     * 
     * @param rateLimitResetTime reset timestamp
     */
    public void setRateLimitResetTime(LocalDateTime rateLimitResetTime) {
        this.rateLimitResetTime = rateLimitResetTime;
    }

    @Override
    public String toString() {
        return "OtpStatistics{" +
                "recipient='" + (recipient != null ? maskRecipient(recipient) : "GLOBAL") + '\'' +
                ", totalOtpsSent=" + totalOtpsSent +
                ", totalOtpsVerified=" + totalOtpsVerified +
                ", successfulVerifications=" + successfulVerifications +
                ", failedVerifications=" + failedVerifications +
                ", successRate=" + String.format("%.2f", successRate) + "%" +
                ", currentRateLimitCount=" + currentRateLimitCount +
                '}';
    }

    private String maskRecipient(String recipient) {
        if (recipient == null || recipient.length() <= 4) {
            return "****";
        }
        if (recipient.contains("@")) {
            // Email masking: first 2 chars + *** + domain
            int atIndex = recipient.indexOf("@");
            return recipient.substring(0, Math.min(2, atIndex)) + "***" + recipient.substring(atIndex);
        } else {
            // Phone masking: +1***-***-last4
            return "+***-***-" + recipient.substring(recipient.length() - 4);
        }
    }
}
