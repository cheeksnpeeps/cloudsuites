package com.cloudsuites.framework.services.auth;

import com.cloudsuites.framework.services.auth.entities.DeviceFingerprint;
import com.cloudsuites.framework.services.auth.entities.DeviceRegistrationRequest;
import com.cloudsuites.framework.services.auth.entities.DeviceVerificationResult;
import com.cloudsuites.framework.services.auth.entities.TrustedDeviceTokenRequest;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing device trust relationships and fingerprinting.
 * 
 * This service provides functionality to:
 * - Register and verify trusted devices
 * - Generate and validate device fingerprints
 * - Manage device-based authentication tokens
 * - Support "keep me logged in" functionality
 * 
 * Device trust allows users to mark devices as trusted, enabling
 * extended authentication sessions and reduced MFA requirements.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public interface DeviceTrustService {

    /**
     * Register a new device as trusted for the specified user.
     * 
     * Creates a device fingerprint based on provided device characteristics
     * and establishes a trust relationship with the user account.
     * 
     * @param request Device registration details including user ID and device info
     * @return DeviceFingerprint containing the registered device information
     * @throws IllegalArgumentException if request is invalid
     * @throws SecurityException if device registration fails security checks
     */
    DeviceFingerprint registerTrustedDevice(DeviceRegistrationRequest request);

    /**
     * Verify if a device is trusted for the specified user.
     * 
     * Compares the provided device characteristics against stored
     * fingerprints to determine trust status.
     * 
     * @param userId User identifier
     * @param deviceCharacteristics Device information for verification
     * @return DeviceVerificationResult containing trust status and device info
     */
    DeviceVerificationResult verifyDeviceTrust(String userId, String deviceCharacteristics);

    /**
     * Generate a device fingerprint from device characteristics.
     * 
     * Creates a stable, unique identifier for the device based on
     * various device properties and browser/app characteristics.
     * 
     * @param deviceInfo Raw device information (user agent, screen resolution, etc.)
     * @return Unique device fingerprint string
     */
    String generateDeviceFingerprint(String deviceInfo);

    /**
     * Create an extended authentication token for a trusted device.
     * 
     * Issues a longer-lived token that can be used for subsequent
     * authentications without requiring full MFA.
     * 
     * @param request Trusted device token request details
     * @return Extended JWT token for the trusted device
     * @throws SecurityException if device is not trusted or token creation fails
     */
    String createTrustedDeviceToken(TrustedDeviceTokenRequest request);

    /**
     * Validate a trusted device token.
     * 
     * Verifies the token signature, expiration, and device trust status.
     * 
     * @param token Trusted device token to validate
     * @param deviceFingerprint Current device fingerprint for verification
     * @return true if token is valid and device is still trusted
     */
    boolean validateTrustedDeviceToken(String token, String deviceFingerprint);

    /**
     * Revoke trust for a specific device.
     * 
     * Removes the device from the user's trusted device list and
     * invalidates any associated tokens.
     * 
     * @param userId User identifier
     * @param deviceFingerprint Device fingerprint to revoke
     * @return true if device trust was successfully revoked
     */
    boolean revokeTrustedDevice(String userId, String deviceFingerprint);

    /**
     * Get all trusted devices for a user.
     * 
     * Returns a list of all devices currently trusted by the user,
     * including registration dates and last activity.
     * 
     * @param userId User identifier
     * @return List of trusted device information
     */
    List<DeviceFingerprint> getTrustedDevices(String userId);

    /**
     * Check if a device fingerprint is trusted for the user.
     * 
     * Quick lookup to determine trust status without full verification.
     * 
     * @param userId User identifier
     * @param deviceFingerprint Device fingerprint to check
     * @return Optional containing device info if trusted, empty otherwise
     */
    Optional<DeviceFingerprint> findTrustedDevice(String userId, String deviceFingerprint);

    /**
     * Update last activity timestamp for a trusted device.
     * 
     * Tracks device usage for security monitoring and cleanup purposes.
     * 
     * @param deviceFingerprint Device fingerprint to update
     */
    void updateDeviceActivity(String deviceFingerprint);

    /**
     * Clean up expired or unused trusted devices.
     * 
     * Removes devices that haven't been used within the configured
     * retention period or have explicitly expired.
     * 
     * @return Number of devices cleaned up
     */
    int cleanupExpiredDevices();
}
