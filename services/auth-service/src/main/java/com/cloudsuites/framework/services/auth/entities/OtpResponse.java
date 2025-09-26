package com.cloudsuites.framework.services.auth.entities;

import com.cloudsuites.framework.services.auth.OtpChannel;

import java.time.LocalDateTime;

/**
 * Response for OTP operations.
 * Contains the result of OTP generation or verification.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class OtpResponse {

    /**
     * Whether the operation was successful.
     */
    private boolean success;

    /**
     * Response message.
     */
    private String message;

    /**
     * Channel used for OTP delivery.
     */
    private OtpChannel channel;

    /**
     * Masked destination (e.g., "user@exa***.com" or "+1****5678").
     */
    private String maskedDestination;

    /**
     * When the OTP expires.
     */
    private LocalDateTime expiresAt;

    /**
     * Remaining attempts before lockout.
     */
    private Integer remainingAttempts;

    /**
     * Time until next attempt is allowed.
     */
    private LocalDateTime nextAttemptAt;

    // Constructors
    public OtpResponse() {}

    public OtpResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Static factory methods
    public static OtpResponse success(String message, OtpChannel channel, String maskedDestination, LocalDateTime expiresAt) {
        OtpResponse response = new OtpResponse(true, message);
        response.channel = channel;
        response.maskedDestination = maskedDestination;
        response.expiresAt = expiresAt;
        return response;
    }

    public static OtpResponse success(String message, String otpId, OtpChannel channel, String maskedDestination, LocalDateTime expiresAt) {
        OtpResponse response = new OtpResponse(true, message);
        response.channel = channel;
        response.maskedDestination = maskedDestination;
        response.expiresAt = expiresAt;
        // Note: otpId is not stored in response for security reasons
        return response;
    }

    public static OtpResponse failure(String message) {
        return new OtpResponse(false, message);
    }

    public static OtpResponse rateLimited(String message, LocalDateTime nextAttemptAt) {
        OtpResponse response = new OtpResponse(false, message);
        response.nextAttemptAt = nextAttemptAt;
        return response;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public OtpChannel getChannel() { return channel; }
    public void setChannel(OtpChannel channel) { this.channel = channel; }

    public String getMaskedDestination() { return maskedDestination; }
    public void setMaskedDestination(String maskedDestination) { this.maskedDestination = maskedDestination; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Integer getRemainingAttempts() { return remainingAttempts; }
    public void setRemainingAttempts(Integer remainingAttempts) { this.remainingAttempts = remainingAttempts; }

    public LocalDateTime getNextAttemptAt() { return nextAttemptAt; }
    public void setNextAttemptAt(LocalDateTime nextAttemptAt) { this.nextAttemptAt = nextAttemptAt; }
}
