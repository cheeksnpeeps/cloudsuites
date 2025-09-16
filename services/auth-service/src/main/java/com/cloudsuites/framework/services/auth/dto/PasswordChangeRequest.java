package com.cloudsuites.framework.services.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for password change requests.
 * 
 * Contains current password verification and new password information.
 */
public class PasswordChangeRequest {
    
    @NotBlank(message = "User ID is required")
    @Size(max = 255, message = "User ID cannot exceed 255 characters")
    private String userId;
    
    @NotBlank(message = "Current password is required")
    private String currentPassword;
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String newPassword;
    
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
    
    @Size(max = 255, message = "Device ID cannot exceed 255 characters")
    private String deviceId;
    
    @Size(max = 500, message = "Device fingerprint cannot exceed 500 characters")
    private String deviceFingerprint;
    
    // Default constructor
    public PasswordChangeRequest() {
    }
    
    // Constructor with required fields
    public PasswordChangeRequest(String userId, String currentPassword, String newPassword, String confirmPassword) {
        this.userId = userId;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getCurrentPassword() {
        return currentPassword;
    }
    
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }
    
    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }
    
    /**
     * Validates that new password and confirmation match.
     * 
     * @return true if passwords match
     */
    public boolean isPasswordConfirmationValid() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
    
    @Override
    public String toString() {
        return "PasswordChangeRequest{" +
                "userId='" + userId + '\'' +
                ", currentPassword='[PROTECTED]'" +
                ", newPassword='[PROTECTED]'" +
                ", confirmPassword='[PROTECTED]'" +
                ", deviceId='" + deviceId + '\'' +
                ", deviceFingerprint='[PROTECTED]'" +
                '}';
    }
}
