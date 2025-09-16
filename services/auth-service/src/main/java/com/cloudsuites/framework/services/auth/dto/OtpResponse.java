package com.cloudsuites.framework.services.auth.dto;

import com.cloudsuites.framework.services.auth.OtpChannel;
import java.time.LocalDateTime;

/**
 * Response DTO for OTP operations.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public class OtpResponse {

    private boolean success;
    private String message;
    private String otpId;
    private OtpChannel channel;
    private String recipient;
    private LocalDateTime expiresAt;
    private int remainingAttempts;
    private long rateLimitResetSeconds;

    /**
     * Default constructor for framework use.
     */
    public OtpResponse() {}

    /**
     * Constructor for successful OTP response.
     * 
     * @param success operation success status
     * @param message response message
     */
    public OtpResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Creates a successful OTP response.
     * 
     * @param message success message
     * @param otpId unique OTP identifier
     * @param channel delivery channel used
     * @param recipient masked recipient
     * @param expiresAt OTP expiration time
     * @return successful OtpResponse
     */
    public static OtpResponse success(String message, String otpId, OtpChannel channel, 
                                    String recipient, LocalDateTime expiresAt) {
        OtpResponse response = new OtpResponse(true, message);
        response.setOtpId(otpId);
        response.setChannel(channel);
        response.setRecipient(recipient);
        response.setExpiresAt(expiresAt);
        return response;
    }

    /**
     * Creates a failure OTP response.
     * 
     * @param message error message
     * @return failed OtpResponse
     */
    public static OtpResponse failure(String message) {
        return new OtpResponse(false, message);
    }

    /**
     * Creates a rate limited OTP response.
     * 
     * @param message rate limit message
     * @param resetSeconds seconds until rate limit resets
     * @return rate limited OtpResponse
     */
    public static OtpResponse rateLimited(String message, long resetSeconds) {
        OtpResponse response = new OtpResponse(false, message);
        response.setRateLimitResetSeconds(resetSeconds);
        return response;
    }

    /**
     * Gets the operation success status.
     * 
     * @return true if operation successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the operation success status.
     * 
     * @param success success flag
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets the response message.
     * 
     * @return user-friendly message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the response message.
     * 
     * @param message response message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the unique OTP identifier.
     * 
     * @return OTP ID for tracking
     */
    public String getOtpId() {
        return otpId;
    }

    /**
     * Sets the OTP identifier.
     * 
     * @param otpId unique OTP ID
     */
    public void setOtpId(String otpId) {
        this.otpId = otpId;
    }

    /**
     * Gets the delivery channel used.
     * 
     * @return SMS or EMAIL channel
     */
    public OtpChannel getChannel() {
        return channel;
    }

    /**
     * Sets the delivery channel.
     * 
     * @param channel OTP delivery channel
     */
    public void setChannel(OtpChannel channel) {
        this.channel = channel;
    }

    /**
     * Gets the masked recipient.
     * 
     * @return masked phone number or email
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Sets the recipient (should be masked).
     * 
     * @param recipient masked recipient
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Gets the OTP expiration time.
     * 
     * @return expiration timestamp
     */
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    /**
     * Sets the OTP expiration time.
     * 
     * @param expiresAt expiration timestamp
     */
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * Gets the remaining verification attempts.
     * 
     * @return number of attempts left
     */
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    /**
     * Sets the remaining verification attempts.
     * 
     * @param remainingAttempts attempts left
     */
    public void setRemainingAttempts(int remainingAttempts) {
        this.remainingAttempts = remainingAttempts;
    }

    /**
     * Gets the rate limit reset time in seconds.
     * 
     * @return seconds until rate limit resets
     */
    public long getRateLimitResetSeconds() {
        return rateLimitResetSeconds;
    }

    /**
     * Sets the rate limit reset time.
     * 
     * @param rateLimitResetSeconds seconds until reset
     */
    public void setRateLimitResetSeconds(long rateLimitResetSeconds) {
        this.rateLimitResetSeconds = rateLimitResetSeconds;
    }

    @Override
    public String toString() {
        return "OtpResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", otpId='" + otpId + '\'' +
                ", channel=" + channel +
                ", recipient='" + recipient + '\'' +
                ", expiresAt=" + expiresAt +
                ", remainingAttempts=" + remainingAttempts +
                ", rateLimitResetSeconds=" + rateLimitResetSeconds +
                '}';
    }
}
