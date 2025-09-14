package com.cloudsuites.framework.modules.jwt;

import com.cloudsuites.framework.modules.user.repository.UserSessionRepository;
import com.cloudsuites.framework.services.common.exception.ValidationException;
import com.cloudsuites.framework.services.user.entities.DeviceType;
import com.cloudsuites.framework.services.user.entities.UserSession;
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

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private UserSessionRepository userSessionRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private static final String TEST_USER_ID = "USER-12345";
    private static final String TEST_DEVICE_FINGERPRINT = "device-fingerprint-123";
    private static final String TEST_DEVICE_NAME = "Test Device";
    private static final String TEST_USER_AGENT = "Mozilla/5.0 (Test Browser)";
    private static final String TEST_IP_ADDRESS = "192.168.1.100";
    private static final String TEST_LOCATION = "Toronto, Canada";
    private static final String TEST_REFRESH_TOKEN = "refresh-token-abc123";
    private static final String TEST_SESSION_ID = "SESSION-123";
    private static final String TEST_ACCESS_TOKEN_JTI = "jti-123";

    @BeforeEach
    void setUp() {
        // match @Value defaults in impl
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpirationHours", 720);
        ReflectionTestUtils.setField(refreshTokenService, "mobileRefreshTokenExpirationDays", 90);
        ReflectionTestUtils.setField(refreshTokenService, "trustedDeviceExpirationDays", 365);
        ReflectionTestUtils.setField(refreshTokenService, "maxSessionsPerUser", 10);
    }

    @Test
    void testCreateRefreshToken_SuccessfulCreation() {
        when(userSessionRepository.countActiveSessionsByUserId(TEST_USER_ID)).thenReturn(5L);
        UserSession expectedSession = createTestUserSession();
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(expectedSession);

        UserSession result = refreshTokenService.createRefreshToken(
                TEST_USER_ID, TEST_DEVICE_FINGERPRINT, TEST_DEVICE_NAME,
                DeviceType.DESKTOP, TEST_USER_AGENT, TEST_IP_ADDRESS, TEST_LOCATION, false
        );

        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getUserId());
        assertEquals(TEST_DEVICE_FINGERPRINT, result.getDeviceFingerprint());
        assertEquals(TEST_DEVICE_NAME, result.getDeviceName());
        assertEquals(DeviceType.DESKTOP, result.getDeviceType());
        assertTrue(result.isActive());
        assertFalse(result.getIsTrustedDevice());

        verify(userSessionRepository).countActiveSessionsByUserId(TEST_USER_ID);
        verify(userSessionRepository).save(any(UserSession.class));
    }

    @Test
    void testCreateRefreshToken_TrustedDevice_ExtendsExpiration() {
        when(userSessionRepository.countActiveSessionsByUserId(TEST_USER_ID)).thenReturn(3L);
        UserSession expectedSession = createTestUserSession();
        expectedSession.setIsTrustedDevice(true);
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(expectedSession);

        UserSession result = refreshTokenService.createRefreshToken(
                TEST_USER_ID, TEST_DEVICE_FINGERPRINT, TEST_DEVICE_NAME,
                DeviceType.DESKTOP, TEST_USER_AGENT, TEST_IP_ADDRESS, TEST_LOCATION, true
        );

        assertNotNull(result);
        assertTrue(result.getIsTrustedDevice());
        verify(userSessionRepository).save(any(UserSession.class));
    }

    @Test
    void testRotateRefreshToken_Success() {
        UserSession existingSession = createTestUserSession();
        existingSession.setExpiresAt(LocalDateTime.now().plusDays(30));

        when(userSessionRepository.findByRefreshTokenHashAndActive(anyString(), eq(true)))
                .thenReturn(Optional.of(existingSession));
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(existingSession);

        String newRefreshToken = "new-refresh-token-456";

        UserSession result = refreshTokenService.rotateRefreshToken(
                TEST_REFRESH_TOKEN, newRefreshToken, TEST_ACCESS_TOKEN_JTI
        );

        assertNotNull(result);
        assertEquals(TEST_ACCESS_TOKEN_JTI, result.getAccessTokenJti());
        verify(userSessionRepository).findByRefreshTokenHashAndActive(anyString(), eq(true));
        verify(userSessionRepository).save(existingSession);
    }

    @Test
    void testRotateRefreshToken_InvalidToken_ThrowsException() {
        when(userSessionRepository.findByRefreshTokenHashAndActive(anyString(), eq(true)))
                .thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () ->
                refreshTokenService.rotateRefreshToken(TEST_REFRESH_TOKEN, "new-token", TEST_ACCESS_TOKEN_JTI));

        verify(userSessionRepository).findByRefreshTokenHashAndActive(anyString(), eq(true));
        verify(userSessionRepository, never()).save(any());
    }

    @Test
    void testRotateRefreshToken_ExpiredSession_ThrowsException() {
        UserSession expiredSession = createTestUserSession();
        expiredSession.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(userSessionRepository.findByRefreshTokenHashAndActive(anyString(), eq(true)))
                .thenReturn(Optional.of(expiredSession));
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(expiredSession);

        assertThrows(ValidationException.class, () ->
                refreshTokenService.rotateRefreshToken(TEST_REFRESH_TOKEN, "new-token", TEST_ACCESS_TOKEN_JTI));

        verify(userSessionRepository).findByRefreshTokenHashAndActive(anyString(), eq(true));
        verify(userSessionRepository).save(expiredSession); // deactivation persisted
    }

    @Test
    void testValidateRefreshToken_ValidToken() {
        UserSession validSession = createTestUserSession();
        validSession.setExpiresAt(LocalDateTime.now().plusDays(30));

        when(userSessionRepository.findByRefreshTokenHashAndActive(anyString(), eq(true)))
                .thenReturn(Optional.of(validSession));

        Optional<UserSession> result = refreshTokenService.validateRefreshToken(TEST_REFRESH_TOKEN);

        assertTrue(result.isPresent());
        assertEquals(validSession, result.get());
        verify(userSessionRepository).findByRefreshTokenHashAndActive(anyString(), eq(true));
    }

    @Test
    void testValidateRefreshToken_InvalidToken() {
        when(userSessionRepository.findByRefreshTokenHashAndActive(anyString(), eq(true)))
                .thenReturn(Optional.empty());

        Optional<UserSession> result = refreshTokenService.validateRefreshToken(TEST_REFRESH_TOKEN);

        assertFalse(result.isPresent());
        verify(userSessionRepository).findByRefreshTokenHashAndActive(anyString(), eq(true));
    }

    @Test
    void testValidateRefreshToken_ExpiredToken() {
        UserSession expiredSession = createTestUserSession();
        expiredSession.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(userSessionRepository.findByRefreshTokenHashAndActive(anyString(), eq(true)))
                .thenReturn(Optional.of(expiredSession));
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(expiredSession);

        Optional<UserSession> result = refreshTokenService.validateRefreshToken(TEST_REFRESH_TOKEN);

        assertFalse(result.isPresent());
        verify(userSessionRepository).findByRefreshTokenHashAndActive(anyString(), eq(true));
        verify(userSessionRepository).save(expiredSession); // deactivation persisted
    }

    @Test
    void testRevokeRefreshToken_Success() {
        // Service now issues a direct UPDATE via repository
        when(userSessionRepository.deactivateSessionByRefreshToken(anyString(), any(LocalDateTime.class)))
                .thenReturn(1);

        boolean result = refreshTokenService.revokeRefreshToken(TEST_REFRESH_TOKEN);

        assertTrue(result);
        verify(userSessionRepository).deactivateSessionByRefreshToken(anyString(), any(LocalDateTime.class));
        // no entity load/save anymore
        verify(userSessionRepository, never()).findByRefreshTokenHashAndActive(anyString(), anyBoolean());
        verify(userSessionRepository, never()).save(any());
    }

    @Test
    void testRevokeRefreshToken_TokenNotFound() {
        when(userSessionRepository.deactivateSessionByRefreshToken(anyString(), any(LocalDateTime.class)))
                .thenReturn(0);

        boolean result = refreshTokenService.revokeRefreshToken(TEST_REFRESH_TOKEN);

        assertFalse(result);
        verify(userSessionRepository).deactivateSessionByRefreshToken(anyString(), any(LocalDateTime.class));
        verify(userSessionRepository, never()).save(any());
    }

    @Test
    void testRevokeAllUserTokens() {
        when(userSessionRepository.deactivateAllUserSessions(eq(TEST_USER_ID), any(LocalDateTime.class)))
                .thenReturn(3);

        int result = refreshTokenService.revokeAllUserTokens(TEST_USER_ID);

        assertEquals(3, result);
        verify(userSessionRepository).deactivateAllUserSessions(eq(TEST_USER_ID), any(LocalDateTime.class));
    }

    @Test
    void testGetUserActiveSessions() {
        List<UserSession> activeSessions = List.of(createTestUserSession(), createTestUserSession());
        when(userSessionRepository.findActiveSessionsByUserId(TEST_USER_ID)).thenReturn(activeSessions);

        List<UserSession> result = refreshTokenService.getUserActiveSessions(TEST_USER_ID);

        assertEquals(2, result.size());
        verify(userSessionRepository).findActiveSessionsByUserId(TEST_USER_ID);
    }

    @Test
    void testGetSessionByAccessTokenJti() {
        UserSession session = createTestUserSession();
        session.setAccessTokenJti(TEST_ACCESS_TOKEN_JTI);
        when(userSessionRepository.findByAccessTokenJti(TEST_ACCESS_TOKEN_JTI)).thenReturn(Optional.of(session));

        Optional<UserSession> result = refreshTokenService.getSessionByAccessTokenJti(TEST_ACCESS_TOKEN_JTI);

        assertTrue(result.isPresent());
        assertEquals(TEST_ACCESS_TOKEN_JTI, result.get().getAccessTokenJti());
        verify(userSessionRepository).findByAccessTokenJti(TEST_ACCESS_TOKEN_JTI);
    }

    @Test
    void testTokenHashGeneration_ConsistentHashing() {
        UserSession session = createTestUserSession();
        session.setExpiresAt(LocalDateTime.now().plusDays(30));

        when(userSessionRepository.findByRefreshTokenHashAndActive(anyString(), eq(true)))
                .thenReturn(Optional.of(session));

        Optional<UserSession> result1 = refreshTokenService.validateRefreshToken(TEST_REFRESH_TOKEN);
        Optional<UserSession> result2 = refreshTokenService.validateRefreshToken(TEST_REFRESH_TOKEN);

        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        verify(userSessionRepository, times(2)).findByRefreshTokenHashAndActive(anyString(), eq(true));
    }


