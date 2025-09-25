package com.cloudsuites.framework.services.auth.entities;

import com.cloudsuites.framework.services.auth.AuthEventCategory;
import com.cloudsuites.framework.services.auth.AuthEventType;
import com.cloudsuites.framework.services.auth.RiskLevel;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Request for creating an audit event.
 * Contains the information needed to log security events.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class AuditEventRequest {

    /**
     * User ID associated with the event (can be null for anonymous events).
     */
    private String userId;

    /**
     * Type of authentication event.
     */
    @NotNull(message = "Event type is required")
    private AuthEventType eventType;

    /**
     * Category of the event.
     */
    @NotNull(message = "Event category is required")
    private AuthEventCategory category;

    /**
     * Risk level of the event.
     */
    @NotNull(message = "Risk level is required")
    private RiskLevel riskLevel;

    /**
     * IP address where the event occurred.
     */
    private String ipAddress;

    /**
     * User agent string.
     */
    private String userAgent;

    /**
     * Session ID if available.
     */
    private String sessionId;

    /**
     * Device fingerprint if available.
     */
    private String deviceFingerprint;

    /**
     * Geographic location if available.
     */
    private String location;

    /**
     * Additional event details.
     */
    private String details;

    /**
     * Additional metadata as key-value pairs.
     */
    private Map<String, Object> metadata;

    // Constructors
    public AuditEventRequest() {}

    public AuditEventRequest(AuthEventType eventType, AuthEventCategory category, RiskLevel riskLevel) {
        this.eventType = eventType;
        this.category = category;
        this.riskLevel = riskLevel;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public AuthEventType getEventType() { return eventType; }
    public void setEventType(AuthEventType eventType) { this.eventType = eventType; }

    public AuthEventCategory getCategory() { return category; }
    public void setCategory(AuthEventCategory category) { this.category = category; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getDeviceFingerprint() { return deviceFingerprint; }
    public void setDeviceFingerprint(String deviceFingerprint) { this.deviceFingerprint = deviceFingerprint; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
