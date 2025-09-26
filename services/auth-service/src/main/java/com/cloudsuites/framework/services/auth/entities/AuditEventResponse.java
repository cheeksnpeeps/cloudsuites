package com.cloudsuites.framework.services.auth.entities;

import com.cloudsuites.framework.services.auth.AuthEventCategory;
import com.cloudsuites.framework.services.auth.AuthEventType;
import com.cloudsuites.framework.services.auth.RiskLevel;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response for audit event operations.
 * Simple response containing the created event information.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class AuditEventResponse {

    /**
     * Event ID.
     */
    private String eventId;

    /**
     * Event type.
     */
    private AuthEventType eventType;

    /**
     * Event category.
     */
    private AuthEventCategory category;

    /**
     * User ID associated with the event.
     */
    private String userId;

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
     * Event description.
     */
    private String description;

    /**
     * Failure reason if applicable.
     */
    private String failureReason;

    /**
     * Geographic location information.
     */
    private String geolocation;

    /**
     * Device type information.
     */
    private String deviceType;

    /**
     * Additional metadata.
     */
    private Map<String, Object> metadata;

    /**
     * Risk level.
     */
    private RiskLevel riskLevel;

    /**
     * Event timestamp.
     */
    private LocalDateTime timestamp;

    /**
     * Success indicator.
     */
    private boolean success;

    /**
     * Response message.
     */
    private String message;

    // Constructors
    public AuditEventResponse() {}

    public AuditEventResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Static factory methods
    public static AuditEventResponse success(String eventId) {
        AuditEventResponse response = new AuditEventResponse(true, "Event created successfully");
        response.eventId = eventId;
        return response;
    }

    public static AuditEventResponse failure(String message) {
        return new AuditEventResponse(false, message);
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public AuthEventType getEventType() { return eventType; }
    public void setEventType(AuthEventType eventType) { this.eventType = eventType; }

    public AuthEventCategory getCategory() { return category; }
    public void setCategory(AuthEventCategory category) { this.category = category; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public String getGeolocation() { return geolocation; }
    public void setGeolocation(String geolocation) { this.geolocation = geolocation; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
