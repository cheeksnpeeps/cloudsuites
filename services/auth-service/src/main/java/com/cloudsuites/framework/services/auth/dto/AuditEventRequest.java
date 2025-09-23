package com.cloudsuites.framework.services.auth.dto;

import com.cloudsuites.framework.services.auth.AuthEventCategory;
import com.cloudsuites.framework.services.auth.AuthEventType;
import com.cloudsuites.framework.services.auth.RiskLevel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * Request DTO for creating audit events.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public class AuditEventRequest {

    @NotNull(message = "Event type is required")
    private AuthEventType eventType;

    @NotNull(message = "Event category is required")
    private AuthEventCategory category;

    private String userId;

    @Size(max = 45, message = "IP address must not exceed 45 characters")
    private String ipAddress;

    @Size(max = 500, message = "User agent must not exceed 500 characters")
    private String userAgent;

    private String sessionId;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private Boolean success;

    @Size(max = 500, message = "Failure reason must not exceed 500 characters")
    private String failureReason;

    @Size(max = 100, message = "Geolocation must not exceed 100 characters")
    private String geolocation;

    @Size(max = 50, message = "Device type must not exceed 50 characters")
    private String deviceType;

    private Map<String, Object> metadata;

    private RiskLevel riskLevel;

    /**
     * Default constructor.
     */
    public AuditEventRequest() {}

    /**
     * Constructor for basic audit event.
     */
    public AuditEventRequest(AuthEventType eventType, AuthEventCategory category, 
                            String userId, String ipAddress, String description) {
        this.eventType = eventType;
        this.category = category;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.description = description;
    }

    // Getters and setters
    public AuthEventType getEventType() {
        return eventType;
    }

    public void setEventType(AuthEventType eventType) {
        this.eventType = eventType;
    }

    public AuthEventCategory getCategory() {
        return category;
    }

    public void setCategory(AuthEventCategory category) {
        this.category = category;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(String geolocation) {
        this.geolocation = geolocation;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    @Override
    public String toString() {
        return "AuditEventRequest{" +
                "eventType=" + eventType +
                ", category=" + category +
                ", userId='" + userId + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", description='" + description + '\'' +
                ", success=" + success +
                '}';
    }
}
