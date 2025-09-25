package com.cloudsuites.framework.services.auth.entities;

import com.cloudsuites.framework.services.auth.AuthEventCategory;
import com.cloudsuites.framework.services.auth.AuthEventType;
import com.cloudsuites.framework.services.auth.RiskLevel;

import java.time.LocalDateTime;

/**
 * Request for querying audit events.
 * Contains filtering and pagination parameters.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class AuditQueryRequest {

    /**
     * Filter by user ID.
     */
    private String userId;

    /**
     * Filter by event type.
     */
    private AuthEventType eventType;

    /**
     * Filter by event category.
     */
    private AuthEventCategory category;

    /**
     * Filter by risk level.
     */
    private RiskLevel riskLevel;

    /**
     * Filter by IP address.
     */
    private String ipAddress;

    /**
     * Start date for time range filtering.
     */
    private LocalDateTime startDate;

    /**
     * End date for time range filtering.
     */
    private LocalDateTime endDate;

    /**
     * Page number for pagination (0-based).
     */
    private int page = 0;

    /**
     * Page size for pagination.
     */
    private int size = 20;

    /**
     * Sort field.
     */
    private String sortBy = "timestamp";

    /**
     * Sort direction (ASC or DESC).
     */
    private String sortDirection = "DESC";

    // Constructors
    public AuditQueryRequest() {}

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

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
}