@Test
void testRefreshTokenLifecycle_CreateRotateValidateRevoke() {
    // Setup
    UserSession createdSession = createTestUserSession();
    UserSession rotatedSession = createTestUserSession();
    String newRefreshToken = "new-refresh-token-456";

    // 1) Create
    when(userSessionRepository.countActiveSessionsByUserId(TEST_USER_ID)).thenReturn(5L);
    when(userSessionRepository.save(any(UserSession.class))).thenReturn(createdSession);
    UserSession created = refreshTokenService.createRefreshToken(
            TEST_USER_ID, TEST_DEVICE_FINGERPRINT, TEST_DEVICE_NAME,
            DeviceType.DESKTOP, TEST_USER_AGENT, TEST_IP_ADDRESS, TEST_LOCATION, false
    );
    assertNotNull(created);

    // 2) Rotate (find active + save)
    createdSession.setExpiresAt(LocalDateTime.now().plusDays(30));
    when(userSessionRepository.findByRefreshTokenHashAndActive(anyString(), eq(true)))
            .thenReturn(Optional.of(createdSession));
    when(userSessionRepository.save(any(UserSession.class))).thenReturn(rotatedSession);

    UserSession rotated = refreshTokenService.rotateRefreshToken(
            TEST_REFRESH_TOKEN, newRefreshToken, TEST_ACCESS_TOKEN_JTI
    );
    assertNotNull(rotated);

    // 3) Validate new token (find active)
    when(userSessionRepository.findByRefreshTokenHashAndActive(anyString(), eq(true)))
            .thenReturn(Optional.of(rotatedSession));
    Optional<UserSession> validated = refreshTokenService.validateRefreshToken(newRefreshToken);
    assertTrue(validated.isPresent());

    // 4) Revoke refresh token - direct update (no save)
    when(userSessionRepository.deactivateSessionByRefreshToken(anyString(), any(LocalDateTime.class)))
            .thenReturn(1);

    boolean revoked = refreshTokenService.revokeRefreshToken(newRefreshToken);
    assertTrue(revoked);

    // Verify interactions:
    // - 2 saves total: create + rotate
    verify(userSessionRepository, times(2)).save(any(UserSession.class));
    // - validation did one lookup
    verify(userSessionRepository, atLeast(1)).findByRefreshTokenHashAndActive(anyString(), eq(true));
    // - revoke used the bulk update method (no save)
    verify(userSessionRepository, times(1))
            .deactivateSessionByRefreshToken(anyString(), any(LocalDateTime.class));
}

    private UserSession createTestUserSession() {
        return UserSession.builder()
                .sessionId(TEST_SESSION_ID)
                .userId(TEST_USER_ID)
                .refreshTokenHash("hashed-refresh-token")
                .deviceFingerprint(TEST_DEVICE_FINGERPRINT)
                .deviceName(TEST_DEVICE_NAME)
                .deviceType(DeviceType.DESKTOP)
                .userAgent(TEST_USER_AGENT)
                .ipAddress(TEST_IP_ADDRESS)
                .location(TEST_LOCATION)
                .isTrustedDevice(false)
                .isActive(true)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .lastActivityAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .createdBy(TEST_USER_ID)
                .build();
    }
}