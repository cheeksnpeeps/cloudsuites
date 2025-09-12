package com.cloudsuites.framework.services.user;

import com.cloudsuites.framework.services.user.entities.DeviceType;
import com.cloudsuites.framework.services.user.entities.UserSession;

/**
 * High-level service for token rotation that integrates JWT token generation
 * with refresh token management and session tracking.
 */
public interface TokenRotationService {

    /**
     * Creates new access and refresh token pair for initial login.
     * 
     * @param userId The user identifier
     * @param deviceFingerprint Unique device identifier
     * @param deviceName Human-readable device name
     * @param deviceType Type of device
     * @param userAgent Browser/app user agent string
     * @param ipAddress Client IP address
     * @param location Optional location information
     * @param isTrusted Whether this is a trusted device
     * @return Token pair response with access and refresh tokens
     */
    TokenPairResponse createTokenPair(String userId, 
                                    String deviceFingerprint,
                                    String deviceName,
                                    DeviceType deviceType,
                                    String userAgent,
                                    String ipAddress,
                                    String location,
                                    boolean isTrusted);

    /**
     * Rotates tokens by using the current refresh token to generate new access and refresh tokens.
     * 
     * @param refreshToken The current refresh token
     * @param ipAddress Current IP address for session tracking
     * @param userAgent Current user agent for session tracking
     * @return New token pair with updated refresh token
     */
    TokenPairResponse rotateTokens(String refreshToken, String ipAddress, String userAgent);

    /**
     * Validates a refresh token and returns user session information.
     * 
     * @param refreshToken The refresh token to validate
     * @return User session if token is valid
     */
    java.util.Optional<UserSession> validateRefreshToken(String refreshToken);

    /**
     * Revokes a specific refresh token.
     * 
     * @param refreshToken The refresh token to revoke
     * @return true if token was successfully revoked
     */
    boolean revokeToken(String refreshToken);

    /**
     * Revokes all tokens for a user (logout from all devices).
     * 
     * @param userId The user identifier
     * @return Number of sessions that were revoked
     */
    int revokeAllUserTokens(String userId);

    /**
     * Revokes token by access token JTI (for single session logout).
     * 
     * @param accessTokenJti The JTI claim from access token
     * @return true if session was successfully revoked
     */
    boolean revokeByAccessToken(String accessTokenJti);

    /**
     * Token pair response containing access and refresh tokens.
     */
    record TokenPairResponse(
        String accessToken,
        String refreshToken,
        String sessionId,
        long accessTokenExpiresIn,
        long refreshTokenExpiresIn,
        String tokenType
    ) {
        public static TokenPairResponse of(String accessToken, String refreshToken, String sessionId, 
                                         long accessExpiresIn, long refreshExpiresIn) {
            return new TokenPairResponse(accessToken, refreshToken, sessionId, 
                                       accessExpiresIn, refreshExpiresIn, "Bearer");
        }
    }
}
