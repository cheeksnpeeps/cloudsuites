package com.cloudsuites.framework.modules.auth.repository;

import com.cloudsuites.framework.services.auth.entities.DeviceFingerprint;
import com.cloudsuites.framework.services.auth.entities.TrustStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DeviceFingerprint entity operations.
 * 
 * Provides data access methods for managing device trust relationships,
 * fingerprint lookups, and device security operations.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
@Repository
public interface DeviceFingerprintRepository extends JpaRepository<DeviceFingerprint, String> {

    /**
     * Find a device fingerprint by user ID and fingerprint hash.
     * 
     * @param userId User identifier
     * @param fingerprint Device fingerprint hash
     * @return Optional containing the device if found
     */
    Optional<DeviceFingerprint> findByUserIdAndFingerprint(String userId, String fingerprint);

    /**
     * Find a device fingerprint by fingerprint hash only.
     * 
     * @param fingerprint Device fingerprint hash
     * @return Optional containing the device if found
     */
    Optional<DeviceFingerprint> findByFingerprint(String fingerprint);

    /**
     * Find all trusted devices for a specific user.
     * 
     * @param userId User identifier
     * @return List of trusted devices for the user
     */
    @Query("SELECT d FROM DeviceFingerprint d WHERE d.userId = :userId " +
           "AND d.trustStatus = :trustStatus AND d.isDeleted = false " +
           "ORDER BY d.lastUsedAt DESC")
    List<DeviceFingerprint> findTrustedDevicesByUserId(@Param("userId") String userId, 
                                                       @Param("trustStatus") TrustStatus trustStatus);

    /**
     * Find all devices for a user regardless of trust status.
     * 
     * @param userId User identifier
     * @return List of all devices for the user
     */
    @Query("SELECT d FROM DeviceFingerprint d WHERE d.userId = :userId " +
           "AND d.isDeleted = false ORDER BY d.registeredAt DESC")
    List<DeviceFingerprint> findAllDevicesByUserId(@Param("userId") String userId);

    /**
     * Find devices that have expired.
     * 
     * @param currentTime Current timestamp for comparison
     * @return List of expired devices
     */
    @Query("SELECT d FROM DeviceFingerprint d WHERE d.expiresAt < :currentTime " +
           "AND d.trustStatus = :trustStatus AND d.isDeleted = false")
    List<DeviceFingerprint> findExpiredDevices(@Param("currentTime") LocalDateTime currentTime,
                                              @Param("trustStatus") TrustStatus trustStatus);

