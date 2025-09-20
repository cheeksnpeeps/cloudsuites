package com.cloudsuites.framework.services.auth.dto;

import com.cloudsuites.framework.services.user.entities.AuthEventCategory;
import com.cloudsuites.framework.services.user.entities.AuthEventType;
import com.cloudsuites.framework.services.user.entities.RiskLevel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for querying audit events with complex filters.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public class AuditQueryRequest {

    private List<String> userIds;
    
    private List<AuthEventType> eventTypes;
    
    private List<AuthEventCategory> eventCategories;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
    
    private List<String> ipAddresses;
    
    private List<RiskLevel> riskLevels;
    
    private Integer minRiskScore;
    
    private Integer maxRiskScore;
    
    private Boolean success;
    
    private String sessionId;
    
    private String deviceFingerprint;
    
    private boolean includeMetadata = false;

    /**
     * Default constructor.
     */
    public AuditQueryRequest() {}

    /**
     * Constructor for basic filtering.
     */
    public AuditQueryRequest(List<String> userIds, List<AuthEventType> eventTypes, 
                            LocalDateTime startDate, LocalDateTime endDate) {
        this.userIds = userIds;
        this.eventTypes = eventTypes;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<AuthEventType> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<AuthEventType> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public List<AuthEventCategory> getEventCategories() {
        return eventCategories;
    }

    public void setEventCategories(List<AuthEventCategory> eventCategories) {
        this.eventCategories = eventCategories;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<String> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public List<RiskLevel> getRiskLevels() {
        return riskLevels;
    }

    public void setRiskLevels(List<RiskLevel> riskLevels) {
        this.riskLevels = riskLevels;
    }

    public Integer getMinRiskScore() {
        return minRiskScore;
    }

    public void setMinRiskScore(Integer minRiskScore) {
        this.minRiskScore = minRiskScore;
    }

    public Integer getMaxRiskScore() {
        return maxRiskScore;
    }

    public void setMaxRiskScore(Integer maxRiskScore) {
        this.maxRiskScore = maxRiskScore;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
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

    public boolean isIncludeMetadata() {
        return includeMetadata;
    }

    public void setIncludeMetadata(boolean includeMetadata) {
        this.includeMetadata = includeMetadata;
    }

    @Override
    public String toString() {
        return "AuditQueryRequest{" +
                "userIds=" + userIds +
                ", eventTypes=" + eventTypes +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", success=" + success +
                ", includeMetadata=" + includeMetadata +
                '}';
    }
}
