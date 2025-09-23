package com.cloudsuites.framework.services.auth;

import com.cloudsuites.framework.services.auth.dto.AuditEventRequest;
import com.cloudsuites.framework.services.auth.dto.AuditEventResponse;
import com.cloudsuites.framework.services.auth.dto.AuditQueryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service interface for comprehensive audit logging of authentication events.
 * Provides security monitoring, compliance reporting, and forensic capabilities.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public interface AuditService {

    /**
     * Records an authentication event for audit purposes.
     * 
     * @param request audit event details
     * @return saved audit event response
     */
    AuditEventResponse logAuthEvent(AuditEventRequest request);

    /**
     * Records a successful user login event.
     * 
     * @param userId user identifier
     * @param ipAddress client IP address
     * @param userAgent browser/client user agent
     * @param sessionId session identifier
     * @return audit event response
     */
    AuditEventResponse logSuccessfulLogin(String userId, String ipAddress, String userAgent, String sessionId);

    /**
     * Records a failed login attempt.
     * 
     * @param userId attempted user identifier (may be null for invalid usernames)
     * @param ipAddress client IP address
     * @param userAgent browser/client user agent
     * @param reason failure reason
     * @return audit event response
     */
    AuditEventResponse logFailedLogin(String userId, String ipAddress, String userAgent, String reason);

    /**
     * Records a password change event.
     * 
     * @param userId user identifier
     * @param ipAddress client IP address
     * @param userAgent browser/client user agent
     * @param sessionId session identifier
     * @return audit event response
     */
    AuditEventResponse logPasswordChange(String userId, String ipAddress, String userAgent, String sessionId);

    /**
     * Records an OTP verification event.
     * 
     * @param userId user identifier
     * @param ipAddress client IP address
     * @param userAgent browser/client user agent
     * @param success whether OTP verification was successful
     * @param channel delivery channel (SMS, EMAIL)
     * @return audit event response
     */
    AuditEventResponse logOtpVerification(String userId, String ipAddress, String userAgent, boolean success, String channel);

    /**
     * Records a suspicious activity event.
     * 
     * @param userId user identifier (may be null)
     * @param ipAddress client IP address
     * @param userAgent browser/client user agent
     * @param description activity description
     * @param metadata additional event metadata
     * @return audit event response
     */
    AuditEventResponse logSuspiciousActivity(String userId, String ipAddress, String userAgent, String description, Map<String, Object> metadata);

    /**
     * Records a session expiration event.
     * 
     * @param userId user identifier
     * @param sessionId expired session identifier
     * @param reason expiration reason
     * @return audit event response
     */
    AuditEventResponse logSessionExpiration(String userId, String sessionId, String reason);

    /**
     * Retrieves audit events for a specific user.
     * 
     * @param userId user identifier
     * @param eventTypes filter by event types (null for all)
     * @param startDate start date for filtering (null for no limit)
     * @param endDate end date for filtering (null for no limit)
     * @param pageable pagination parameters
     * @return page of audit events
     */
    Page<AuditEventResponse> getUserAuditEvents(String userId, List<AuthEventType> eventTypes,
                                               LocalDateTime startDate, LocalDateTime endDate,
                                               Pageable pageable);

    /**
     * Performs complex audit queries with multiple filter criteria.
     * 
     * @param request query criteria and filters
     * @param pageable pagination parameters
     * @return page of matching audit events
     */
    Page<AuditEventResponse> searchAuditEvents(AuditQueryRequest request, Pageable pageable);

    /**
     * Retrieves security events requiring immediate attention.
     * 
     * @param startDate start date for filtering
     * @param endDate end date for filtering
     * @param pageable pagination parameters
     * @return page of security events
     */
    Page<AuditEventResponse> getSecurityEvents(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Gets audit statistics for reporting and monitoring.
     * 
     * @param startDate start date for statistics
     * @param endDate end date for statistics
     * @return audit statistics map
     */
    Map<String, Object> getAuditStatistics(LocalDateTime startDate, LocalDateTime endDate);
}