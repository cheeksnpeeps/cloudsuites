package com.cloudsuites.framework.modules.jwt;

import com.cloudsuites.framework.modules.user.repository.UserSessionRepository;
import com.cloudsuites.framework.services.common.exception.ValidationException;
import com.cloudsuites.framework.services.user.RefreshTokenService;
import com.cloudsuites.framework.services.user.RefreshTokenService.SessionStats;
import com.cloudsuites.framework.services.user.entities.DeviceType;
import com.cloudsuites.framework.services.user.entities.UserSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of RefreshTokenService providing secure refresh token rotation,
 * session management, and device tracking for the CloudSuites authentication system.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    private final UserSessionRepository userSessionRepository;

    @Value("${app.security.jwt.refresh-token.expiration-hours:720}") // 30 days default
    private int refreshTokenExpirationHours;

    @Value("${app.security.jwt.refresh-token.mobile-expiration-days:90}") // 90 days for mobile
    private int mobileRefreshTokenExpirationDays;

    @Value("${app.security.jwt.refresh-token.trusted-device-expiration-days:365}") // 1 year for trusted devices
    private int trustedDeviceExpirationDays;

    @Value("${app.security.session.max-sessions-per-user:10}")
    private int maxSessionsPerUser;

    @Override
    public UserSession createRefreshToken(String userId, 
                                        String deviceFingerprint,
                                        String deviceName,
                                        DeviceType deviceType,
                                        String userAgent,
                                        String ipAddress,
                                        String location,
                                        boolean isTrusted) {
        
        log.debug("Creating refresh token for user: {} on device: {} (trusted: {})", userId, deviceType, isTrusted);
        
        // Check if user has reached maximum sessions limit
        long currentSessions = userSessionRepository.countActiveSessionsByUserId(userId);
        if (currentSessions >= maxSessionsPerUser) {
            log.warn("User {} has reached maximum sessions limit: {}", userId, maxSessionsPerUser);
            // Remove oldest session to make room
            removeOldestSession(userId);
        }

        // Generate a dummy refresh token hash for now (will be updated when real token is generated)
        String tempTokenHash = generateTokenHash("temp_" + System.currentTimeMillis());

        // Determine expiration based on device type and trust level
        LocalDateTime expiresAt = calculateExpirationTime(deviceType, isTrusted);

        // Create new session
        UserSession session = UserSession.builder()
                .userId(userId)
                .refreshTokenHash(tempTokenHash)
                .deviceFingerprint(deviceFingerprint)
                .deviceName(deviceName)
                .deviceType(deviceType)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .location(location)
                .isTrustedDevice(isTrusted)
                .isActive(true)
                .expiresAt(expiresAt)
                .lastActivityAt(LocalDateTime.now())
                .createdBy(userId)
                .build();

        UserSession savedSession = userSessionRepository.save(session);
        
        log.info("Created new session {} for user {} on {} device (expires: {})", 
                savedSession.getSessionId(), userId, deviceType, expiresAt);
        
        return savedSession;
    }

    @Override
    public UserSession rotateRefreshToken(String currentRefreshToken, String newRefreshToken, String accessTokenJti) {
        log.debug("Rotating refresh token for access token JTI: {}", accessTokenJti);
        
        String currentTokenHash = generateTokenHash(currentRefreshToken);
        String newTokenHash = generateTokenHash(newRefreshToken);
        
        // Find session by current refresh token
        Optional<UserSession> sessionOpt = userSessionRepository.findByRefreshTokenHashAndActive(currentTokenHash, true);
        if (sessionOpt.isEmpty()) {
            log.warn("Attempted to rotate invalid or expired refresh token");
            throw new ValidationException("Invalid or expired refresh token");
        }
        
        UserSession session = sessionOpt.get();
        
        // Check if session is expired
        if (session.isExpired()) {
            log.warn("Attempted to rotate expired session: {}", session.getSessionId());
            session.deactivate();
            userSessionRepository.save(session);
            throw new ValidationException("Session has expired");
        }
        
        // Update session with new token and access token JTI
        session.rotateRefreshToken(newTokenHash);
        session.setAccessTokenJti(accessTokenJti);
        session.updateLastActivity();
        
        UserSession updatedSession = userSessionRepository.save(session);
        
        log.debug("Successfully rotated refresh token for session: {}", session.getSessionId());
        return updatedSession;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSession> validateRefreshToken(String refreshToken) {
        String tokenHash = generateTokenHash(refreshToken);
        
        Optional<UserSession> sessionOpt = userSessionRepository.findByRefreshTokenHashAndActive(tokenHash,true);
        if (sessionOpt.isEmpty()) {
            log.debug("Refresh token validation failed: token not found or inactive");
            return Optional.empty();
        }
        
        UserSession session = sessionOpt.get();
        if (session.isExpired()) {
            log.debug("Refresh token validation failed: session {} expired", session.getSessionId());
            // Deactivate expired session
            session.deactivate();
            userSessionRepository.save(session);
            return Optional.empty();
        }
        
        log.debug("Refresh token validation successful for session: {}", session.getSessionId());
        return Optional.of(session);
    }

    @Override
    public boolean revokeRefreshToken(String refreshToken) {
        String tokenHash = generateTokenHash(refreshToken);
        
        int updated = userSessionRepository.deactivateSessionByRefreshToken(tokenHash, LocalDateTime.now());
        boolean revoked = updated > 0;
        
        if (revoked) {
            log.info("Successfully revoked refresh token");
        } else {
            log.warn("Failed to revoke refresh token: not found or already inactive");
        }
        
        return revoked;
    }

    @Override
    public int revokeAllUserTokens(String userId) {
        log.info("Revoking all sessions for user: {}", userId);
        
        int revokedCount = userSessionRepository.deactivateAllUserSessions(userId, LocalDateTime.now());
        
        log.info("Successfully revoked {} sessions for user: {}", revokedCount, userId);
        return revokedCount;
    }

    @Override
    public boolean revokeSessionById(String sessionId) {
        int updated = userSessionRepository.deactivateSession(sessionId, LocalDateTime.now());
        boolean revoked = updated > 0;
        
        if (revoked) {
            log.info("Successfully revoked session: {}", sessionId);
        } else {
            log.warn("Failed to revoke session: {} not found", sessionId);
        }
        
        return revoked;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSession> getUserActiveSessions(String userId) {
        return userSessionRepository.findActiveSessionsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSession> getSessionByAccessTokenJti(String accessTokenJti) {
        return userSessionRepository.findByAccessTokenJti(accessTokenJti);
    }

    @Override
    public void updateSessionActivity(String sessionId, String ipAddress, String userAgent, String location) {
        Optional<UserSession> sessionOpt = userSessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            UserSession session = sessionOpt.get();
            session.updateSessionMetadata(ipAddress, userAgent, location);
            userSessionRepository.save(session);
            
            log.debug("Updated activity for session: {} from IP: {}", sessionId, ipAddress);
        } else {
            log.warn("Attempted to update activity for non-existent session: {}", sessionId);
        }
    }

    @Override
    public boolean extendSessionExpiration(String sessionId, int additionalHours) {
        Optional<UserSession> sessionOpt = userSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            log.warn("Cannot extend non-existent session: {}", sessionId);
            return false;
        }
        
        UserSession session = sessionOpt.get();
        if (!session.isActive()) {
            log.warn("Cannot extend inactive session: {}", sessionId);
            return false;
        }
        
        LocalDateTime newExpiration = session.getExpiresAt().plusHours(additionalHours);
        session.extendExpiration(newExpiration);
        userSessionRepository.save(session);
        
        log.info("Extended session {} expiration by {} hours to: {}", sessionId, additionalHours, newExpiration);
        return true;
    }

    @Override
    public boolean trustDevice(String sessionId) {
        Optional<UserSession> sessionOpt = userSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            log.warn("Cannot trust non-existent session: {}", sessionId);
            return false;
        }
        
        UserSession session = sessionOpt.get();
        session.trustDevice();
        
        // Extend expiration for trusted device
        LocalDateTime newExpiration = LocalDateTime.now().plusDays(trustedDeviceExpirationDays);
        session.extendExpiration(newExpiration);
        
        userSessionRepository.save(session);
        
        log.info("Marked device as trusted for session: {} (extended to: {})", sessionId, newExpiration);
        return true;
    }

    @Override
    public boolean untrustDevice(String sessionId) {
        Optional<UserSession> sessionOpt = userSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            log.warn("Cannot untrust non-existent session: {}", sessionId);
            return false;
        }
        
        UserSession session = sessionOpt.get();
        session.untrustDevice();
        
        // Reset expiration to normal duration
        LocalDateTime normalExpiration = calculateExpirationTime(session.getDeviceType(), false);
        session.extendExpiration(normalExpiration);
        
        userSessionRepository.save(session);
        
        log.info("Removed device trust for session: {} (reset expiration to: {})", sessionId, normalExpiration);
        return true;
    }

    @Override
    public int cleanupExpiredSessions() {
        log.debug("Starting expired sessions cleanup");
        
        List<UserSession> expiredSessions = userSessionRepository.findExpiredSessions(LocalDateTime.now());
        int expiredCount = expiredSessions.size();
        
        if (expiredCount > 0) {
            // Deactivate expired sessions first
            for (UserSession session : expiredSessions) {
                session.deactivate();
            }
            userSessionRepository.saveAll(expiredSessions);
            
            // Delete sessions expired more than 7 days ago
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(7);
            int deletedCount = userSessionRepository.deleteExpiredSessions(cutoffTime);
            
            log.info("Cleanup completed: {} expired sessions deactivated, {} old sessions deleted", 
                    expiredCount, deletedCount);
        }
        
        return expiredCount;
    }

    @Override
    public int cleanupStaleSessions(int inactiveHours) {
        LocalDateTime staleTime = LocalDateTime.now().minusHours(inactiveHours);
        List<UserSession> staleSessions = userSessionRepository.findStaleSessions(staleTime);
        int staleCount = staleSessions.size();
        
        if (staleCount > 0) {
            for (UserSession session : staleSessions) {
                session.deactivate();
            }
            userSessionRepository.saveAll(staleSessions);
            
            log.info("Deactivated {} stale sessions (inactive for more than {} hours)", staleCount, inactiveHours);
        }
        
        return staleCount;
    }

    @Override
    @Transactional(readOnly = true)
    public SessionStats getUserSessionStats(String userId) {
        List<Object> results = userSessionRepository.getSessionStatsByUserId(userId);
        if (results.isEmpty()) {
            return new SessionStats(0, 0, null);
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) results.get(0);
        
        int activeSessions = ((Number) stats.get("activeSessions")).intValue();
        int trustedDevices = ((Number) stats.get("trustedDevices")).intValue();
        LocalDateTime lastActivity = (LocalDateTime) stats.get("lastActivity");
        
        return new SessionStats(activeSessions, trustedDevices, lastActivity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveSessions(String userId) {
        return userSessionRepository.hasActiveSessionsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSession> getSessionsByDeviceFingerprint(String userId, String deviceFingerprint) {
        return userSessionRepository.findActiveSessionsByUserIdAndDeviceFingerprint(userId, deviceFingerprint);
    }

    // ============================================================================
    // PRIVATE HELPER METHODS
    // ============================================================================

    /**
     * Generates a SHA-256 hash of the refresh token for secure storage.
     */
    private String generateTokenHash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return new String(Hex.encode(hash));
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("Failed to hash refresh token", e);
        }
    }

    /**
     * Calculates the expiration time based on device type and trust level.
     */
    private LocalDateTime calculateExpirationTime(DeviceType deviceType, boolean isTrusted) {
        if (isTrusted) {
            return LocalDateTime.now().plusDays(trustedDeviceExpirationDays);
        }
        
        return switch (deviceType) {
            case MOBILE_IOS, MOBILE_ANDROID -> LocalDateTime.now().plusDays(mobileRefreshTokenExpirationDays);
            case WEB, DESKTOP -> LocalDateTime.now().plusHours(refreshTokenExpirationHours);
            default -> LocalDateTime.now().plusHours(refreshTokenExpirationHours);
        };
    }

    /**
     * Removes the oldest session for a user when the session limit is reached.
     */
    private void removeOldestSession(String userId) {
        List<UserSession> sessions = userSessionRepository.findActiveSessionsByUserId(userId);
        if (!sessions.isEmpty()) {
            // Sessions are ordered by lastActivityAt DESC, so the last one is oldest
            UserSession oldestSession = sessions.get(sessions.size() - 1);
            oldestSession.deactivate();
            userSessionRepository.save(oldestSession);
            
            log.info("Removed oldest session {} for user {} to make room for new session", 
                    oldestSession.getSessionId(), userId);
        }
    }
}
