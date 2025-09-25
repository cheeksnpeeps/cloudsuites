package com.cloudsuites.framework.services.auth.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request for changing a user's password.
 * Contains current and new password information.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class PasswordChangeRequest {

    /**
     * User identifier (email or user ID).
     */
    @NotBlank(message = "User identifier is required")
    private String userIdentifier;

    /**
     * Current password for verification.
     */
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    /**
     * New password.
     */
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String newPassword;

    /**
     * Confirm new password.
     */
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    /**
     * IP address of the requester.
     */
    private String ipAddress;

    /**
     * User agent of the requester.
     */
    private String userAgent;

    // Constructors
    public PasswordChangeRequest() {}

    public PasswordChangeRequest(String userIdentifier, String currentPassword, String newPassword) {
        this.userIdentifier = userIdentifier;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = newPassword;
    }

    // Getters and Setters
    public String getUserIdentifier() { return userIdentifier; }
    public void setUserIdentifier(String userIdentifier) { this.userIdentifier = userIdentifier; }

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    // Utility methods
    public boolean passwordsMatch() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}
