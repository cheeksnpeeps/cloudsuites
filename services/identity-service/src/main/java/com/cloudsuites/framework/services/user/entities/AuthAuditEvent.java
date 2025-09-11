package com.cloudsuites.framework.services.user.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Authentication Audit Event entity for comprehensive security logging.
 * Maps to the auth_audit_events table created in V4 migration.
 * Tracks all authentication-related events for security monitoring and compliance.
 */
@Data
@Entity
@Table(name = "auth_audit_events", indexes = {
    @Index(name = "idx_auth_audit_user_id", columnList = "user_id"),
    @Index(name = "idx_auth_audit_event_type", columnList = "event_type"),
    @Index(name = "idx_auth_audit_created_at", columnList = "created_at"),
    @Index(name = "idx_auth_audit_risk_level", columnList = "risk_level"),
    @Index(name = "idx_auth_audit_ip_address", columnList = "ip_address"),
    @Index(name = "idx_auth_audit_session_id", columnList = "session_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthAuditEvent {

    private static final Logger logger = LoggerFactory.getLogger(AuthAuditEvent.class);

    @Id
    @Column(name = "audit_id", unique = true, nullable = false)
    private String auditId;

    @Size(max = 255, message = "User ID must not exceed 255 characters")
    @Column(name = "user_id")
    private String userId;

    @NotNull(message = "Event type is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private AuthEventType eventType;

    @NotNull(message = "Event category is mandatory")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "event_category", nullable = false, length = 30)
    private AuthEventCategory eventCategory = AuthEventCategory.AUTHENTICATION;

    @NotNull(message = "Event description is mandatory")
    @Size(max = 1000, message = "Event description must not exceed 1000 characters")
    @Column(name = "event_description", nullable = false, columnDefinition = "TEXT")
    private String eventDescription;

    @NotNull(message = "Risk level is mandatory")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel = RiskLevel.LOW;

    @Builder.Default
    @Min(value = 0, message = "Risk score cannot be negative")
    @Max(value = 100, message = "Risk score cannot exceed 100")
    @Column(name = "risk_score", nullable = false)
    private Integer riskScore = 0;

    // Request context
    @Column(name = "ip_address")
    private String ipAddress;

    @Size(max = 1000, message = "User agent must not exceed 1000 characters")
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Size(max = 500, message = "Request path must not exceed 500 characters")
    @Column(name = "request_path", length = 500)
    private String requestPath;

    @Size(max = 10, message = "HTTP method must not exceed 10 characters")
    @Column(name = "http_method", length = 10)
    private String httpMethod;

    // Authentication context
    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_method", length = 30)
    private AuthenticationMethod authenticationMethod;

    @Size(max = 255, message = "Session ID must not exceed 255 characters")
    @Column(name = "session_id")
    private String sessionId;

    @Size(max = 500, message = "Device fingerprint must not exceed 500 characters")
    @Column(name = "device_fingerprint", length = 500)
    private String deviceFingerprint;

    // Geolocation context
    @Size(max = 3, message = "Country code must not exceed 3 characters")
    @Column(name = "country_code", length = 3)
    private String countryCode;

    @Size(max = 100, message = "Region must not exceed 100 characters")
    @Column(name = "region", length = 100)
    private String region;

    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(name = "city", length = 100)
    private String city;

    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    @Column(name = "timezone", length = 50)
    private String timezone;

    // Additional context
    @Column(name = "additional_data")
    private String additionalData; // JSON as string

    @Size(max = 200, message = "Failure reason must not exceed 200 characters")
    @Column(name = "failure_reason", length = 200)
    private String failureReason;

    @Size(max = 255, message = "Affected resource must not exceed 255 characters")
    @Column(name = "affected_resource")
    private String affectedResource;

    // Metadata
    @Size(max = 255, message = "Event ID must not exceed 255 characters")
    @Column(name = "event_id")
    private String eventId;

    @Size(max = 255, message = "Correlation ID must not exceed 255 characters")
    @Column(name = "correlation_id")
    private String correlationId;

    @Size(max = 255, message = "Trace ID must not exceed 255 characters")
    @Column(name = "trace_id")
    private String traceId;

    // Audit trail
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime createdAt;

    @Builder.Default
    @Size(max = 255, message = "Recorded by must not exceed 255 characters")
    @Column(name = "recorded_by")
    private String recordedBy = "SYSTEM";

    // ============================================================================
    // LIFECYCLE CALLBACKS
    // ============================================================================

    @PrePersist
    protected void onCreate() {
        this.auditId = IdGenerator.generateULID("AUD-");
        this.createdAt = LocalDateTime.now();
        
        // Set default correlation ID if not provided
        if (this.correlationId == null) {
            this.correlationId = IdGenerator.generateULID("COR-");
        }
        
        logger.debug("Created audit event: {} for user: {} - {}", 
                     this.auditId, this.userId, this.eventType);
    }

    // ============================================================================
    // BUSINESS METHODS
    // ============================================================================

    /**
     * Checks if this audit event represents a security incident.
     */
    public boolean isSecurityIncident() {
        return riskLevel == RiskLevel.HIGH || riskLevel == RiskLevel.CRITICAL 
            || riskScore >= 80;
    }

    /**
     * Checks if this audit event represents a successful authentication.
     */
    public boolean isSuccessfulAuth() {
        return eventType == AuthEventType.LOGIN_SUCCESS 
            || eventType == AuthEventType.OTP_VERIFY_SUCCESS;
    }

    /**
     * Checks if this audit event represents a failed authentication.
     */
    public boolean isFailedAuth() {
        return eventType == AuthEventType.LOGIN_FAILURE 
            || eventType == AuthEventType.OTP_VERIFY_FAILURE;
    }

    /**
     * Gets a summary description for logging and notifications.
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(eventType.name()).append(" for ");
        
        if (userId != null) {
            sb.append("user ").append(userId);
        } else {
            sb.append("unknown user");
        }
        
        if (ipAddress != null) {
            sb.append(" from IP ").append(ipAddress);
        }
        
        if (riskLevel != RiskLevel.LOW) {
            sb.append(" (").append(riskLevel).append(" risk)");
        }
        
        return sb.toString();
    }

    /**
     * Determines if this event should trigger an alert.
     */
    public boolean shouldTriggerAlert() {
        return riskLevel == RiskLevel.CRITICAL 
            || (riskLevel == RiskLevel.HIGH && riskScore >= 90)
            || eventType == AuthEventType.SUSPICIOUS_ACTIVITY
            || eventType == AuthEventType.ACCOUNT_LOCKED;
    }

    /**
     * Creates a builder for login success events.
     */
    public static AuthAuditEventBuilder loginSuccess(String userId, String ipAddress) {
        return AuthAuditEvent.builder()
            .userId(userId)
            .eventType(AuthEventType.LOGIN_SUCCESS)
            .eventCategory(AuthEventCategory.AUTHENTICATION)
            .eventDescription("User successfully logged in")
            .riskLevel(RiskLevel.LOW)
            .ipAddress(ipAddress);
    }

    /**
     * Creates a builder for login failure events.
     */
    public static AuthAuditEventBuilder loginFailure(String userId, String ipAddress, String reason) {
        return AuthAuditEvent.builder()
            .userId(userId)
            .eventType(AuthEventType.LOGIN_FAILURE)
            .eventCategory(AuthEventCategory.AUTHENTICATION)
            .eventDescription("Login attempt failed: " + reason)
            .riskLevel(RiskLevel.MEDIUM)
            .riskScore(50)
            .ipAddress(ipAddress)
            .failureReason(reason);
    }

    /**
     * Creates a builder for suspicious activity events.
     */
    public static AuthAuditEventBuilder suspiciousActivity(String userId, String ipAddress, String description) {
        return AuthAuditEvent.builder()
            .userId(userId)
            .eventType(AuthEventType.SUSPICIOUS_ACTIVITY)
            .eventCategory(AuthEventCategory.SECURITY)
            .eventDescription("Suspicious activity detected: " + description)
            .riskLevel(RiskLevel.HIGH)
            .riskScore(85)
            .ipAddress(ipAddress);
    }

    /**
     * Creates a builder for OTP verification events.
     */
    public static AuthAuditEventBuilder otpVerification(String userId, String ipAddress, boolean success) {
        AuthEventType eventType = success ? AuthEventType.OTP_VERIFY_SUCCESS : AuthEventType.OTP_VERIFY_FAILURE;
        RiskLevel riskLevel = success ? RiskLevel.LOW : RiskLevel.MEDIUM;
        String description = success ? "OTP verification successful" : "OTP verification failed";
        
        return AuthAuditEvent.builder()
            .userId(userId)
            .eventType(eventType)
            .eventCategory(AuthEventCategory.AUTHENTICATION)
            .eventDescription(description)
            .riskLevel(riskLevel)
            .riskScore(success ? 10 : 40)
            .ipAddress(ipAddress);
    }
}
