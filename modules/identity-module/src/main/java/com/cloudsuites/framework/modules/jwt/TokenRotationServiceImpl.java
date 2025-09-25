package com.cloudsuites.framework.modules.jwt;

import com.cloudsuites.framework.services.user.RefreshTokenService;
import com.cloudsuites.framework.services.user.TokenRotationService;
import com.cloudsuites.framework.services.user.entities.DeviceType;
import com.cloudsuites.framework.services.user.entities.UserSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of TokenRotationService that integrates JWT token generation
 * with refresh token rotation and session management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TokenRotationServiceImpl implements TokenRotationService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.security.jwt.access-token.expiration-minutes:15}")
    private int accessTokenExpirationMinutes;

    @Value("${app.security.jwt.refresh-token.expiration-hours:720}") // 30 days
    private int refreshTokenExpirationHours;

    private static final Logger log = LoggerFactory.getLogger(TokenRotationServiceImpl.class);

    @Override
    public TokenPairResponse createTokenPair(String userId,
                                           String deviceFingerprint,
                                           String deviceName,
                                           DeviceType deviceType,
                                           String userAgent,
                                           String ipAddress,
                                           String location,
                                           boolean isTrusted) {
        
        log.debug("Creating token pair for user: {} on device: {}", userId, deviceType);
        
        // Generate unique JTI for access token
        String accessTokenJti = UUID.randomUUID().toString();
        
        // Create session first with temporary refresh token hash
        UserSession session = refreshTokenService.createRefreshToken(
            userId, deviceFingerprint, deviceName, deviceType, 
            userAgent, ipAddress, location, isTrusted
        );
        
        // Generate actual tokens
        String accessToken = generateAccessToken(userId, session.getSessionId(), accessTokenJti);
        String refreshToken = generateRefreshToken(userId, session.getSessionId());
        
        // Update session with real refresh token and access token JTI
        UserSession updatedSession = refreshTokenService.rotateRefreshToken(
            "temp_" + System.currentTimeMillis(), // temporary token that was used during creation
            refreshToken, 
            accessTokenJti
        );
        
        log.info("Created token pair for user: {} with session: {}", userId, updatedSession.getSessionId());
        
        return TokenPairResponse.of(
            accessToken, 
            refreshToken, 
            updatedSession.getSessionId(),
            accessTokenExpirationMinutes * 60L, // convert to seconds
            refreshTokenExpirationHours * 3600L // convert to seconds
        );
    }

    @Override
    public TokenPairResponse rotateTokens(String refreshToken, String clientId, DeviceType deviceType, boolean isTrustedDevice) {
        log.debug("Rotating tokens for refresh token");
        
        // Validate refresh token first
        Optional<UserSession> sessionOpt = refreshTokenService.validateRefreshToken(refreshToken);
        if (sessionOpt.isEmpty()) {
            log.warn("Token rotation failed: invalid refresh token");
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        UserSession session = sessionOpt.get();
        String userId = session.getUserId();
        
        // Generate new tokens
        String newAccessTokenJti = UUID.randomUUID().toString();
        String newAccessToken = generateAccessToken(userId, session.getSessionId(), newAccessTokenJti);
        String newRefreshToken = generateRefreshToken(userId, session.getSessionId());
        
        // Rotate refresh token in session
        UserSession updatedSession = refreshTokenService.rotateRefreshToken(
            refreshToken, 
            newRefreshToken, 
            newAccessTokenJti
        );
        
        // Update session activity with existing session data
        refreshTokenService.updateSessionActivity(
            updatedSession.getSessionId(), 
            session.getIpAddress(), 
            session.getUserAgent(), 
            session.getLocation()
        );
        
        log.debug("Successfully rotated tokens for user: {} session: {}", userId, session.getSessionId());
        
        return TokenPairResponse.of(
            newAccessToken, 
            newRefreshToken, 
            updatedSession.getSessionId(),
            accessTokenExpirationMinutes * 60L,
            refreshTokenExpirationHours * 3600L
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSession> validateRefreshToken(String refreshToken) {
        return refreshTokenService.validateRefreshToken(refreshToken);
    }

    @Override
    public boolean revokeToken(String refreshToken) {
        return refreshTokenService.revokeRefreshToken(refreshToken);
    }

    @Override
    public int revokeAllUserTokens(String userId) {
        return refreshTokenService.revokeAllUserTokens(userId);
    }

    @Override
    public boolean revokeByAccessToken(String accessTokenJti) {
        Optional<UserSession> sessionOpt = refreshTokenService.getSessionByAccessTokenJti(accessTokenJti);
        if (sessionOpt.isPresent()) {
            return refreshTokenService.revokeSessionById(sessionOpt.get().getSessionId());
        }
        return false;
    }

    // ============================================================================
    // PRIVATE HELPER METHODS
    // ============================================================================

    /**
     * Generates an access token with custom claims.
     */
    private String generateAccessToken(String userId, String sessionId, String jti) {
        // Build custom claims for the token (including JTI)
        Map<String, Object> customClaims = Map.of(
            "sessionId", sessionId,
            "type", "access",
            "jti", jti
        );
        
        return jwtTokenProvider.generateAccessToken(userId, customClaims);
    }

    /**
     * Generates a refresh token.
     */
    private String generateRefreshToken(String userId, String sessionId) {
        return jwtTokenProvider.generateRefreshToken(userId, sessionId);
    }
}
