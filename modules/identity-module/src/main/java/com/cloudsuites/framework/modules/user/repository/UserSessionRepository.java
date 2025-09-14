package com.cloudsuites.framework.modules.user.repository;

import com.cloudsuites.framework.services.user.entities.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {

    Optional<UserSession> findByRefreshTokenHashAndActive(String refreshTokenHash, Boolean active);

    List<UserSession> findByUserIdAndActive(String userId, Boolean active);

    @Query("SELECT s FROM UserSession s WHERE s.userId = :userId AND s.active = true")
    List<UserSession> findActiveSessionsByUserId(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.active = false, s.lastModifiedAt = :updatedAt WHERE s.userId = :userId AND s.active = true")
    int deactivateAllUserSessions(@Param("userId") String userId, @Param("updatedAt") LocalDateTime updatedAt);

    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.active = false, s.lastModifiedAt = :updatedAt WHERE s.sessionId = :sessionId")
    int deactivateSession(@Param("sessionId") String sessionId, @Param("updatedAt") LocalDateTime updatedAt);

    @Query("SELECT s FROM UserSession s WHERE s.tokenExpiryDate < :currentTime AND s.active = true")
    List<UserSession> findExpiredSessions(@Param("currentTime") LocalDateTime currentTime);

    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.active = false, s.lastModifiedAt = :updatedAt WHERE s.tokenExpiryDate < :currentTime AND s.active = true")
    int cleanupExpiredSessions(@Param("currentTime") LocalDateTime currentTime, @Param("updatedAt") LocalDateTime updatedAt);

    @Query("SELECT COUNT(s) FROM UserSession s WHERE s.userId = :userId AND s.active = true")
    long countActiveSessionsByUserId(@Param("userId") String userId);

    List<UserSession> findByIpAddressAndUserAgentAndActive(String ipAddress, String userAgent, Boolean active);

    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.active = false, s.lastModifiedAt = :updatedAt WHERE s.refreshTokenHash = :tokenHash AND s.active = true")
    int deactivateSessionByRefreshToken(@Param("tokenHash") String tokenHash, @Param("updatedAt") LocalDateTime updatedAt);

    Optional<UserSession> findByAccessTokenJti(String accessTokenJti);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserSession s WHERE s.tokenExpiryDate < :cutoffTime")
    int deleteExpiredSessions(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT s FROM UserSession s WHERE s.lastActivity < :staleTime AND s.active = true")
    List<UserSession> findStaleSessions(@Param("staleTime") LocalDateTime staleTime);

    @Query("SELECT COUNT(s), MAX(s.lastActivity), MIN(s.createdAt) FROM UserSession s WHERE s.userId = :userId AND s.active = true")
    List<Object> getSessionStatsByUserId(@Param("userId") String userId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM UserSession s WHERE s.userId = :userId AND s.active = true")
    boolean hasActiveSessionsByUserId(@Param("userId") String userId);

    @Query("SELECT s FROM UserSession s WHERE s.userId = :userId AND s.deviceFingerprint = :deviceFingerprint AND s.active = true")
    List<UserSession> findActiveSessionsByUserIdAndDeviceFingerprint(@Param("userId") String userId, @Param("deviceFingerprint") String deviceFingerprint);
}
