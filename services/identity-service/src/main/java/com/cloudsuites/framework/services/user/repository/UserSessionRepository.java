package com.cloudsuites.framework.services.user.repository;

import com.cloudsuites.framework.services.user.entities.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing user sessions and refresh tokens.
 * Supports token rotation, device management, and session lifecycle operations.
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {

    /**
     * Find an active session by refresh token hash.
     */
    @Query("SELECT s FROM UserSession s WHERE s.refreshTokenHash = :tokenHash AND s.isActive = true")
    Optional<UserSession> findByRefreshTokenHashAndActive(@Param("tokenHash") String tokenHash);

    /**
     * Find all active sessions for a specific user.
     */
    @Query("SELECT s FROM UserSession s WHERE s.userId = :userId AND s.isActive = true ORDER BY s.lastActivityAt DESC")
    List<UserSession> findActiveSessionsByUserId(@Param("userId") String userId);

    /**
     * Find all sessions for a specific user (active and inactive).
     */
    @Query("SELECT s FROM UserSession s WHERE s.userId = :userId ORDER BY s.lastActivityAt DESC")
    List<UserSession> findAllSessionsByUserId(@Param("userId") String userId);

    /**
     * Find active sessions by user ID and device fingerprint.
     */
    @Query("SELECT s FROM UserSession s WHERE s.userId = :userId AND s.deviceFingerprint = :deviceFingerprint AND s.isActive = true")
    List<UserSession> findActiveSessionsByUserIdAndDeviceFingerprint(@Param("userId") String userId, 
                                                                      @Param("deviceFingerprint") String deviceFingerprint);

    /**
     * Find session by access token JTI (for logout operations).
     */
    @Query("SELECT s FROM UserSession s WHERE s.accessTokenJti = :jti AND s.isActive = true")
    Optional<UserSession> findByAccessTokenJti(@Param("jti") String jti);

    /**
     * Count active sessions for a user.
     */
    @Query("SELECT COUNT(s) FROM UserSession s WHERE s.userId = :userId AND s.isActive = true")
    long countActiveSessionsByUserId(@Param("userId") String userId);

    /**
     * Find expired sessions that need cleanup.
     */
    @Query("SELECT s FROM UserSession s WHERE s.expiresAt < :currentTime")
    List<UserSession> findExpiredSessions(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find stale sessions (inactive for more than specified hours).
     */
    @Query("SELECT s FROM UserSession s WHERE s.lastActivityAt < :staleTime AND s.isActive = true")
    List<UserSession> findStaleSessions(@Param("staleTime") LocalDateTime staleTime);

    /**
     * Deactivate all sessions for a specific user (logout all devices).
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false, s.lastModifiedAt = :modifiedAt WHERE s.userId = :userId AND s.isActive = true")
    int deactivateAllUserSessions(@Param("userId") String userId, @Param("modifiedAt") LocalDateTime modifiedAt);

    /**
     * Deactivate a specific session.
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false, s.lastModifiedAt = :modifiedAt WHERE s.sessionId = :sessionId")
    int deactivateSession(@Param("sessionId") String sessionId, @Param("modifiedAt") LocalDateTime modifiedAt);

    /**
     * Deactivate sessions by refresh token hash.
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false, s.lastModifiedAt = :modifiedAt WHERE s.refreshTokenHash = :tokenHash")
    int deactivateSessionByRefreshToken(@Param("tokenHash") String tokenHash, @Param("modifiedAt") LocalDateTime modifiedAt);

    /**
     * Delete expired sessions (cleanup operation).
     */
    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.expiresAt < :cutoffTime")
    int deleteExpiredSessions(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Update last activity time for a session.
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.lastActivityAt = :activityTime, s.lastModifiedAt = :modifiedAt WHERE s.sessionId = :sessionId")
    int updateLastActivity(@Param("sessionId") String sessionId, 
                          @Param("activityTime") LocalDateTime activityTime,
                          @Param("modifiedAt") LocalDateTime modifiedAt);

    /**
     * Find trusted device sessions for a user.
     */
    @Query("SELECT s FROM UserSession s WHERE s.userId = :userId AND s.isTrustedDevice = true AND s.isActive = true")
    List<UserSession> findTrustedDeviceSessionsByUserId(@Param("userId") String userId);

    /**
     * Find sessions by device type for a user.
     */
    @Query("SELECT s FROM UserSession s WHERE s.userId = :userId AND s.deviceType = :deviceType AND s.isActive = true")
    List<UserSession> findActiveSessionsByUserIdAndDeviceType(@Param("userId") String userId, 
                                                               @Param("deviceType") com.cloudsuites.framework.services.user.entities.DeviceType deviceType);

    /**
     * Check if user has any active sessions.
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM UserSession s WHERE s.userId = :userId AND s.isActive = true")
    boolean hasActiveSessionsByUserId(@Param("userId") String userId);

    /**
     * Get session statistics for a user.
     */
    @Query("SELECT new map(" +
           "COUNT(CASE WHEN s.isActive = true THEN 1 END) as activeSessions, " +
           "COUNT(CASE WHEN s.isTrustedDevice = true AND s.isActive = true THEN 1 END) as trustedDevices, " +
           "MAX(s.lastActivityAt) as lastActivity) " +
           "FROM UserSession s WHERE s.userId = :userId")
    List<Object> getSessionStatsByUserId(@Param("userId") String userId);
}
