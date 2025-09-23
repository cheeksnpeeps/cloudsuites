package com.cloudsuites.framework.modules.auth.repository;

import com.cloudsuites.framework.modules.auth.entity.AuditEvent;
import com.cloudsuites.framework.services.auth.AuthEventType;
import com.cloudsuites.framework.services.auth.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for AuditEvent entities.
 * 
 * Provides comprehensive data access methods for authentication audit logging
 * including user activity tracking, security incident monitoring, and
 * compliance audit trail management.
 * 
 * This repository supports:
 * - Event logging and retrieval
 * - User-specific audit trails
 * - Security pattern analysis
 * - Risk assessment queries
 * - Compliance reporting
 * 
 * @author CloudSuites Platform
 * @since 1.0
 */
@Repository
public interface AuthAuditEventRepository extends JpaRepository<AuditEvent, String>,
        JpaSpecificationExecutor<AuditEvent> {    /**
     * Find audit events by user ID with pagination and sorting.
     * 
     * @param userId the user identifier
     * @param pageable pagination and sorting parameters
     * @return page of audit events for the user
     */
    Page<AuditEvent> findByUserId(String userId, Pageable pageable);

    /**
     * Find audit events by user ID and event type after a specific timestamp.
     * Results are ordered by timestamp in descending order.
     * 
     * @param userId the user identifier
     * @param eventType the type of authentication event
     * @param timestamp the cutoff timestamp
     * @return list of audit events matching criteria
     */
    List<AuditEvent> findByUserIdAndEventTypeAndTimestampAfterOrderByTimestampDesc(
            String userId, AuthEventType eventType, LocalDateTime timestamp);

    /**
     * Find audit events with risk level greater than or equal to specified level
     * after a specific timestamp. Results are ordered by timestamp in descending order.
     * 
     * @param riskLevel the minimum risk level
     * @param timestamp the cutoff timestamp
     * @return list of high-risk audit events
     */
    List<AuditEvent> findByRiskLevelGreaterThanEqualAndTimestampAfterOrderByTimestampDesc(
            RiskLevel riskLevel, LocalDateTime timestamp);

    /**
     * Find audit events by IP address after a specific timestamp.
     * Results are ordered by timestamp in descending order.
     * 
     * @param ipAddress the IP address
     * @param timestamp the cutoff timestamp
     * @return list of audit events from the IP address
     */
    List<AuditEvent> findByIpAddressAndTimestampAfterOrderByTimestampDesc(
            String ipAddress, LocalDateTime timestamp);

    /**
     * Count audit events by event type within a time range.
     * 
     * @param eventType the type of authentication event
     * @param startTime the start of the time range
     * @param endTime the end of the time range
     * @return count of events matching criteria
     */
    long countByEventTypeAndTimestampBetween(AuthEventType eventType, 
                                           LocalDateTime startTime, 
                                           LocalDateTime endTime);

    /**
     * Count audit events by user ID and event type after a specific timestamp.
     * Useful for tracking recent activity patterns.
     * 
     * @param userId the user identifier
     * @param eventType the type of authentication event
     * @param timestamp the cutoff timestamp
     * @return count of events matching criteria
     */
    long countByUserIdAndEventTypeAndTimestampAfter(String userId, 
                                                   AuthEventType eventType, 
                                                   LocalDateTime timestamp);

    /**
     * Count audit events by user ID, IP address, and event type.
     * Useful for detecting familiar vs unfamiliar IP addresses.
     * 
     * @param userId the user identifier
     * @param ipAddress the IP address
     * @param eventType the type of authentication event
     * @return count of events matching criteria
     */
    long countByUserIdAndIpAddressAndEventType(String userId, 
                                             String ipAddress, 
                                             AuthEventType eventType);

    /**
     * Get comprehensive event statistics by event type within a time range.
     * Returns aggregated counts grouped by event type.
     * 
     * @param startTime the start of the time range
     * @param endTime the end of the time range
     * @return list of [AuthEventType, count] arrays
     */
    @Query("SELECT e.eventType, COUNT(e) " +
           "FROM AuditEvent e " +
           "WHERE e.timestamp BETWEEN :startTime AND :endTime " +
           "GROUP BY e.eventType " +
           "ORDER BY COUNT(e) DESC")
    List<Object[]> getEventStatistics(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);

    /**
     * Find recent failed login attempts for a user within specified hours.
     * Optimized query for security monitoring and rate limiting.
     * 
     * @param userId the user identifier
     * @param hours number of hours to look back
     * @return list of recent failed login events
     */
    @Query("SELECT e FROM AuditEvent e " +
           "WHERE e.userId = :userId " +
           "AND e.eventType = 'LOGIN_FAILURE' " +
           "AND e.timestamp >= :cutoffTime " +
           "ORDER BY e.timestamp DESC")
    List<AuditEvent> findRecentFailedLogins(@Param("userId") String userId,
                                               @Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Find suspicious activities across all users within specified time range.
     * Focuses on high-risk events for security monitoring.
     * 
     * @param minimumRiskLevel the minimum risk level to include
     * @param cutoffTime the earliest timestamp to include
     * @return list of suspicious audit events
     */
    @Query("SELECT e FROM AuditEvent e " +
           "WHERE e.riskLevel >= :minimumRiskLevel " +
           "AND e.timestamp >= :cutoffTime " +
           "ORDER BY e.riskLevel DESC, e.timestamp DESC")
    List<AuditEvent> findSuspiciousActivities(@Param("minimumRiskLevel") RiskLevel minimumRiskLevel,
                                                 @Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Find events by session ID for session-based audit trails.
     * 
     * @param sessionId the session identifier
     * @return list of audit events for the session
     */
    List<AuditEvent> findBySessionIdOrderByTimestampAsc(String sessionId);

    /**
     * Get daily login statistics for a user within a date range.
     * Useful for user activity analysis and reporting.
     * 
     * @param userId the user identifier
     * @param startTime the start of the time range
     * @param endTime the end of the time range
     * @return list of [date, successful_logins, failed_logins] arrays
     */
    @Query("SELECT DATE(e.timestamp) as loginDate, " +
           "       SUM(CASE WHEN e.eventType = 'LOGIN_SUCCESS' THEN 1 ELSE 0 END) as successfulLogins, " +
           "       SUM(CASE WHEN e.eventType = 'LOGIN_FAILURE' THEN 1 ELSE 0 END) as failedLogins " +
           "FROM AuditEvent e " +
           "WHERE e.userId = :userId " +
           "AND e.timestamp BETWEEN :startTime AND :endTime " +
           "AND e.eventType IN ('LOGIN_SUCCESS', 'LOGIN_FAILURE') " +
           "GROUP BY DATE(e.timestamp) " +
           "ORDER BY DATE(e.timestamp) DESC")
    List<Object[]> getDailyLoginStatistics(@Param("userId") String userId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * Find events by multiple event types within a time range.
     * Flexible query for various audit reporting needs.
     * 
     * @param eventTypes list of event types to include
     * @param startTime the start of the time range
     * @param endTime the end of the time range
     * @param pageable pagination and sorting parameters
     * @return page of audit events matching criteria
     */
    @Query("SELECT e FROM AuditEvent e " +
           "WHERE e.eventType IN :eventTypes " +
           "AND e.timestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY e.timestamp DESC")
    Page<AuditEvent> findByEventTypesAndTimestampBetween(@Param("eventTypes") List<AuthEventType> eventTypes,
                                                            @Param("startTime") LocalDateTime startTime,
                                                            @Param("endTime") LocalDateTime endTime,
                                                            Pageable pageable);

    /**
     * Clean up old audit events beyond retention period.
     * Used for data retention policy compliance.
     * 
     * @param cutoffTime events older than this timestamp will be deleted
     * @return number of deleted events
     */
    @Query("DELETE FROM AuditEvent e WHERE e.timestamp < :cutoffTime")
    int deleteOldEvents(@Param("cutoffTime") LocalDateTime cutoffTime);
}
