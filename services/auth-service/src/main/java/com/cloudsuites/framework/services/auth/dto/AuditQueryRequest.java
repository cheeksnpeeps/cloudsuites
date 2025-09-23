package com.cloudsuites.framework.services.auth.dto;

import com.cloudsuites.framework.services.auth.AuthEventCategory;
import com.cloudsuites.framework.services.auth.AuthEventType;
import com.cloudsuites.framework.services.auth.RiskLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for querying audit events.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public class AuditQueryRequest {

    private String userId;
    private List<AuthEventType> eventTypes;
    private List<AuthEventCategory> categories;
    private List<RiskLevel> riskLevels;
    private String ipAddress;
    private String sessionId;
    private Boolean success;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Min(value = 0, message = "Page number must be 0 or greater")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    private Integer size = 20;

    private String sortBy = "timestamp";
    private String sortDirection = "DESC";

    /**
     * Default constructor.
     */
    public AuditQueryRequest() {}

    /**
     * Constructor with basic filters.
     */
    public AuditQueryRequest(String userId, LocalDateTime startTime, LocalDateTime endTime) {
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<AuthEventType> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<AuthEventType> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public List<AuthEventCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<AuthEventCategory> categories) {
        this.categories = categories;
    }

    public List<RiskLevel> getRiskLevels() {
        return riskLevels;
    }

    public void setRiskLevels(List<RiskLevel> riskLevels) {
        this.riskLevels = riskLevels;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    @Override
    public String toString() {
        return "AuditQueryRequest{" +
                "userId='" + userId + '\'' +
                ", eventTypes=" + eventTypes +
                ", categories=" + categories +
                ", riskLevels=" + riskLevels +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}
