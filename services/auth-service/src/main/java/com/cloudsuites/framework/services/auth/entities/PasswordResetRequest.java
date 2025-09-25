package com.cloudsuites.framework.services.auth.entities;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request for resetting a user's password.
 * Contains user identification for password reset process.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class PasswordResetRequest {

    /**
     * User's email address for password reset.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * Reset token (if this is a token-based reset).
     */
    private String resetToken;

    /**
     * New password (if this is the reset completion step).
     */
    private String newPassword;

    /**
     * Confirm new password.
     */
    private String confirmPassword;

    /**
     * IP address of the requester.
     */
    private String ipAddress;

    /**
     * User agent of the requester.
     */
    private String userAgent;

    /**
     * Callback URL for reset completion.
     */
    private String callbackUrl;

    // Constructors
    public PasswordResetRequest() {}

    public PasswordResetRequest(String email) {
        this.email = email;
    }

    public PasswordResetRequest(String email, String resetToken, String newPassword) {
        this.email = email;
        this.resetToken = resetToken;
        this.newPassword = newPassword;
        this.confirmPassword = newPassword;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }

    // Utility methods
    public boolean isResetInitiation() {
        return resetToken == null && newPassword == null;
    }

    public boolean isResetCompletion() {
        return resetToken != null && newPassword != null;
    }

    public boolean passwordsMatch() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}
