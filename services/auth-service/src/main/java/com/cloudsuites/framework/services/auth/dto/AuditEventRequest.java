package com.cloudsuites.framework.services.auth.dto;

import com.cloudsuites.framework.services.user.entities.AuthEventCategory;
import com.cloudsuites.framework.services.user.entities.AuthEventType;
import com.cloudsuites.framework.services.user.entities.AuthenticationMethod;
import com.cloudsuites.framework.services.user.entities.RiskLevel;
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
    private AuthEventCategory eventCategory;

    @NotNull(message = "Event description is required")
    @Size(max = 1000, message = "Event description must not exceed 1000 characters")
    private String eventDescription;

    private String userId;

    @Size(max = 45, message = "IP address must not exceed 45 characters")
    private String ipAddress;

    @Size(max = 1000, message = "User agent must not exceed 1000 characters")
    private String userAgent;

    @Size(max = 500, message = "Request path must not exceed 500 characters")
    private String requestPath;

    @Size(max = 10, message = "HTTP method must not exceed 10 characters")
    private String httpMethod;

    private AuthenticationMethod authenticationMethod;

    private String sessionId;

    @Size(max = 500, message = "Device fingerprint must not exceed 500 characters")
    private String deviceFingerprint;

    private RiskLevel riskLevel;

    private Integer riskScore;

    private Boolean success;

    private Map<String, Object> metadata;

    /**
     * Default constructor.
     */
    public AuditEventRequest() {}

    /**
     * Constructor for basic audit event.
     * 
     * @param eventType the type of event
     * @param eventCategory the category of event
     * @param eventDescription description of the event
     * @param userId user identifier (may be null)
     * @param ipAddress client IP address
     * @param userAgent client user agent
     */
    public AuditEventRequest(AuthEventType eventType, AuthEventCategory eventCategory, 
                            String eventDescription, String userId, String ipAddress, String userAgent) {
        this.eventType = eventType;
        this.eventCategory = eventCategory;
        this.eventDescription = eventDescription;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.riskLevel = RiskLevel.LOW;
        this.riskScore = 0;
        this.success = true;
    }

    // Getters and Setters

    public AuthEventType getEventType() {
        return eventType;
    }

    public void setEventType(AuthEventType eventType) {
        this.eventType = eventType;
    }

    public AuthEventCategory getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(AuthEventCategory eventCategory) {
        this.eventCategory = eventCategory;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
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

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public AuthenticationMethod getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(AuthenticationMethod authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "AuditEventRequest{" +
                "eventType=" + eventType +
                ", eventCategory=" + eventCategory +
                ", eventDescription='" + eventDescription + '\'' +
                ", userId='" + userId + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", riskLevel=" + riskLevel +
                ", success=" + success +
                '}';
    }
}