    /**
     * Find devices that haven't been used within the specified period.
     * 
     * @param cutoffDate Date before which devices are considered inactive
     * @return List of inactive devices
     */
    @Query("SELECT d FROM DeviceFingerprint d WHERE d.lastUsedAt < :cutoffDate " +
           "AND d.isDeleted = false ORDER BY d.lastUsedAt ASC")
    List<DeviceFingerprint> findInactiveDevices(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Count trusted devices for a user.
     * 
     * @param userId User identifier
     * @return Number of trusted devices
     */
    @Query("SELECT COUNT(d) FROM DeviceFingerprint d WHERE d.userId = :userId " +
           "AND d.trustStatus = :trustStatus AND d.isDeleted = false")
    Long countTrustedDevicesByUserId(@Param("userId") String userId,
                                    @Param("trustStatus") TrustStatus trustStatus);

    /**
     * Update last used timestamp and usage count for a device.
     * 
     * @param deviceId Device identifier
     * @param lastUsedAt New last used timestamp
     * @param newUsageCount New usage count
     * @param ipAddress Current IP address
     * @return Number of updated records
     */
    @Modifying
    @Query("UPDATE DeviceFingerprint d SET d.lastUsedAt = :lastUsedAt, " +
           "d.usageCount = :newUsageCount, d.lastIpAddress = :ipAddress " +
           "WHERE d.deviceId = :deviceId")
    int updateDeviceActivity(@Param("deviceId") String deviceId,
                           @Param("lastUsedAt") LocalDateTime lastUsedAt,
                           @Param("newUsageCount") Long newUsageCount,
                           @Param("ipAddress") String ipAddress);

    /**
     * Update device trust status.
     * 
     * @param deviceId Device identifier
     * @param trustStatus New trust status
     * @param modifiedBy Who modified the status
     * @return Number of updated records
     */
    @Modifying
    @Query("UPDATE DeviceFingerprint d SET d.trustStatus = :trustStatus, " +
           "d.lastModifiedBy = :modifiedBy, d.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE d.deviceId = :deviceId")
    int updateTrustStatus(@Param("deviceId") String deviceId,
                         @Param("trustStatus") TrustStatus trustStatus,
                         @Param("modifiedBy") String modifiedBy);

    /**
     * Revoke device trust.
     * 
     * @param deviceId Device identifier
     * @param revokedAt Revocation timestamp
     * @param revocationReason Reason for revocation
     * @param modifiedBy Who revoked the trust
     * @return Number of updated records
     */
    @Modifying
    @Query("UPDATE DeviceFingerprint d SET d.trustStatus = :revokedStatus, " +
           "d.revokedAt = :revokedAt, d.revocationReason = :revocationReason, " +
           "d.lastModifiedBy = :modifiedBy, d.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE d.deviceId = :deviceId")
    int revokeDeviceTrust(@Param("deviceId") String deviceId,
                         @Param("revokedStatus") TrustStatus revokedStatus,
                         @Param("revokedAt") LocalDateTime revokedAt,
                         @Param("revocationReason") String revocationReason,
                         @Param("modifiedBy") String modifiedBy);

    /**
     * Soft delete expired devices.
     * 
     * @param cutoffDate Date before which devices should be deleted
     * @param modifiedBy Who performed the cleanup
     * @return Number of deleted records
     */
    @Modifying
    @Query("UPDATE DeviceFingerprint d SET d.isDeleted = true, " +
           "d.lastModifiedBy = :modifiedBy, d.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE d.expiresAt < :cutoffDate AND d.isDeleted = false")
    int softDeleteExpiredDevices(@Param("cutoffDate") LocalDateTime cutoffDate,
                                @Param("modifiedBy") String modifiedBy);

    /**
     * Find devices by IP address for security analysis.
     * 
     * @param ipAddress IP address to search
     * @param fromDate Start date for the search
     * @return List of devices that used the IP address
     */
    @Query("SELECT d FROM DeviceFingerprint d WHERE " +
           "(d.registrationIp = :ipAddress OR d.lastIpAddress = :ipAddress) " +
           "AND d.registeredAt >= :fromDate AND d.isDeleted = false " +
           "ORDER BY d.registeredAt DESC")
    List<DeviceFingerprint> findDevicesByIpAddress(@Param("ipAddress") String ipAddress,
                                                  @Param("fromDate") LocalDateTime fromDate);

    /**
     * Check if a user has reached the maximum number of trusted devices.
     * 
     * @param userId User identifier
     * @param maxDevices Maximum allowed trusted devices
     * @return True if user has reached the limit
     */
    @Query("SELECT COUNT(d) >= :maxDevices FROM DeviceFingerprint d WHERE d.userId = :userId " +
           "AND d.trustStatus = :trustStatus AND d.isDeleted = false")
    boolean hasReachedDeviceLimit(@Param("userId") String userId,
                                 @Param("maxDevices") int maxDevices,
                                 @Param("trustStatus") TrustStatus trustStatus);

    /**
     * Find devices with high risk scores.
     * 
     * @param minRiskScore Minimum risk score threshold
     * @return List of high-risk devices
     */
    @Query("SELECT d FROM DeviceFingerprint d WHERE d.riskScore >= :minRiskScore " +
           "AND d.isDeleted = false ORDER BY d.riskScore DESC, d.lastUsedAt DESC")
    List<DeviceFingerprint> findHighRiskDevices(@Param("minRiskScore") int minRiskScore);

    /**
     * Find devices that need risk score updates (haven't been updated recently).
     * 
     * @param cutoffDate Date before which risk scores should be updated
     * @return List of devices needing risk assessment updates
     */
    @Query("SELECT d FROM DeviceFingerprint d WHERE d.updatedAt < :cutoffDate " +
           "AND d.trustStatus = :trustStatus AND d.isDeleted = false " +
           "ORDER BY d.updatedAt ASC")
    List<DeviceFingerprint> findDevicesNeedingRiskUpdate(@Param("cutoffDate") LocalDateTime cutoffDate,
                                                        @Param("trustStatus") TrustStatus trustStatus);
}
