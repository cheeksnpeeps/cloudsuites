package com.cloudsuites.framework.services.auth.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request for registering a new trusted device.
 * Contains the minimum information needed to establish device trust.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class DeviceRegistrationRequest {

    /**
     * User ID requesting device registration.
     */
    @NotBlank(message = "User ID is required")
    private String userId;

    /**
     * Device fingerprint hash for identification.
     */
    @NotBlank(message = "Device fingerprint is required") 
    @Size(min = 32, max = 128, message = "Device fingerprint must be between 32 and 128 characters")
    private String deviceFingerprint;

    /**
     * Human-readable device name.
     */
    @Size(max = 100, message = "Device name cannot exceed 100 characters")
    private String deviceName;

    /**
     * Device type classification.
     */
    @NotNull(message = "Device type is required")
    private DeviceType deviceType;

    /**
     * Operating system information.
     */
    @Size(max = 50, message = "OS info cannot exceed 50 characters")
    private String osInfo;

    /**
     * Browser or application information.
     */
    @Size(max = 100, message = "Browser info cannot exceed 100 characters")
    private String browserInfo;

    /**
     * IP address for registration.
     */
    private String ipAddress;

    /**
     * User agent string.
     */
    private String userAgent;

    /**
     * Additional metadata as JSON string.
     */
    private String metadata;

    /**
     * Device information string (concatenated browser + OS info).
     */
    private String deviceInfo;

    /**
     * Trust expiration days.
     */
    private Integer trustExpirationDays;

    /**
     * Whether to trust this device.
     */
    private Boolean trustDevice;

    /**
     * Whether biometric authentication is supported.
     */
    private Boolean biometricSupported;

    /**
     * Device characteristics for fingerprinting.
     */
    private String deviceCharacteristics;

    // Constructors
    public DeviceRegistrationRequest() {}

    public DeviceRegistrationRequest(String userId, String deviceFingerprint, String deviceName, DeviceType deviceType) {
        this.userId = userId;
        this.deviceFingerprint = deviceFingerprint;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDeviceFingerprint() { return deviceFingerprint; }
    public void setDeviceFingerprint(String deviceFingerprint) { this.deviceFingerprint = deviceFingerprint; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public DeviceType getDeviceType() { return deviceType; }
    public void setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; }

    public String getOsInfo() { return osInfo; }
    public void setOsInfo(String osInfo) { this.osInfo = osInfo; }

    public String getBrowserInfo() { return browserInfo; }
    public void setBrowserInfo(String browserInfo) { this.browserInfo = browserInfo; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }

    public Integer getTrustExpirationDays() { return trustExpirationDays; }
    public void setTrustExpirationDays(Integer trustExpirationDays) { this.trustExpirationDays = trustExpirationDays; }

    public Boolean getTrustDevice() { return trustDevice; }
    public void setTrustDevice(Boolean trustDevice) { this.trustDevice = trustDevice; }

    public Boolean getBiometricSupported() { return biometricSupported; }
    public void setBiometricSupported(Boolean biometricSupported) { this.biometricSupported = biometricSupported; }

    public String getDeviceCharacteristics() { return deviceCharacteristics; }
    public void setDeviceCharacteristics(String deviceCharacteristics) { this.deviceCharacteristics = deviceCharacteristics; }
}
