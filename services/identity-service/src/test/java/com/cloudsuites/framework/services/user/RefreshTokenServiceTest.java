package com.cloudsuites.framework.services.user;

import com.cloudsuites.framework.services.user.entities.DeviceType;
import com.cloudsuites.framework.services.user.entities.UserSession;
import com.cloudsuites.framework.services.user.impl.RefreshTokenServiceImpl;
import com.cloudsuites.framework.services.user.repository.UserSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RefreshTokenService implementation.
 * Tests token rotation, session management, and security features.
 */
@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private UserSessionRepository userSessionRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private UserSession testSession;
    private final String testUserId = "USER-123";
    private final String testRefreshToken = "refresh-token-123";
    private final String testDeviceFingerprint = "device-fingerprint-123";

    @BeforeEach
    void setUp() {
        // Set configuration properties
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpirationHours", 720);
        ReflectionTestUtils.setField(refreshTokenService, "mobileRefreshTokenExpirationDays", 90);
        ReflectionTestUtils.setField(refreshTokenService, "trustedDeviceExpirationDays", 365);
        ReflectionTestUtils.setField(refreshTokenService, "maxSessionsPerUser", 10);

        // Create test session
        testSession = UserSession.builder()
                .sessionId("SES-123")
                .userId(testUserId)
                .refreshTokenHash("hashed-token")
                .deviceFingerprint(testDeviceFingerprint)
                .deviceName("Test Device")
                .deviceType(DeviceType.WEB)
                .userAgent("Test Agent")
                .ipAddress("192.168.1.1")
                .location("Test Location")
                .isTrustedDevice(false)
                .isActive(true)
                .lastActivityAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(720))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createRefreshToken_Success() {
        // Given
        when(userSessionRepository.countActiveSessionsByUserId(testUserId)).thenReturn(5L);
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(testSession);

        // When
        UserSession result = refreshTokenService.createRefreshToken(
                testUserId, testDeviceFingerprint, "Test Device", DeviceType.WEB,
                "Test Agent", "192.168.1.1", "Test Location", false
        );

        // Then
        assertNotNull(result);
        assertEquals(testUserId, result.getUserId());
        assertEquals(DeviceType.WEB, result.getDeviceType());
        assertFalse(result.getIsTrustedDevice());
        verify(userSessionRepository).save(any(UserSession.class));
    }

    @Test
    void createRefreshToken_WithMaxSessionsReached_RemovesOldest() {
        // Given
        when(userSessionRepository.countActiveSessionsByUserId(testUserId)).thenReturn(10L);
        when(userSessionRepository.findActiveSessionsByUserId(testUserId))
                .thenReturn(List.of(testSession));
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(testSession);

        // When
        refreshTokenService.createRefreshToken(
                testUserId, testDeviceFingerprint, "Test Device", DeviceType.WEB,
                "Test Agent", "192.168.1.1", "Test Location", false
        );

        // Then
        verify(userSessionRepository, times(2)).save(any(UserSession.class)); // Once for deactivation, once for new session
    }

    @Test
    void rotateRefreshToken_Success() {
        // Given
        String currentToken = "current-token";
        String newToken = "new-token";
        String accessTokenJti = "jti-123";

        when(userSessionRepository.findByRefreshTokenHashAndActive(anyString()))
                .thenReturn(Optional.of(testSession));
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(testSession);

        // When
        UserSession result = refreshTokenService.rotateRefreshToken(currentToken, newToken, accessTokenJti);

        // Then
        assertNotNull(result);
        verify(userSessionRepository).save(any(UserSession.class));
    }

    @Test
    void rotateRefreshToken_InvalidToken_ThrowsException() {
        // Given
        String currentToken = "invalid-token";
        String newToken = "new-token";
        String accessTokenJti = "jti-123";

        when(userSessionRepository.findByRefreshTokenHashAndActive(anyString()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(com.cloudsuites.framework.services.common.exception.ValidationException.class,
                () -> refreshTokenService.rotateRefreshToken(currentToken, newToken, accessTokenJti));
    }

    @Test
    void validateRefreshToken_ValidToken_ReturnsSession() {
        // Given
        String refreshToken = "valid-token";
        when(userSessionRepository.findByRefreshTokenHashAndActive(anyString()))
                .thenReturn(Optional.of(testSession));

        // When
        Optional<UserSession> result = refreshTokenService.validateRefreshToken(refreshToken);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testSession, result.get());
    }

    @Test
    void validateRefreshToken_InvalidToken_ReturnsEmpty() {
        // Given
        String refreshToken = "invalid-token";
        when(userSessionRepository.findByRefreshTokenHashAndActive(anyString()))
                .thenReturn(Optional.empty());

        // When
        Optional<UserSession> result = refreshTokenService.validateRefreshToken(refreshToken);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void validateRefreshToken_ExpiredSession_ReturnsEmpty() {
        // Given
        String refreshToken = "expired-token";
        UserSession expiredSession = UserSession.builder()
                .sessionId("SES-EXPIRED")
                .userId(testUserId)
                .refreshTokenHash("hashed-token")
                .isActive(true)
                .expiresAt(LocalDateTime.now().minusHours(1)) // Expired
                .build();

        when(userSessionRepository.findByRefreshTokenHashAndActive(anyString()))
                .thenReturn(Optional.of(expiredSession));
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(expiredSession);

        // When
        Optional<UserSession> result = refreshTokenService.validateRefreshToken(refreshToken);

        // Then
        assertFalse(result.isPresent());
        verify(userSessionRepository).save(any(UserSession.class)); // Should deactivate expired session
    }

    @Test
    void revokeRefreshToken_Success() {
        // Given
        String refreshToken = "token-to-revoke";
        when(userSessionRepository.deactivateSessionByRefreshToken(anyString(), any(LocalDateTime.class)))
                .thenReturn(1);

        // When
        boolean result = refreshTokenService.revokeRefreshToken(refreshToken);

        // Then
        assertTrue(result);
        verify(userSessionRepository).deactivateSessionByRefreshToken(anyString(), any(LocalDateTime.class));
    }

    @Test
    void revokeAllUserTokens_Success() {
        // Given
        when(userSessionRepository.deactivateAllUserSessions(eq(testUserId), any(LocalDateTime.class)))
                .thenReturn(3);

        // When
        int result = refreshTokenService.revokeAllUserTokens(testUserId);

        // Then
        assertEquals(3, result);
        verify(userSessionRepository).deactivateAllUserSessions(eq(testUserId), any(LocalDateTime.class));
    }

    @Test
    void getUserActiveSessions_Success() {
        // Given
        when(userSessionRepository.findActiveSessionsByUserId(testUserId))
                .thenReturn(List.of(testSession));

        // When
        List<UserSession> result = refreshTokenService.getUserActiveSessions(testUserId);

        // Then
        assertEquals(1, result.size());
        assertEquals(testSession, result.get(0));
    }

    @Test
    void trustDevice_Success() {
        // Given
        String sessionId = "SES-123";
        when(userSessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(testSession);

        // When
        boolean result = refreshTokenService.trustDevice(sessionId);

        // Then
        assertTrue(result);
        verify(userSessionRepository).save(any(UserSession.class));
    }

    @Test
    void cleanupExpiredSessions_Success() {
        // Given
        UserSession expiredSession = UserSession.builder()
                .sessionId("SES-EXPIRED")
                .userId(testUserId)
                .isActive(true)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .build();

        when(userSessionRepository.findExpiredSessions(any(LocalDateTime.class)))
                .thenReturn(List.of(expiredSession));
        when(userSessionRepository.saveAll(anyList())).thenReturn(List.of(expiredSession));
        when(userSessionRepository.deleteExpiredSessions(any(LocalDateTime.class))).thenReturn(5);

        // When
        int result = refreshTokenService.cleanupExpiredSessions();

        // Then
        assertEquals(1, result); // One expired session found
        verify(userSessionRepository).saveAll(anyList());
        verify(userSessionRepository).deleteExpiredSessions(any(LocalDateTime.class));
    }

    @Test
    void hasActiveSessions_True() {
        // Given
        when(userSessionRepository.hasActiveSessionsByUserId(testUserId)).thenReturn(true);

        // When
        boolean result = refreshTokenService.hasActiveSessions(testUserId);

        // Then
        assertTrue(result);
    }

    @Test
    void hasActiveSessions_False() {
        // Given
        when(userSessionRepository.hasActiveSessionsByUserId(testUserId)).thenReturn(false);

        // When
        boolean result = refreshTokenService.hasActiveSessions(testUserId);

        // Then
        assertFalse(result);
    }
}
