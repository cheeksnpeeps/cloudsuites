package com.cloudsuites.framework.modules.auth.repository;

import com.cloudsuites.framework.modules.auth.entity.AuditEvent;
import com.cloudsuites.framework.services.auth.AuthEventCategory;
import com.cloudsuites.framework.services.auth.AuthEventType;
import com.cloudsuites.framework.services.auth.RiskLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple unit tests for AuditEvent entity.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
@DisplayName("AuditEvent Entity Tests")
class AuthAuditEventRepositoryTest {

    @Test
    @DisplayName("Should create audit event successfully")
    void shouldCreateAuditEvent() {
        // Given
        AuditEvent auditEvent = createTestAuditEvent();

        // Then
        assertThat(auditEvent).isNotNull();
        assertThat(auditEvent.getEventId()).isEqualTo("test-event-123");
        assertThat(auditEvent.getEventType()).isEqualTo(AuthEventType.LOGIN_SUCCESS);
        assertThat(auditEvent.getUserId()).isEqualTo("test-user-123");
        assertThat(auditEvent.getCategory()).isEqualTo(AuthEventCategory.AUTHENTICATION);
        assertThat(auditEvent.getRiskLevel()).isEqualTo(RiskLevel.LOW);
    }

    @Test
    @DisplayName("Should set and get all properties")
    void shouldSetAndGetAllProperties() {
        // Given
        AuditEvent event = new AuditEvent();
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        event.setEventId("event-456");
        event.setUserId("user-456");
        event.setEventType(AuthEventType.LOGIN_FAILURE);
        event.setCategory(AuthEventCategory.AUTHENTICATION);
        event.setRiskLevel(RiskLevel.HIGH);
        event.setIpAddress("10.0.0.1");
        event.setUserAgent("Chrome Browser");
        event.setDescription("Failed login attempt");
        event.setSuccess(false);
        event.setTimestamp(timestamp);

        // Then
        assertThat(event.getEventId()).isEqualTo("event-456");
        assertThat(event.getUserId()).isEqualTo("user-456");
        assertThat(event.getEventType()).isEqualTo(AuthEventType.LOGIN_FAILURE);
        assertThat(event.getCategory()).isEqualTo(AuthEventCategory.AUTHENTICATION);
        assertThat(event.getRiskLevel()).isEqualTo(RiskLevel.HIGH);
        assertThat(event.getIpAddress()).isEqualTo("10.0.0.1");
        assertThat(event.getUserAgent()).isEqualTo("Chrome Browser");
        assertThat(event.getDescription()).isEqualTo("Failed login attempt");
        assertThat(event.getSuccess()).isFalse();
        assertThat(event.getTimestamp()).isEqualTo(timestamp);
    }

    private AuditEvent createTestAuditEvent() {
        AuditEvent event = new AuditEvent();
        event.setEventId("test-event-123");
        event.setUserId("test-user-123");
        event.setEventType(AuthEventType.LOGIN_SUCCESS);
        event.setCategory(AuthEventCategory.AUTHENTICATION);
        event.setRiskLevel(RiskLevel.LOW);
        event.setIpAddress("192.168.1.1");
        event.setUserAgent("Test Browser");
        event.setDescription("Test audit event");
        event.setSuccess(true);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }
}
