package com.cloudsuites.framework.services.user;

import com.cloudsuites.framework.services.user.entities.DeviceType;
import com.cloudsuites.framework.services.user.entities.UserSession;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing refresh token rotation and user session lifecycle.
 * Implements secure token rotation, device management, and session tracking
 * according to OAuth2 and security best practices.
 */
public interface RefreshTokenService {

    /**
     * Creates a new refresh token and session for a user.
     * 
     * @param userId The user identifier
     * @param deviceFingerprint Unique device identifier
     * @param deviceName Human-readable device name
     * @param deviceType Type of device (WEB, MOBILE_IOS, MOBILE_ANDROID, etc.)
     * @param userAgent Browser/app user agent string
     * @param ipAddress Client IP address
     * @param location Optional location information
     * @param isTrusted Whether this is a trusted device
     * @return New user session with refresh token
     */
    UserSession createRefreshToken(String userId, 
                                 String deviceFingerprint,
                                 String deviceName,
                                 DeviceType deviceType,
                                 String userAgent,
                                 String ipAddress,
                                 String location,
                                 boolean isTrusted);

    /**
     * Rotates an existing refresh token to a new one.
     * This is called every time a refresh token is used to get a new access token.
     * 
     * @param currentRefreshToken The current refresh token
     * @param newRefreshToken The new refresh token to replace it
     * @param accessTokenJti JTI of the new access token for tracking
     * @return Updated user session with new refresh token
     * @throws com.cloudsuites.framework.services.common.exception.ValidationException if token is invalid or expired
     */
    UserSession rotateRefreshToken(String currentRefreshToken, String newRefreshToken, String accessTokenJti);

    /**
     * Validates a refresh token and returns the associated session.
     * 
     * @param refreshToken The refresh token to validate
     * @return User session if token is valid and active
     */
    Optional<UserSession> validateRefreshToken(String refreshToken);

    /**
     * Revokes a specific refresh token by making the session inactive.
     * 
     * @param refreshToken The refresh token to revoke
     * @return true if token was successfully revoked
     */
    boolean revokeRefreshToken(String refreshToken);

    /**
     * Revokes all refresh tokens for a specific user (logout from all devices).
     * 
     * @param userId The user identifier
     * @return Number of sessions that were revoked
     */
    int revokeAllUserTokens(String userId);

    /**
     * Revokes a refresh token by session ID.
     * 
     * @param sessionId The session identifier
     * @return true if session was successfully revoked
     */
    boolean revokeSessionById(String sessionId);

    /**
     * Gets all active sessions for a user.
     * 
     * @param userId The user identifier
     * @return List of active user sessions
     */
    List<UserSession> getUserActiveSessions(String userId);

    /**
     * Gets session information by access token JTI.
     * Useful for logout operations when you have the access token.
     * 
     * @param accessTokenJti The JTI claim from the access token
     * @return User session if found and active
     */
    Optional<UserSession> getSessionByAccessTokenJti(String accessTokenJti);

    /**
     * Updates session activity timestamp and metadata.
     * Called when the user performs actions to track activity.
     * 
     * @param sessionId The session identifier
     * @param ipAddress Current IP address
     * @param userAgent Current user agent
     * @param location Optional location information
     */
    void updateSessionActivity(String sessionId, String ipAddress, String userAgent, String location);

    /**
     * Extends the expiration time of a session (for trusted devices).
     * 
     * @param sessionId The session identifier
     * @param additionalHours Hours to extend the session
     * @return true if session was successfully extended
     */
    boolean extendSessionExpiration(String sessionId, int additionalHours);

    /**
     * Marks a device as trusted, extending its session lifetime.
     * 
     * @param sessionId The session identifier
     * @return true if device was successfully marked as trusted
     */
    boolean trustDevice(String sessionId);

    /**
     * Removes trust from a device.
     * 
     * @param sessionId The session identifier
     * @return true if device trust was successfully removed
     */
    boolean untrustDevice(String sessionId);

    /**
     * Cleans up expired sessions from the database.
     * This should be called periodically by a scheduled task.
     * 
     * @return Number of expired sessions that were cleaned up
     */
    int cleanupExpiredSessions();

    /**
     * Cleans up stale sessions (inactive for more than specified hours).
     * 
     * @param inactiveHours Hours of inactivity after which sessions are considered stale
     * @return Number of stale sessions that were cleaned up
     */
    int cleanupStaleSessions(int inactiveHours);

    /**
     * Gets session statistics for a user.
     * 
     * @param userId The user identifier
     * @return Session statistics (active count, trusted devices, last activity)
     */
    SessionStats getUserSessionStats(String userId);

    /**
     * Checks if a user has any active sessions.
     * 
     * @param userId The user identifier
     * @return true if user has active sessions
     */
    boolean hasActiveSessions(String userId);

    /**
     * Gets sessions for a specific device fingerprint.
     * Useful for detecting multiple sessions from the same device.
     * 
     * @param userId The user identifier
     * @param deviceFingerprint The device fingerprint
     * @return List of sessions for the device
     */
    List<UserSession> getSessionsByDeviceFingerprint(String userId, String deviceFingerprint);

    /**
     * Session statistics data class.
     */
    record SessionStats(
        int activeSessions,
        int trustedDevices,
        java.time.LocalDateTime lastActivity
    ) {}
}
