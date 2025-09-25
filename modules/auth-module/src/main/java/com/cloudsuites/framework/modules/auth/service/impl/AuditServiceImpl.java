package com.cloudsuites.framework.modules.auth.service.impl;

import com.cloudsuites.framework.services.auth.entities.AuditEvent;
import com.cloudsuites.framework.modules.auth.repository.AuthAuditEventRepository;
import com.cloudsuites.framework.services.auth.AuditService;
import com.cloudsuites.framework.services.auth.AuthEventCategory;
import com.cloudsuites.framework.services.auth.AuthEventType;
import com.cloudsuites.framework.services.auth.RiskLevel;
import com.cloudsuites.framework.services.auth.entities.AuditEventRequest;
import com.cloudsuites.framework.services.auth.entities.AuditEventResponse;
import com.cloudsuites.framework.services.auth.entities.AuditQueryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Collections;

/**
 * Implementation of AuditService for logging and querying authentication events.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
@Service
@Transactional
public class AuditServiceImpl implements AuditService {

    private final AuthAuditEventRepository auditEventRepository;

    public AuditServiceImpl(AuthAuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Override
    public AuditEventResponse logAuthEvent(AuditEventRequest request) {
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setEventId(java.util.UUID.randomUUID().toString());
        auditEvent.setEventType(request.getEventType());
        auditEvent.setCategory(request.getCategory());
        auditEvent.setUserId(request.getUserId());
        auditEvent.setIpAddress(request.getIpAddress());
        auditEvent.setUserAgent(request.getUserAgent());
        auditEvent.setSessionId(request.getSessionId());
        auditEvent.setDescription(request.getDescription());
        auditEvent.setSuccess(request.getSuccess());
        auditEvent.setFailureReason(request.getFailureReason());
        auditEvent.setGeolocation(request.getGeolocation());
        auditEvent.setDeviceType(request.getDeviceType());
        auditEvent.setMetadata(request.getMetadata());
        auditEvent.setRiskLevel(request.getRiskLevel() != null ? request.getRiskLevel() : RiskLevel.LOW);
        auditEvent.setTimestamp(LocalDateTime.now());

        AuditEvent savedEvent = auditEventRepository.save(auditEvent);
        return mapToResponse(savedEvent);
    }

    @Override
    public AuditEventResponse logSuccessfulLogin(String userId, String ipAddress, String userAgent, String sessionId) {
        AuditEventRequest request = new AuditEventRequest(
            AuthEventType.LOGIN_SUCCESS,
            AuthEventCategory.AUTHENTICATION,
            userId,
            ipAddress,
            "Successful login"
        );
        request.setUserAgent(userAgent);
        request.setSessionId(sessionId);
        request.setSuccess(true);
        request.setRiskLevel(RiskLevel.LOW);
        
        return logAuthEvent(request);
    }

    @Override
    public AuditEventResponse logFailedLogin(String userId, String ipAddress, String userAgent, String reason) {
        AuditEventRequest request = new AuditEventRequest(
            AuthEventType.LOGIN_FAILURE,
            AuthEventCategory.AUTHENTICATION,
            userId,
            ipAddress,
            "Failed login attempt"
        );
        request.setUserAgent(userAgent);
        request.setSuccess(false);
        request.setFailureReason(reason);
        request.setRiskLevel(RiskLevel.MEDIUM);
        
        return logAuthEvent(request);
    }

    @Override
    public AuditEventResponse logPasswordChange(String userId, String ipAddress, String userAgent, String sessionId) {
        AuditEventRequest request = new AuditEventRequest(
            AuthEventType.PASSWORD_CHANGE,
            AuthEventCategory.PASSWORD_MANAGEMENT,
            userId,
            ipAddress,
            "Password changed"
        );
        request.setUserAgent(userAgent);
        request.setSessionId(sessionId);
        request.setSuccess(true);
        request.setRiskLevel(RiskLevel.MEDIUM);
        
        return logAuthEvent(request);
    }

    @Override
    public AuditEventResponse logOtpVerification(String userId, String ipAddress, String userAgent, boolean success, String channel) {
        AuditEventRequest request = new AuditEventRequest(
            success ? AuthEventType.OTP_VERIFY_SUCCESS : AuthEventType.OTP_VERIFY_FAILURE,
            AuthEventCategory.OTP_MANAGEMENT,
            userId,
            ipAddress,
            "OTP verification via " + channel
        );
        request.setUserAgent(userAgent);
        request.setSuccess(success);
        request.setRiskLevel(success ? RiskLevel.LOW : RiskLevel.MEDIUM);
        
        return logAuthEvent(request);
    }

    @Override
    public AuditEventResponse logSuspiciousActivity(String userId, String ipAddress, String userAgent, String description, Map<String, Object> metadata) {
        AuditEventRequest request = new AuditEventRequest(
            AuthEventType.SUSPICIOUS_ACTIVITY,
            AuthEventCategory.SECURITY,
            userId,
            ipAddress,
            description
        );
        request.setUserAgent(userAgent);
        request.setMetadata(metadata);
        request.setSuccess(false);
        request.setRiskLevel(RiskLevel.HIGH);
        
        return logAuthEvent(request);
    }

    @Override
    public AuditEventResponse logSessionExpiration(String userId, String sessionId, String reason) {
        AuditEventRequest request = new AuditEventRequest(
            AuthEventType.SESSION_EXPIRED,
            AuthEventCategory.SESSION_MANAGEMENT,
            userId,
            null,
            "Session expired: " + reason
        );
        request.setSessionId(sessionId);
        request.setFailureReason(reason);
        request.setSuccess(false);
        request.setRiskLevel(RiskLevel.LOW);
        
        return logAuthEvent(request);
    }

    @Override
    public Page<AuditEventResponse> getUserAuditEvents(String userId, List<AuthEventType> eventTypes,
                                                      LocalDateTime startDate, LocalDateTime endDate,
                                                      Pageable pageable) {
        // Simplified implementation - return empty page for now
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Page<AuditEventResponse> searchAuditEvents(AuditQueryRequest request, Pageable pageable) {
        // Simplified implementation - return empty page for now
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Page<AuditEventResponse> getSecurityEvents(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        // Simplified implementation - return empty page for now
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Map<String, Object> getAuditStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        // Simplified implementation - return empty map for now
        return Collections.emptyMap();
    }

    private AuditEventResponse mapToResponse(AuditEvent event) {
        AuditEventResponse response = new AuditEventResponse();
        response.setEventId(event.getEventId());
        response.setEventType(event.getEventType());
        response.setCategory(event.getCategory());
        response.setUserId(event.getUserId());
        response.setIpAddress(event.getIpAddress());
        response.setUserAgent(event.getUserAgent());
        response.setSessionId(event.getSessionId());
        response.setDescription(event.getDescription());
        response.setSuccess(event.getSuccess());
        response.setFailureReason(event.getFailureReason());
        response.setGeolocation(event.getGeolocation());
        response.setDeviceType(event.getDeviceType());
        response.setMetadata(event.getMetadata());
        response.setRiskLevel(event.getRiskLevel());
        response.setTimestamp(event.getTimestamp());
        return response;
    }
}
