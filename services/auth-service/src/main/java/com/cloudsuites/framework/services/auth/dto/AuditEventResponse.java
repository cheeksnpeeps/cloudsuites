package com.cloudsuites.framework.services.auth.dto;

import com.cloudsuites.framework.services.user.entities.AuthEventCategory;
import com.cloudsuites.framework.services.user.entities.AuthEventType;
import com.cloudsuites.framework.services.user.entities.AuthenticationMethod;
import com.cloudsuites.framework.services.user.entities.RiskLevel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for audit events.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public class AuditEventResponse {

    private String auditId;
    
    private String userId;
    
    private AuthEventType eventType;
    
    private AuthEventCategory eventCategory;
    
    private String eventDescription;
    
    private RiskLevel riskLevel;
    
    private Integer riskScore;
    
    private String ipAddress;
    
    private String userAgent;
    
    private String requestPath;
    
    private String httpMethod;
    
    private AuthenticationMethod authenticationMethod;
    
    private String sessionId;
    
    private String deviceFingerprint;
    
    private Boolean success;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private Map<String, Object> metadata;

    /**
     * Default constructor.
     */
    public AuditEventResponse() {}

    /**
     * Constructor with basic fields.
     */
    public AuditEventResponse(String auditId, String userId, AuthEventType eventType, 
                             AuthEventCategory eventCategory, String eventDescription,
                             Boolean success, LocalDateTime timestamp) {
        this.auditId = auditId;
        this.userId = userId;
        this.eventType = eventType;
        this.eventCategory = eventCategory;
        this.eventDescription = eventDescription;
        this.success = success;
        this.timestamp = timestamp;
    }

    // Getters and Setters

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "AuditEventResponse{" +
                "auditId='" + auditId + '\'' +
                ", userId='" + userId + '\'' +
                ", eventType=" + eventType +
                ", eventCategory=" + eventCategory +
                ", eventDescription='" + eventDescription + '\'' +
                ", success=" + success +
                ", timestamp=" + timestamp +
                '}';
    }
}