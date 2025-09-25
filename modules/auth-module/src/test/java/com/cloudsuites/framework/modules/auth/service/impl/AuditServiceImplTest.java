package com.cloudsuites.framework.modules.auth.service.impl;

import com.cloudsuites.framework.modules.auth.entity.AuditEvent;
import com.cloudsuites.framework.modules.auth.repository.AuthAuditEventRepository;
import com.cloudsuites.framework.services.auth.AuthEventCategory;
import com.cloudsuites.framework.services.auth.AuthEventType;
import com.cloudsuites.framework.services.auth.RiskLevel;
import com.cloudsuites.framework.services.auth.entities.AuditEventRequest;
import com.cloudsuites.framework.services.auth.entities.AuditEventResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Simple unit tests for AuditServiceImpl.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuditServiceImpl Basic Tests")
@Disabled("Temporarily disabled due to DTO-to-entity architecture migration")
class AuditServiceImplBasicTest {

    @Mock
    private AuthAuditEventRepository auditEventRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    private AuditEvent sampleEvent;

    @BeforeEach
    void setUp() {
        sampleEvent = new AuditEvent();
        sampleEvent.setEventId("test-event-123");
        sampleEvent.setUserId("test-user-123");
        sampleEvent.setEventType(AuthEventType.LOGIN_SUCCESS);
        sampleEvent.setCategory(AuthEventCategory.AUTHENTICATION);
        sampleEvent.setRiskLevel(RiskLevel.LOW);
        sampleEvent.setIpAddress("192.168.1.1");
        sampleEvent.setUserAgent("Test Browser");
        sampleEvent.setDescription("Test audit event");
        sampleEvent.setSuccess(true);
    }

    @Test
    @DisplayName("Should log authentication event successfully")
    void shouldLogAuthEventSuccessfully() {
        // Given
        AuditEventRequest request = new AuditEventRequest(
            AuthEventType.LOGIN_SUCCESS,
            AuthEventCategory.AUTHENTICATION,
            "test-user-123",
            "192.168.1.1",
            "Successful login"
        );
        request.setUserAgent("Test Browser");
        request.setSuccess(true);
        request.setRiskLevel(RiskLevel.LOW);

        when(auditEventRepository.save(any(AuditEvent.class))).thenReturn(sampleEvent);

        // When
        AuditEventResponse response = auditService.logAuthEvent(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEventId()).isEqualTo("test-event-123");
        assertThat(response.getEventType()).isEqualTo(AuthEventType.LOGIN_SUCCESS);
        assertThat(response.getUserId()).isEqualTo("test-user-123");
        assertThat(response.getSuccess()).isTrue();
    }

    @Test
    @DisplayName("Should log successful login")
    void shouldLogSuccessfulLogin() {
        // Given
        when(auditEventRepository.save(any(AuditEvent.class))).thenReturn(sampleEvent);

        // When
        AuditEventResponse response = auditService.logSuccessfulLogin(
            "test-user-123", 
            "192.168.1.1", 
            "Test Browser", 
            "session-123"
        );

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEventType()).isEqualTo(AuthEventType.LOGIN_SUCCESS);
        assertThat(response.getSuccess()).isTrue();
    }

    @Test
    @DisplayName("Should log failed login")
    void shouldLogFailedLogin() {
        // Given
        AuditEvent failedEvent = new AuditEvent();
        failedEvent.setEventId("failed-event-123");
        failedEvent.setEventType(AuthEventType.LOGIN_FAILURE);
        failedEvent.setSuccess(false);
        
        when(auditEventRepository.save(any(AuditEvent.class))).thenReturn(failedEvent);

        // When
        AuditEventResponse response = auditService.logFailedLogin(
            "test-user-123",
            "192.168.1.1",
            "Test Browser",
            "Invalid password"
        );

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEventType()).isEqualTo(AuthEventType.LOGIN_FAILURE);
        assertThat(response.getSuccess()).isFalse();
    }
}
