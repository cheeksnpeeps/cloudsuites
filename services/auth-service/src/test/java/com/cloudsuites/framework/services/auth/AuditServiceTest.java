package com.cloudsuites.framework.services.auth;

import com.cloudsuites.framework.services.auth.entities.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AuditService contract and behavior validation.
 * Tests the audit logging service interface contract without dependencies.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
class AuditServiceTest {

    private AuditService auditService;
    private UUID testUserId;
    private UUID testSessionId;
    private String testIpAddress;
    private String testUserAgent;

    @BeforeEach
    void setUp() {
        // Test data setup
        testUserId = UUID.randomUUID();
        testSessionId = UUID.randomUUID();
        testIpAddress = "192.168.1.100";
        testUserAgent = "Mozilla/5.0 (Test Browser)";
        
        // Mock implementation for contract testing
        auditService = new MockAuditService();
    }

    @Test
    @DisplayName("Should log authentication events")
    void shouldLogAuthenticationEvents() {
        // Given
        AuditEventRequest request = new AuditEventRequest();
        request.setEventType(AuthEventType.LOGIN_SUCCESS);
        request.setCategory(AuthEventCategory.AUTHENTICATION);
        request.setUserId(testUserId.toString());
        request.setIpAddress(testIpAddress);
        request.setDetails("User login successful");
        request.setUserAgent(testUserAgent);
        request.setSessionId(testSessionId.toString());
        // When
        AuditEventResponse response = auditService.logAuthEvent(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEventId()).isNotNull();
        assertThat(response.getEventType()).isEqualTo(AuthEventType.LOGIN_SUCCESS);
        assertThat(response.getCategory()).isEqualTo(AuthEventCategory.AUTHENTICATION);
        assertThat(response.getSuccess()).isTrue();
    }

