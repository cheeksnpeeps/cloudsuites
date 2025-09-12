package com.cloudsuites.framework.services.user.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * User Session entity for managing refresh tokens and user sessions across devices.
 * Maps to the user_sessions table created in V3 migration.
 * Implements refresh token rotation and device trust management.
 */
@Data
@Entity
@Table(name = "user_sessions", indexes = {
    @Index(name = "idx_user_sessions_user_id", columnList = "user_id"),
    @Index(name = "idx_user_sessions_active", columnList = "user_id, is_active"),
    @Index(name = "idx_user_sessions_expires_at", columnList = "expires_at"),
    @Index(name = "idx_user_sessions_last_activity", columnList = "last_activity_at"),
    @Index(name = "idx_user_sessions_device_fingerprint", columnList = "device_fingerprint"),
    @Index(name = "idx_user_sessions_refresh_token_hash", columnList = "refresh_token_hash")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {

    private static final Logger logger = LoggerFactory.getLogger(UserSession.class);

    @Id
    @Column(name = "session_id", unique = true, nullable = false)
    private String sessionId;

    @NotNull(message = "User ID is mandatory")
    @Size(max = 255, message = "User ID must not exceed 255 characters")
    @Column(name = "user_id", nullable = false)
    private String userId;

    @NotNull(message = "Refresh token hash is mandatory")
    @JsonIgnore // Never expose refresh token hash
    @Size(max = 255, message = "Refresh token hash must not exceed 255 characters")
    @Column(name = "refresh_token_hash", nullable = false)
    private String refreshTokenHash;

    @Size(max = 255, message = "Access token JTI must not exceed 255 characters")
    @Column(name = "access_token_jti")
    private String accessTokenJti;

    @Size(max = 500, message = "Device fingerprint must not exceed 500 characters")
    @Column(name = "device_fingerprint", length = 500)
    private String deviceFingerprint;

    @Size(max = 200, message = "Device name must not exceed 200 characters")
    @Column(name = "device_name", length = 200)
    private String deviceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", length = 50)
    private DeviceType deviceType;

    @Size(max = 1000, message = "User agent must not exceed 1000 characters")
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "ip_address")
    private String ipAddress;

    @Size(max = 200, message = "Location must not exceed 200 characters")
    @Column(name = "location", length = 200)
    private String location;

    @Builder.Default
    @Column(name = "is_trusted_device", nullable = false)
    private Boolean isTrustedDevice = false;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @NotNull(message = "Last activity time is mandatory")
    @Column(name = "last_activity_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime lastActivityAt;

    @NotNull(message = "Expiration time is mandatory")
    @Column(name = "expires_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime createdAt;

    @Size(max = 255, message = "Created by must not exceed 255 characters")
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_modified_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime lastModifiedAt;

    @Size(max = 255, message = "Last modified by must not exceed 255 characters")
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    // ============================================================================
    // LIFECYCLE CALLBACKS
    // ============================================================================

    @PrePersist
    protected void onCreate() {
        this.sessionId = IdGenerator.generateULID("SES-");
        this.createdAt = LocalDateTime.now();
        this.lastActivityAt = LocalDateTime.now();
        
        // Validate expiration time
        if (this.expiresAt != null && this.expiresAt.isBefore(this.createdAt)) {
            throw new IllegalArgumentException("Expiration time must be after creation time");
        }
        
        logger.debug("Created session: {} for user: {} on device: {}", 
                     this.sessionId, this.userId, this.deviceType);
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedAt = LocalDateTime.now();
    }

    // ============================================================================
    // BUSINESS METHODS
    // ============================================================================

    /**
     * Checks if the session is still valid (active and not expired).
     */
    public boolean isValid() {
        return isActive && expiresAt.isAfter(LocalDateTime.now());
    }

    /**
     * Checks if the session has expired.
     */
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    /**
     * Updates the last activity timestamp to current time.
     */
    public void updateLastActivity() {
        this.lastActivityAt = LocalDateTime.now();
        logger.debug("Updated last activity for session: {}", this.sessionId);
    }

    /**
     * Deactivates the session.
     */
    public void deactivate() {
        this.isActive = false;
        this.lastModifiedAt = LocalDateTime.now();
        logger.debug("Deactivated session: {}", this.sessionId);
    }

    /**
     * Marks the device as trusted.
     */
    public void trustDevice() {
        this.isTrustedDevice = true;
        this.lastModifiedAt = LocalDateTime.now();
        logger.debug("Marked device as trusted for session: {}", this.sessionId);
    }

    /**
     * Removes device trust.
     */
    public void untrustDevice() {
        this.isTrustedDevice = false;
        this.lastModifiedAt = LocalDateTime.now();
        logger.debug("Removed device trust for session: {}", this.sessionId);
    }

    /**
     * Updates the refresh token hash (for token rotation).
     */
    public void rotateRefreshToken(String newTokenHash) {
        if (newTokenHash == null || newTokenHash.trim().isEmpty()) {
            throw new IllegalArgumentException("New refresh token hash cannot be null or empty");
        }
        
        String oldTokenHash = this.refreshTokenHash;
        this.refreshTokenHash = newTokenHash;
        this.lastModifiedAt = LocalDateTime.now();
        
        logger.debug("Rotated refresh token for session: {} (old hash: {}...)", 
                     this.sessionId, oldTokenHash.substring(0, Math.min(8, oldTokenHash.length())));
    }

    /**
     * Extends the session expiration time.
     */
    public void extendExpiration(LocalDateTime newExpirationTime) {
        if (newExpirationTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("New expiration time must be in the future");
        }
        
        this.expiresAt = newExpirationTime;
        this.lastModifiedAt = LocalDateTime.now();
        logger.debug("Extended session {} expiration to: {}", this.sessionId, this.expiresAt);
    }

    /**
     * Updates session metadata (IP, user agent, location).
     */
    public void updateSessionMetadata(String ipAddress, String userAgent, String location) {
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.location = location;
        this.lastActivityAt = LocalDateTime.now();
        this.lastModifiedAt = LocalDateTime.now();
        
        logger.debug("Updated metadata for session: {} from IP: {}", this.sessionId, ipAddress);
    }

    /**
     * Gets a display-friendly device description.
     */
    public String getDeviceDescription() {
        StringBuilder sb = new StringBuilder();
        
        if (deviceName != null && !deviceName.trim().isEmpty()) {
            sb.append(deviceName);
        } else if (deviceType != null) {
            sb.append(deviceType.getDisplayName());
        } else {
            sb.append("Unknown Device");
        }
        
        if (location != null && !location.trim().isEmpty()) {
            sb.append(" (").append(location).append(")");
        }
        
        return sb.toString();
    }

    /**
     * Creates a builder for web sessions.
     */
    public static UserSessionBuilder webSession(String userId, String refreshTokenHash, int expirationHours) {
        return UserSession.builder()
            .userId(userId)
            .refreshTokenHash(refreshTokenHash)
            .deviceType(DeviceType.WEB)
            .expiresAt(LocalDateTime.now().plusHours(expirationHours));
    }

    /**
     * Creates a builder for mobile sessions.
     */
    public static UserSessionBuilder mobileSession(String userId, String refreshTokenHash, DeviceType deviceType, int expirationDays) {
        if (deviceType != DeviceType.MOBILE_IOS && deviceType != DeviceType.MOBILE_ANDROID) {
            throw new IllegalArgumentException("Device type must be MOBILE_IOS or MOBILE_ANDROID");
        }
        
        return UserSession.builder()
            .userId(userId)
            .refreshTokenHash(refreshTokenHash)
            .deviceType(deviceType)
            .expiresAt(LocalDateTime.now().plusDays(expirationDays));
    }
}
