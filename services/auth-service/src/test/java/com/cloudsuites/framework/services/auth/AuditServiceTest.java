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
        assertThat(response.isSuccess()).isTrue();
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
        assertThat(response.isSuccess()).isTrue();
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
        assertThat(response.isSuccess()).isFalse();
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
        assertThat(response.isSuccess()).isTrue();
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
        request.setEventType(AuthEventType.LOGIN_FAILURE);
        request.setStartDate(LocalDateTime.now().minusDays(1));
        request.setEndDate(LocalDateTime.now());
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
            String eventId = UUID.randomUUID().toString();
            AuditEventResponse response = AuditEventResponse.success(eventId);
            response.setEventType(request.getEventType());
            response.setCategory(request.getCategory());
            response.setUserId(request.getUserId());
            response.setIpAddress(request.getIpAddress());
            response.setUserAgent(request.getUserAgent());
            response.setSessionId(request.getSessionId());
            response.setDescription(request.getDetails());
            response.setGeolocation(request.getLocation());
            response.setDeviceType("WEB"); // Default device type
            response.setMetadata(request.getMetadata());
            response.setRiskLevel(request.getRiskLevel() != null ? request.getRiskLevel() : RiskLevel.LOW);
            response.setTimestamp(LocalDateTime.now());
            
            return response;
        }

        @Override
        public AuditEventResponse logSuccessfulLogin(String userId, String ipAddress, String userAgent, String sessionId) {
            AuditEventRequest request = new AuditEventRequest(
                AuthEventType.LOGIN_SUCCESS, AuthEventCategory.AUTHENTICATION, RiskLevel.LOW
            );
            request.setUserId(userId);
            request.setIpAddress(ipAddress);
            request.setUserAgent(userAgent);
            request.setSessionId(sessionId);
            request.setDetails("Login successful");
            return logAuthEvent(request);
        }

        @Override
        public AuditEventResponse logFailedLogin(String userId, String ipAddress, String userAgent, String reason) {
            // Create audit event directly with failure info
            AuditEvent auditEvent = new AuditEvent();
            auditEvent.setEventId(UUID.randomUUID().toString());
            auditEvent.setEventType(AuthEventType.LOGIN_FAILURE);
            auditEvent.setCategory(AuthEventCategory.AUTHENTICATION);
            auditEvent.setUserId(userId);
            auditEvent.setIpAddress(ipAddress);
            auditEvent.setUserAgent(userAgent);
            auditEvent.setDescription("Login failed");
            auditEvent.setSuccess(false);
            auditEvent.setFailureReason(reason);
            auditEvent.setRiskLevel(RiskLevel.MEDIUM);
            auditEvent.setTimestamp(LocalDateTime.now());
            auditEvent.setCreatedAt(LocalDateTime.now());
            auditEvent.setCreatedBy("SYSTEM");
            
            // For failed operations, return a response that reflects the operation failure
            String eventId = UUID.randomUUID().toString();
            AuditEventResponse response = AuditEventResponse.success(eventId);
            response.setEventType(AuthEventType.LOGIN_FAILURE);
            response.setCategory(AuthEventCategory.AUTHENTICATION);
            response.setUserId(userId);
            response.setIpAddress(ipAddress);
            response.setUserAgent(userAgent);
            response.setDescription("Login failed");
            response.setSuccess(false);
            response.setFailureReason(reason);
            response.setRiskLevel(RiskLevel.MEDIUM);
            response.setTimestamp(LocalDateTime.now());
            
            return response;
        }

        @Override
        public AuditEventResponse logPasswordChange(String userId, String ipAddress, String userAgent, String sessionId) {
            AuditEventRequest request = new AuditEventRequest(
                AuthEventType.PASSWORD_CHANGE, AuthEventCategory.AUTHENTICATION, RiskLevel.LOW
            );
            request.setUserId(userId);
            request.setIpAddress(ipAddress);
            request.setUserAgent(userAgent);
            request.setSessionId(sessionId);
            request.setDetails("Password changed");
            return logAuthEvent(request);
        }

        @Override
        public AuditEventResponse logOtpVerification(String userId, String ipAddress, String userAgent, boolean success, String channel) {
            AuthEventType eventType = success ? AuthEventType.OTP_VERIFY_SUCCESS : AuthEventType.OTP_VERIFY_FAILURE;
            String eventId = UUID.randomUUID().toString();
            AuditEventResponse response = AuditEventResponse.success(eventId);
            response.setEventType(eventType);
            response.setCategory(AuthEventCategory.AUTHENTICATION);
            response.setUserId(userId);
            response.setIpAddress(ipAddress);
            response.setUserAgent(userAgent);
            response.setDescription(success ? "OTP verification successful" : "OTP verification failed");
            response.setSuccess(success);
            response.setMetadata(Map.of("channel", channel));
            response.setRiskLevel(RiskLevel.LOW);
            response.setTimestamp(LocalDateTime.now());
            
            return response;
        }

        @Override
        public AuditEventResponse logSuspiciousActivity(String userId, String ipAddress, String userAgent, String description, Map<String, Object> metadata) {
            String eventId = UUID.randomUUID().toString();
            AuditEventResponse response = AuditEventResponse.success(eventId);
            response.setEventType(AuthEventType.SUSPICIOUS_ACTIVITY);
            response.setCategory(AuthEventCategory.SECURITY);
            response.setUserId(userId);
            response.setIpAddress(ipAddress);
            response.setUserAgent(userAgent);
            response.setDescription(description);
            response.setSuccess(false);
            response.setMetadata(metadata);
            response.setRiskLevel(RiskLevel.HIGH);
            response.setTimestamp(LocalDateTime.now());
            
            return response;
        }

        @Override
        public AuditEventResponse logSessionExpiration(String userId, String sessionId, String reason) {
            String eventId = UUID.randomUUID().toString();
            AuditEventResponse response = AuditEventResponse.success(eventId);
            response.setEventType(AuthEventType.SESSION_EXPIRED);
            response.setCategory(AuthEventCategory.SESSION_MANAGEMENT);
            response.setUserId(userId);
            response.setSessionId(sessionId);
            response.setDescription("Session expired");
            response.setFailureReason(reason);
            response.setRiskLevel(RiskLevel.LOW);
            response.setTimestamp(LocalDateTime.now());
            
            return response;
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