    @Test
    @DisplayName("Should log successful login")
    void shouldLogSuccessfulLogin() {
        // When
        AuditEventResponse response = auditService.logSuccessfulLogin(
            testUserId.toString(), testIpAddress, testUserAgent, testSessionId.toString()
        );

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEventType()).isEqualTo(AuthEventType.LOGIN_SUCCESS);
        assertThat(response.getSuccess()).isTrue();
    }

    @Test
    @DisplayName("Should log failed login")
    void shouldLogFailedLogin() {
        // When
        AuditEventResponse response = auditService.logFailedLogin(
            testUserId.toString(), testIpAddress, testUserAgent, "Invalid credentials"
        );

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEventType()).isEqualTo(AuthEventType.LOGIN_FAILURE);
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getFailureReason()).isEqualTo("Invalid credentials");
    }

    @Test
    @DisplayName("Should log password change")
    void shouldLogPasswordChange() {
        // When
        AuditEventResponse response = auditService.logPasswordChange(
            testUserId.toString(), testIpAddress, testUserAgent, testSessionId.toString()
        );

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEventType()).isEqualTo(AuthEventType.PASSWORD_CHANGE);
    }

    @Test
    @DisplayName("Should log OTP verification")
    void shouldLogOtpVerification() {
        // When - successful OTP
        AuditEventResponse response = auditService.logOtpVerification(
            testUserId.toString(), testIpAddress, testUserAgent, true, "SMS"
        );

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEventType()).isEqualTo(AuthEventType.OTP_VERIFY_SUCCESS);
        assertThat(response.getSuccess()).isTrue();
    }

    @Test
    @DisplayName("Should log suspicious activity")
    void shouldLogSuspiciousActivity() {
        // Given
        Map<String, Object> metadata = Map.of(
            "attemptCount", 5,
            "timeWindow", "60 seconds"
        );

        // When
        AuditEventResponse response = auditService.logSuspiciousActivity(
            testUserId.toString(), testIpAddress, testUserAgent, 
            "Multiple failed login attempts", metadata
        );

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEventType()).isEqualTo(AuthEventType.SUSPICIOUS_ACTIVITY);
        assertThat(response.getRiskLevel()).isIn(RiskLevel.HIGH, RiskLevel.CRITICAL);
    }

    @Test
    @DisplayName("Should log session expiration")
    void shouldLogSessionExpiration() {
        // When
        AuditEventResponse response = auditService.logSessionExpiration(
            testUserId.toString(), testSessionId.toString(), "Timeout"
        );

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEventType()).isEqualTo(AuthEventType.SESSION_EXPIRED);
    }

    @Test
    @DisplayName("Should get user audit events with filtering")
    void shouldGetUserAuditEvents() {
        // Given
        List<AuthEventType> eventTypes = List.of(AuthEventType.LOGIN_SUCCESS, AuthEventType.LOGIN_FAILURE);
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<AuditEventResponse> events = auditService.getUserAuditEvents(
            testUserId.toString(), eventTypes, startDate, endDate, pageable
        );

        // Then
        assertThat(events).isNotNull();
        assertThat(events.getContent()).isNotNull();
    }

    @Test
    @DisplayName("Should search audit events")
    void shouldSearchAuditEvents() {
        // Given
        AuditQueryRequest request = new AuditQueryRequest();
        request.setUserId(testUserId.toString());
        request.setEventTypes(List.of(AuthEventType.LOGIN_FAILURE));
        request.setSuccess(false);
        request.setStartTime(LocalDateTime.now().minusDays(1));
        request.setEndTime(LocalDateTime.now());
        Pageable pageable = PageRequest.of(0, 20);

        // When
        Page<AuditEventResponse> events = auditService.searchAuditEvents(request, pageable);

        // Then
        assertThat(events).isNotNull();
        assertThat(events.getContent()).isNotNull();
    }

    @Test
    @DisplayName("Should get security events")
    void shouldGetSecurityEvents() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 50);

        // When
        Page<AuditEventResponse> events = auditService.getSecurityEvents(startDate, endDate, pageable);

        // Then
        assertThat(events).isNotNull();
        assertThat(events.getContent()).isNotNull();
    }

    /**
     * Mock implementation for contract testing
     */
    private static class MockAuditService implements AuditService {
        
        @Override
        public AuditEventResponse logAuthEvent(AuditEventRequest request) {
            AuditEventResponse response = new AuditEventResponse();
            response.setEventId(UUID.randomUUID().toString());
            response.setEventType(request.getEventType());
            response.setCategory(request.getCategory());
            response.setUserId(request.getUserId());
            response.setIpAddress(request.getIpAddress());
            response.setUserAgent(request.getUserAgent());
            response.setSessionId(request.getSessionId());
            response.setDescription(request.getDescription());
            response.setSuccess(request.getSuccess());
            response.setFailureReason(request.getFailureReason());
            response.setGeolocation(request.getGeolocation());
            response.setDeviceType(request.getDeviceType());
            response.setMetadata(request.getMetadata());
            response.setRiskLevel(request.getRiskLevel() != null ? request.getRiskLevel() : RiskLevel.LOW);
            response.setTimestamp(LocalDateTime.now());
            response.setCreatedAt(LocalDateTime.now());
            response.setLastModifiedAt(LocalDateTime.now());
            response.setCreatedBy("SYSTEM");
            response.setLastModifiedBy("SYSTEM");
            return response;
        }

        @Override
        public AuditEventResponse logSuccessfulLogin(String userId, String ipAddress, String userAgent, String sessionId) {
            AuditEventRequest request = new AuditEventRequest(
                AuthEventType.LOGIN_SUCCESS, AuthEventCategory.AUTHENTICATION, userId, ipAddress, "Login successful"
            );
            request.setUserAgent(userAgent);
            request.setSessionId(sessionId);
            request.setSuccess(true);
            return logAuthEvent(request);
        }

        @Override
        public AuditEventResponse logFailedLogin(String userId, String ipAddress, String userAgent, String reason) {
            AuditEventRequest request = new AuditEventRequest(
                AuthEventType.LOGIN_FAILURE, AuthEventCategory.AUTHENTICATION, userId, ipAddress, "Login failed"
            );
            request.setUserAgent(userAgent);
            request.setSuccess(false);
            request.setFailureReason(reason);
            return logAuthEvent(request);
        }

        @Override
        public AuditEventResponse logPasswordChange(String userId, String ipAddress, String userAgent, String sessionId) {
            AuditEventRequest request = new AuditEventRequest(
                AuthEventType.PASSWORD_CHANGE, AuthEventCategory.AUTHENTICATION, userId, ipAddress, "Password changed"
            );
            request.setUserAgent(userAgent);
            request.setSessionId(sessionId);
            request.setSuccess(true);
            return logAuthEvent(request);
        }

        @Override
        public AuditEventResponse logOtpVerification(String userId, String ipAddress, String userAgent, boolean success, String channel) {
            AuthEventType eventType = success ? AuthEventType.OTP_VERIFY_SUCCESS : AuthEventType.OTP_VERIFY_FAILURE;
            AuditEventRequest request = new AuditEventRequest(
                eventType, AuthEventCategory.AUTHENTICATION, userId, ipAddress, 
                success ? "OTP verification successful" : "OTP verification failed"
            );
            request.setUserAgent(userAgent);
            request.setSuccess(success);
            request.setMetadata(Map.of("channel", channel));
            return logAuthEvent(request);
        }

        @Override
        public AuditEventResponse logSuspiciousActivity(String userId, String ipAddress, String userAgent, String description, Map<String, Object> metadata) {
            AuditEventRequest request = new AuditEventRequest(
                AuthEventType.SUSPICIOUS_ACTIVITY, AuthEventCategory.SECURITY, userId, ipAddress, description
            );
            request.setUserAgent(userAgent);
            request.setSuccess(false);
            request.setMetadata(metadata);
            request.setRiskLevel(RiskLevel.HIGH);
            return logAuthEvent(request);
        }

        @Override
        public AuditEventResponse logSessionExpiration(String userId, String sessionId, String reason) {
            AuditEventRequest request = new AuditEventRequest(
                AuthEventType.SESSION_EXPIRED, AuthEventCategory.SESSION_MANAGEMENT, userId, null, "Session expired"
            );
            request.setSessionId(sessionId);
            request.setFailureReason(reason);
            return logAuthEvent(request);
        }

        @Override
        public Page<AuditEventResponse> getUserAuditEvents(String userId, List<AuthEventType> eventTypes, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
            return Page.empty();
        }

        @Override
        public Page<AuditEventResponse> searchAuditEvents(AuditQueryRequest request, Pageable pageable) {
            return Page.empty();
        }

        @Override
        public Page<AuditEventResponse> getSecurityEvents(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
            return Page.empty();
        }

        @Override
        public Map<String, Object> getAuditStatistics(LocalDateTime startDate, LocalDateTime endDate) {
            return Map.of(
                "totalEvents", 0L,
                "successfulEvents", 0L,
                "failedEvents", 0L,
                "securityEvents", 0L
            );
        }
    }
}
