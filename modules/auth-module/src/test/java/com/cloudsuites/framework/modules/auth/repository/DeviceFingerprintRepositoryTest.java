package com.cloudsuites.framework.modules.auth.repository;

import com.cloudsuites.framework.modules.auth.entity.DeviceFingerprint;
import com.cloudsuites.framework.services.auth.dto.DeviceFingerprintDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for DeviceFingerprintRepository.
 * 
 * Tests repository operations for device trust management
 * including queries, updates, and data integrity.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
@DataJpaTest(excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration.class
})
@ContextConfiguration(classes = {TestJpaConfig.class})
@ActiveProfiles("test")
@DisplayName("DeviceFingerprint Repository Integration Tests")
class DeviceFingerprintRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private DeviceFingerprintRepository deviceRepository;
    
    private DeviceFingerprint trustedDevice;
    private DeviceFingerprint expiredDevice;
    private DeviceFingerprint revokedDevice;
    private String testUserId;
    private String testFingerprint;
    
    @BeforeEach
    void setUp() {
        testUserId = "user-123";
        testFingerprint = "sha256-trusted-device-fingerprint-hash-123456789abcdef0";
        
        trustedDevice = DeviceFingerprint.builder()
                .deviceId("device-trusted-123")
                .userId(testUserId)
                .fingerprint(testFingerprint)
                .deviceName("MacBook Pro")
                .deviceType(DeviceFingerprintDto.DeviceType.LAPTOP)
                .osInfo("macOS")
                .browserInfo("Chrome")
                .registrationIp("192.168.1.100")
                .trustStatus(DeviceFingerprintDto.TrustStatus.TRUSTED)
                .registeredAt(LocalDateTime.now().minusDays(5))
                .lastUsedAt(LocalDateTime.now().minusHours(2))
                .expiresAt(LocalDateTime.now().plusDays(25))
                .riskScore(15)
                .usageCount(10L)
                .biometricCapable(true)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)")
                .lastIpAddress("192.168.1.100")
                .createdBy(testUserId)
                .isDeleted(false)
                .build();
        
        expiredDevice = DeviceFingerprint.builder()
                .deviceId("device-expired-123")
                .userId(testUserId)
                .fingerprint("sha256-expired-device-fingerprint-hash-987654321fedcba0")
                .deviceName("iPhone 14")
                .deviceType(DeviceFingerprintDto.DeviceType.MOBILE)
                .osInfo("iOS")
                .browserInfo("Safari")
                .registrationIp("192.168.1.101")
                .trustStatus(DeviceFingerprintDto.TrustStatus.TRUSTED)
                .registeredAt(LocalDateTime.now().minusDays(40))
                .lastUsedAt(LocalDateTime.now().minusDays(5))
                .expiresAt(LocalDateTime.now().minusDays(1)) // Expired
                .riskScore(30)
                .usageCount(5L)
                .biometricCapable(true)
                .createdBy(testUserId)
                .isDeleted(false)
                .build();
        
        revokedDevice = DeviceFingerprint.builder()
                .deviceId("device-revoked-123")
                .userId("user-456")
                .fingerprint("sha256-revoked-device-fingerprint-hash-abcdef0123456789")
                .deviceName("Windows PC")
                .deviceType(DeviceFingerprintDto.DeviceType.DESKTOP)
                .osInfo("Windows")
                .browserInfo("Edge")
                .registrationIp("192.168.1.102")
                .trustStatus(DeviceFingerprintDto.TrustStatus.REVOKED)
                .registeredAt(LocalDateTime.now().minusDays(10))
                .lastUsedAt(LocalDateTime.now().minusDays(3))
                .revokedAt(LocalDateTime.now().minusDays(1))
                .revocationReason("Security breach")
                .riskScore(85)
                .usageCount(20L)
                .biometricCapable(false)
                .createdBy("user-456")
                .isDeleted(false)
                .build();
        
        entityManager.persistAndFlush(trustedDevice);
        entityManager.persistAndFlush(expiredDevice);
        entityManager.persistAndFlush(revokedDevice);
    }
    
    @Test
    @DisplayName("Should find device by user ID and fingerprint")
    void shouldFindDeviceByUserIdAndFingerprint() {
        // When
        Optional<DeviceFingerprint> result = deviceRepository.findByUserIdAndFingerprint(testUserId, testFingerprint);
        
        // Then
        assertThat(result).isPresent();
        DeviceFingerprint device = result.get();
        assertThat(device.getUserId()).isEqualTo(testUserId);
        assertThat(device.getFingerprint()).isEqualTo(testFingerprint);
        assertThat(device.getDeviceName()).isEqualTo("MacBook Pro");
        assertThat(device.getTrustStatus()).isEqualTo(DeviceFingerprintDto.TrustStatus.TRUSTED);
    }
    
    @Test
    @DisplayName("Should return empty when device not found by user and fingerprint")
    void shouldReturnEmptyWhenDeviceNotFoundByUserAndFingerprint() {
        // When
        Optional<DeviceFingerprint> result = deviceRepository.findByUserIdAndFingerprint("non-existent-user", "non-existent-fingerprint");
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("Should find device by fingerprint only")
    void shouldFindDeviceByFingerprintOnly() {
        // When
        Optional<DeviceFingerprint> result = deviceRepository.findByFingerprint(testFingerprint);
        
        // Then
        assertThat(result).isPresent();
        DeviceFingerprint device = result.get();
        assertThat(device.getFingerprint()).isEqualTo(testFingerprint);
        assertThat(device.getUserId()).isEqualTo(testUserId);
    }
    
    @Test
    @DisplayName("Should find trusted devices by user ID")
    void shouldFindTrustedDevicesByUserId() {
        // When
        List<DeviceFingerprint> trustedDevices = deviceRepository.findTrustedDevicesByUserId(
                testUserId, DeviceFingerprintDto.TrustStatus.TRUSTED);
        
        // Then
        assertThat(trustedDevices).hasSize(2); // trustedDevice and expiredDevice (both have TRUSTED status)
        assertThat(trustedDevices).allMatch(device -> device.getUserId().equals(testUserId));
        assertThat(trustedDevices).allMatch(device -> device.getTrustStatus() == DeviceFingerprintDto.TrustStatus.TRUSTED);
        assertThat(trustedDevices).allMatch(device -> !device.getIsDeleted());
        
        // Verify ordering by last used date (DESC)
        for (int i = 0; i < trustedDevices.size() - 1; i++) {
            LocalDateTime current = trustedDevices.get(i).getLastUsedAt();
            LocalDateTime next = trustedDevices.get(i + 1).getLastUsedAt();
            if (current != null && next != null) {
                assertThat(current).isAfterOrEqualTo(next);
            }
        }
    }
    
    @Test
    @DisplayName("Should find all devices by user ID regardless of status")
    void shouldFindAllDevicesByUserId() {
        // When
        List<DeviceFingerprint> allDevices = deviceRepository.findAllDevicesByUserId(testUserId);
        
        // Then
        assertThat(allDevices).hasSize(2); // trustedDevice and expiredDevice
        assertThat(allDevices).allMatch(device -> device.getUserId().equals(testUserId));
        assertThat(allDevices).allMatch(device -> !device.getIsDeleted());
        
        // Verify ordering by registered date (DESC)
        for (int i = 0; i < allDevices.size() - 1; i++) {
            LocalDateTime current = allDevices.get(i).getRegisteredAt();
            LocalDateTime next = allDevices.get(i + 1).getRegisteredAt();
            assertThat(current).isAfterOrEqualTo(next);
        }
    }
    
    @Test
    @DisplayName("Should find expired devices")
    void shouldFindExpiredDevices() {
        // When
        List<DeviceFingerprint> expiredDevices = deviceRepository.findExpiredDevices(
                LocalDateTime.now(), DeviceFingerprintDto.TrustStatus.TRUSTED);
        
        // Then
        assertThat(expiredDevices).hasSize(1);
        DeviceFingerprint device = expiredDevices.get(0);
        assertThat(device.getDeviceId()).isEqualTo("device-expired-123");
        assertThat(device.getExpiresAt()).isBefore(LocalDateTime.now());
        assertThat(device.getTrustStatus()).isEqualTo(DeviceFingerprintDto.TrustStatus.TRUSTED);
    }
    
    @Test
    @DisplayName("Should find inactive devices")
    void shouldFindInactiveDevices() {
        // When
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(3);
        List<DeviceFingerprint> inactiveDevices = deviceRepository.findInactiveDevices(cutoffDate);
        
        // Then
        assertThat(inactiveDevices).hasSize(2); // Both expired and revoked devices are older than 3 days
        // Verify devices are ordered by lastUsedAt (ASC)
        assertThat(inactiveDevices.get(0).getLastUsedAt()).isBefore(cutoffDate);
        assertThat(inactiveDevices.get(1).getLastUsedAt()).isBefore(cutoffDate);
        // First device should be older (expired device is 5 days old, revoked is 3 days old)
        assertThat(inactiveDevices.get(0).getLastUsedAt()).isBefore(inactiveDevices.get(1).getLastUsedAt());
    }
    
    @Test
    @DisplayName("Should count trusted devices by user ID")
    void shouldCountTrustedDevicesByUserId() {
        // When
        Long count = deviceRepository.countTrustedDevicesByUserId(testUserId, DeviceFingerprintDto.TrustStatus.TRUSTED);
        
        // Then
        assertThat(count).isEqualTo(2L); // trustedDevice and expiredDevice
    }
    
    @Test
    @DisplayName("Should return zero count for user with no trusted devices")
    void shouldReturnZeroCountForUserWithNoTrustedDevices() {
        // When
        Long count = deviceRepository.countTrustedDevicesByUserId("non-existent-user", DeviceFingerprintDto.TrustStatus.TRUSTED);
        
        // Then
        assertThat(count).isEqualTo(0L);
    }
    
    @Test
    @DisplayName("Should update device activity")
    void shouldUpdateDeviceActivity() {
        // Given
        LocalDateTime newLastUsed = LocalDateTime.now();
        Long newUsageCount = 15L;
        String newIpAddress = "192.168.1.200";
        
        // When
        int updatedRows = deviceRepository.updateDeviceActivity(
                trustedDevice.getDeviceId(), newLastUsed, newUsageCount, newIpAddress);
        
        // Then
        assertThat(updatedRows).isEqualTo(1);
        
        // Verify the update by fetching the entity
        entityManager.clear();
        DeviceFingerprint updated = entityManager.find(DeviceFingerprint.class, trustedDevice.getDeviceId());
        assertThat(updated.getLastUsedAt()).isAfter(newLastUsed.minusMinutes(1))
                .isBefore(newLastUsed.plusMinutes(1));
        assertThat(updated.getUsageCount()).isEqualTo(newUsageCount);
        assertThat(updated.getLastIpAddress()).isEqualTo(newIpAddress);
    }
    
    @Test
    @DisplayName("Should update trust status")
    void shouldUpdateTrustStatus() {
        // Given
        String modifiedBy = "admin-user";
        
        // When
        int updatedRows = deviceRepository.updateTrustStatus(
                trustedDevice.getDeviceId(), DeviceFingerprintDto.TrustStatus.SUSPENDED, modifiedBy);
        
        // Then
        assertThat(updatedRows).isEqualTo(1);
        
        // Verify the update
        entityManager.clear();
        DeviceFingerprint updated = entityManager.find(DeviceFingerprint.class, trustedDevice.getDeviceId());
        assertThat(updated.getTrustStatus()).isEqualTo(DeviceFingerprintDto.TrustStatus.SUSPENDED);
        assertThat(updated.getLastModifiedBy()).isEqualTo(modifiedBy);
        assertThat(updated.getUpdatedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("Should revoke device trust")
    void shouldRevokeDeviceTrust() {
        // Given
        LocalDateTime revokedAt = LocalDateTime.now();
        String revocationReason = "User request";
        String modifiedBy = "system";
        
        // When
        int updatedRows = deviceRepository.revokeDeviceTrust(
                trustedDevice.getDeviceId(), DeviceFingerprintDto.TrustStatus.REVOKED, 
                revokedAt, revocationReason, modifiedBy);
        
        // Then
        assertThat(updatedRows).isEqualTo(1);
        
        // Verify the update
        entityManager.clear();
        DeviceFingerprint updated = entityManager.find(DeviceFingerprint.class, trustedDevice.getDeviceId());
        assertThat(updated.getTrustStatus()).isEqualTo(DeviceFingerprintDto.TrustStatus.REVOKED);
        assertThat(updated.getRevokedAt()).isAfter(revokedAt.minusMinutes(1))
                .isBefore(revokedAt.plusMinutes(1));
        assertThat(updated.getRevocationReason()).isEqualTo(revocationReason);
        assertThat(updated.getLastModifiedBy()).isEqualTo(modifiedBy);
    }
    
    @Test
    @DisplayName("Should soft delete expired devices")
    void shouldSoftDeleteExpiredDevices() {
        // Given
        LocalDateTime cutoffDate = LocalDateTime.now();
        String modifiedBy = "cleanup-job";
        
        // When
        int deletedRows = deviceRepository.softDeleteExpiredDevices(cutoffDate, modifiedBy);
        
        // Then
        assertThat(deletedRows).isEqualTo(1); // Only expiredDevice should be deleted
        
        // Verify the soft delete
        entityManager.clear();
        DeviceFingerprint deleted = entityManager.find(DeviceFingerprint.class, expiredDevice.getDeviceId());
        assertThat(deleted.getIsDeleted()).isTrue();
        assertThat(deleted.getLastModifiedBy()).isEqualTo(modifiedBy);
    }
    
    @Test
    @DisplayName("Should find devices by IP address")
    void shouldFindDevicesByIpAddress() {
        // Given
        String ipAddress = "192.168.1.100";
        LocalDateTime fromDate = LocalDateTime.now().minusDays(10);
        
        // When
        List<DeviceFingerprint> devices = deviceRepository.findDevicesByIpAddress(ipAddress, fromDate);
        
        // Then
        assertThat(devices).hasSize(1);
        DeviceFingerprint device = devices.get(0);
        assertThat(device.getDeviceId()).isEqualTo(trustedDevice.getDeviceId());
        assertThat(device.getRegistrationIp()).isEqualTo(ipAddress);
    }
    
    @Test
    @DisplayName("Should check if user has reached device limit")
    void shouldCheckIfUserHasReachedDeviceLimit() {
        // When
        boolean reachedLimit = deviceRepository.hasReachedDeviceLimit(
                testUserId, 2, DeviceFingerprintDto.TrustStatus.TRUSTED);
        
        // Then
        assertThat(reachedLimit).isTrue(); // User has 2 trusted devices, limit is 2
        
        // Test with higher limit
        boolean notReachedLimit = deviceRepository.hasReachedDeviceLimit(
                testUserId, 5, DeviceFingerprintDto.TrustStatus.TRUSTED);
        assertThat(notReachedLimit).isFalse();
    }
    
    @Test
    @DisplayName("Should find high risk devices")
    void shouldFindHighRiskDevices() {
        // When
        List<DeviceFingerprint> highRiskDevices = deviceRepository.findHighRiskDevices(50);
        
        // Then
        assertThat(highRiskDevices).hasSize(1);
        DeviceFingerprint device = highRiskDevices.get(0);
        assertThat(device.getDeviceId()).isEqualTo(revokedDevice.getDeviceId());
        assertThat(device.getRiskScore()).isEqualTo(85);
    }
    
    @Test
    @DisplayName("Should find devices needing risk update")
    void shouldFindDevicesNeedingRiskUpdate() {
        // Given
        LocalDateTime cutoffDate = LocalDateTime.now().plusDays(1); // All devices need update
        
        // When
        List<DeviceFingerprint> devicesNeedingUpdate = deviceRepository.findDevicesNeedingRiskUpdate(
                cutoffDate, DeviceFingerprintDto.TrustStatus.TRUSTED);
        
        // Then
        assertThat(devicesNeedingUpdate).hasSize(2); // trustedDevice and expiredDevice
        assertThat(devicesNeedingUpdate).allMatch(device -> 
            device.getTrustStatus() == DeviceFingerprintDto.TrustStatus.TRUSTED);
    }
    
    @Test
    @DisplayName("Should handle entity lifecycle callbacks")
    void shouldHandleEntityLifecycleCallbacks() {
        // Given
        DeviceFingerprint newDevice = DeviceFingerprint.builder()
                .userId("user-789")
                .fingerprint("sha256-new-device-fingerprint-hash-1234567890abcdef")
                .deviceName("Test Device")
                .deviceType(DeviceFingerprintDto.DeviceType.MOBILE)
                .trustStatus(DeviceFingerprintDto.TrustStatus.PENDING)
                .build();
        
        // When
        DeviceFingerprint saved = deviceRepository.saveAndFlush(newDevice);
        
        // Then
        assertThat(saved.getDeviceId()).isNotNull(); // Should be auto-generated by @PrePersist
        assertThat(saved.getRegisteredAt()).isNotNull(); // Should be auto-set by @CreationTimestamp
    }
}
