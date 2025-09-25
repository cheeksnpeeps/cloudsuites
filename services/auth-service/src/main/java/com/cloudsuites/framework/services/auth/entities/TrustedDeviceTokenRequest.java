package com.cloudsuites.framework.services.auth.entities;

import jakarta.validation.constraints.NotBlank;

/**
 * Request for creating a trusted device token.
 * Used to generate authentication tokens for trusted devices.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class TrustedDeviceTokenRequest {

    /**
     * User ID requesting the token.
     */
    @NotBlank(message = "User ID is required")
    private String userId;

    /**
     * Device fingerprint for the trusted device.
     */
    @NotBlank(message = "Device fingerprint is required")
    private String deviceFingerprint;

    /**
     * Requested token validity duration in seconds.
     * If not specified, uses system default.
     */
    private Long validitySeconds;

    /**
     * Additional scopes or permissions for the token.
     */
    private String[] scopes;

    // Constructors
    public TrustedDeviceTokenRequest() {}

    public TrustedDeviceTokenRequest(String userId, String deviceFingerprint) {
        this.userId = userId;
        this.deviceFingerprint = deviceFingerprint;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDeviceFingerprint() { return deviceFingerprint; }
    public void setDeviceFingerprint(String deviceFingerprint) { this.deviceFingerprint = deviceFingerprint; }

    public Long getValiditySeconds() { return validitySeconds; }
    public void setValiditySeconds(Long validitySeconds) { this.validitySeconds = validitySeconds; }

    public String[] getScopes() { return scopes; }
    public void setScopes(String[] scopes) { this.scopes = scopes; }
}
