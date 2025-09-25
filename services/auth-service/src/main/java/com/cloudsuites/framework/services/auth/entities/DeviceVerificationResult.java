package com.cloudsuites.framework.services.auth.entities;

/**
 * Result of device trust verification.
 * Contains information about whether a device is trusted and related details.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class DeviceVerificationResult {

    /**
     * Whether the device is currently trusted.
     */
    private boolean trusted;

    /**
     * Whether this is a new device registration.
     */
    private boolean newDevice;

    /**
     * Current trust status of the device.
     */
    private TrustStatus trustStatus;

    /**
     * Device information if found.
     */
    private DeviceFingerprint deviceInfo;

    /**
     * Additional message about the verification result.
     */
    private String message;

    /**
     * Whether additional verification is required.
     */
    private boolean requiresVerification;

    // Constructors
    public DeviceVerificationResult() {}

    public DeviceVerificationResult(boolean trusted, TrustStatus trustStatus) {
        this.trusted = trusted;
        this.trustStatus = trustStatus;
    }

    // Static factory methods
    public static DeviceVerificationResult trusted(DeviceFingerprint deviceInfo) {
        DeviceVerificationResult result = new DeviceVerificationResult();
        result.trusted = true;
        result.trustStatus = TrustStatus.TRUSTED;
        result.deviceInfo = deviceInfo;
        result.message = "Device is trusted";
        return result;
    }

    public static DeviceVerificationResult untrusted(String reason) {
        DeviceVerificationResult result = new DeviceVerificationResult();
        result.trusted = false;
        result.trustStatus = TrustStatus.PENDING;
        result.message = reason;
        result.requiresVerification = true;
        return result;
    }

    public static DeviceVerificationResult expired(DeviceFingerprint deviceInfo) {
        DeviceVerificationResult result = new DeviceVerificationResult();
        result.trusted = false;
        result.trustStatus = TrustStatus.EXPIRED;
        result.deviceInfo = deviceInfo;
        result.message = "Device trust has expired";
        result.requiresVerification = true;
        return result;
    }

    public static DeviceVerificationResult newDevice() {
        DeviceVerificationResult result = new DeviceVerificationResult();
        result.trusted = false;
        result.newDevice = true;
        result.trustStatus = TrustStatus.PENDING;
        result.message = "New device detected";
        result.requiresVerification = true;
        return result;
    }

    // Getters and Setters
    public boolean isTrusted() { return trusted; }
    public void setTrusted(boolean trusted) { this.trusted = trusted; }

    public boolean isNewDevice() { return newDevice; }
    public void setNewDevice(boolean newDevice) { this.newDevice = newDevice; }

    public TrustStatus getTrustStatus() { return trustStatus; }
    public void setTrustStatus(TrustStatus trustStatus) { this.trustStatus = trustStatus; }

    public DeviceFingerprint getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(DeviceFingerprint deviceInfo) { this.deviceInfo = deviceInfo; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRequiresVerification() { return requiresVerification; }
    public void setRequiresVerification(boolean requiresVerification) { this.requiresVerification = requiresVerification; }
}
