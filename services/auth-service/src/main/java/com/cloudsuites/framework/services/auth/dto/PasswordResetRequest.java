package com.cloudsuites.framework.services.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for password reset requests.
 * 
 * Contains user identification and new password information for reset operations.
 */
public class PasswordResetRequest {
    
    @Email(message = "Valid email address is required")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;
    
    private String resetToken;
    
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String newPassword;
    
    private String confirmPassword;
    
    @Size(max = 255, message = "Device ID cannot exceed 255 characters")
    private String deviceId;
    
    @Size(max = 500, message = "Device fingerprint cannot exceed 500 characters")
    private String deviceFingerprint;
    
    private boolean isInitiateRequest;
    
    // Default constructor
    public PasswordResetRequest() {
        this.isInitiateRequest = true;
    }
    
    // Constructor for initiate reset
    public PasswordResetRequest(String email) {
        this.email = email;
        this.isInitiateRequest = true;
    }
    
    // Constructor for complete reset
    public PasswordResetRequest(String email, String resetToken, String newPassword, String confirmPassword) {
        this.email = email;
        this.resetToken = resetToken;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
        this.isInitiateRequest = false;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getResetToken() {
        return resetToken;
    }
    
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
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
    
    public boolean isInitiateRequest() {
        return isInitiateRequest;
    }
    
    public void setInitiateRequest(boolean initiateRequest) {
        isInitiateRequest = initiateRequest;
    }
    
    /**
     * Validates that new password and confirmation match.
     * 
     * @return true if passwords match
     */
    public boolean isPasswordConfirmationValid() {
        if (isInitiateRequest) {
            return true; // No password validation needed for initiate request
        }
        return newPassword != null && newPassword.equals(confirmPassword);
    }
    
    /**
     * Validates that all required fields for password reset completion are present.
     * 
     * @return true if all required fields are present
     */
    public boolean isValidForCompletion() {
        return !isInitiateRequest && 
               resetToken != null && !resetToken.trim().isEmpty() &&
               newPassword != null && !newPassword.trim().isEmpty() &&
               isPasswordConfirmationValid();
    }
    
    @Override
    public String toString() {
        return "PasswordResetRequest{" +
                "email='" + email + '\'' +
                ", resetToken='[PROTECTED]'" +
                ", newPassword='[PROTECTED]'" +
                ", confirmPassword='[PROTECTED]'" +
                ", deviceId='" + deviceId + '\'' +
                ", deviceFingerprint='[PROTECTED]'" +
                ", isInitiateRequest=" + isInitiateRequest +
                '}';
    }
}
