package com.cloudsuites.framework.services.auth.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a device fingerprint for trust management.
 * 
 * This entity stores device identification information and trust relationships
 * to support "keep me logged in" and trusted device functionality.
 * 
 * The device fingerprint is generated from various device characteristics
 * and used to identify returning devices for enhanced authentication experience.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
@Entity
@Table(name = "device_fingerprints", 
       indexes = {
           @Index(name = "idx_device_user_fingerprint", 
                  columnList = "user_id, fingerprint", unique = true),
           @Index(name = "idx_device_user_trust_status", 
                  columnList = "user_id, trust_status"),
           @Index(name = "idx_device_fingerprint", 
                  columnList = "fingerprint"),
           @Index(name = "idx_device_expires_at", 
                  columnList = "expires_at"),
           @Index(name = "idx_device_last_used", 
                  columnList = "last_used_at")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceFingerprint {

    /**
     * Unique identifier for the device fingerprint record.
     */
    @Id
    @Column(name = "device_id", length = 36)
    private String deviceId;

    /**
     * User ID associated with this trusted device.
     */
    @NotBlank(message = "User ID is required")
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    /**
     * Device fingerprint hash - unique identifier for the device.
     * Generated from device characteristics like user agent, screen resolution, etc.
     */
    @NotBlank(message = "Device fingerprint is required")
    @Size(min = 32, max = 128, message = "Device fingerprint must be between 32 and 128 characters")
    @Column(name = "fingerprint", nullable = false, length = 128)
    private String fingerprint;

    /**
     * Human-readable device name for user recognition.
     */
    @Size(max = 100, message = "Device name cannot exceed 100 characters")
    @Column(name = "device_name", length = 100)
    private String deviceName;

    /**
     * Device type classification.
     */
    @NotNull(message = "Device type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false, length = 20)
    private DeviceType deviceType;

    /**
     * Operating system information.
     */
    @Size(max = 50, message = "OS info cannot exceed 50 characters")
    @Column(name = "os_info", length = 50)
    private String osInfo;

    /**
     * Browser or application information.
     */
    @Size(max = 100, message = "Browser info cannot exceed 100 characters")
    @Column(name = "browser_info", length = 100)
    private String browserInfo;

    /**
     * IP address when device was first registered.
     */
    @Column(name = "registration_ip", length = 45)
    private String registrationIp;

    /**
     * Current trust status of the device.
     */
    @NotNull(message = "Trust status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "trust_status", nullable = false, length = 20)
    @Builder.Default
    private TrustStatus trustStatus = TrustStatus.PENDING;

    /**
     * When this device was first registered as trusted.
     */
    @CreationTimestamp
    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    /**
     * Last time this device was used for authentication.
     */
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    /**
     * When the device trust expires (if applicable).
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * When this record was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Additional metadata about the device stored as JSON.
     * Can include screen resolution, timezone, language preferences, etc.
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    /**
     * Risk score associated with this device (0-100).
     * Used for security assessment and anomaly detection.
     */
    @Column(name = "risk_score")
    @Builder.Default
    private Integer riskScore = 0;

    /**
     * Number of times this device has been used for authentication.
     * Helps establish usage patterns and device legitimacy.
     */
    @Column(name = "usage_count")
    @Builder.Default
    private Long usageCount = 0L;

    /**
     * Whether this device supports biometric authentication.
     */
    @Column(name = "biometric_capable")
    @Builder.Default
    private Boolean biometricCapable = false;

    /**
     * User agent string from the device's browser/app.
     */
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /**
     * Last known IP address for this device.
     */
    @Column(name = "last_ip_address", length = 45)
    private String lastIpAddress;

    /**
     * Geolocation information for security tracking.
     */
    @Column(name = "last_location", length = 100)
    private String lastLocation;

    /**
     * Who created this device trust record.
     */
    @Column(name = "created_by", length = 36)
    private String createdBy;

    /**
     * Who last modified this device trust record.
     */
    @Column(name = "last_modified_by", length = 36)
    private String lastModifiedBy;

    /**
     * Soft delete flag for maintaining audit trail.
     */
    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    /**
     * When the device trust was revoked (if applicable).
     */
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    /**
     * Reason for revoking device trust.
     */
    @Column(name = "revocation_reason", length = 200)
    private String revocationReason;

    /**
     * Updates the last used timestamp and usage count.
     */
    public void updateActivity() {
        this.lastUsedAt = LocalDateTime.now();
        this.usageCount = this.usageCount != null ? this.usageCount + 1 : 1L;
    }

    /**
     * Revokes trust for this device.
     */
    public void revoke(String reason) {
        this.trustStatus = TrustStatus.REVOKED;
        this.revokedAt = LocalDateTime.now();
        this.revocationReason = reason;
    }

    /**
     * Checks if the device trust has expired.
     */
    public boolean isExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }

    /**
     * Checks if the device is currently trusted (not expired, revoked, or suspended).
     */
    public boolean isTrusted() {
        return this.trustStatus == TrustStatus.TRUSTED && 
               !isExpired() && 
               !Boolean.TRUE.equals(this.isDeleted);
    }

    /**
     * Pre-persist callback to generate device ID if not set.
     */
    @PrePersist
    public void generateDeviceId() {
        if (this.deviceId == null) {
            this.deviceId = java.util.UUID.randomUUID().toString();
        }
    }
}
