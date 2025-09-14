package com.cloudsuites.framework.services.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Base authentication request DTO.
 * 
 * This DTO serves as a foundation for authentication-related requests
 * and contains common fields used across different authentication methods.
 */
public class AuthenticationRequest {
    
    @NotBlank(message = "User identifier is required")
    @Size(max = 255, message = "User identifier cannot exceed 255 characters")
    private String userIdentifier;
    
    @NotBlank(message = "Authentication method is required")
    private String authenticationMethod;
    
    @Size(max = 255, message = "Device ID cannot exceed 255 characters")
    private String deviceId;
    
    @Size(max = 500, message = "Device fingerprint cannot exceed 500 characters")
    private String deviceFingerprint;
    
    // Default constructor
    public AuthenticationRequest() {
    }
    
    // Constructor with required fields
    public AuthenticationRequest(String userIdentifier, String authenticationMethod) {
        this.userIdentifier = userIdentifier;
        this.authenticationMethod = authenticationMethod;
    }
    
    // Getters and Setters
    public String getUserIdentifier() {
        return userIdentifier;
    }
    
    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }
    
    public String getAuthenticationMethod() {
        return authenticationMethod;
    }
    
    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
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
    
    @Override
    public String toString() {
        return "AuthenticationRequest{" +
                "userIdentifier='" + userIdentifier + '\'' +
                ", authenticationMethod='" + authenticationMethod + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", deviceFingerprint='[PROTECTED]'" +
                '}';
    }
}
