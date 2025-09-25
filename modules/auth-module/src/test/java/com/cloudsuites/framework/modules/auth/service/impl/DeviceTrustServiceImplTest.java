package com.cloudsuites.framework.modules.auth.service.impl;

import com.cloudsuites.framework.modules.auth.entity.DeviceFingerprint;
import com.cloudsuites.framework.modules.auth.repository.DeviceFingerprintRepository;
import com.cloudsuites.framework.services.auth.dto.DeviceFingerprintDto;
import com.cloudsuites.framework.services.auth.dto.DeviceRegistrationRequest;
import com.cloudsuites.framework.services.auth.dto.DeviceVerificationResult;
import com.cloudsuites.framework.services.auth.dto.TrustedDeviceTokenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DeviceTrustServiceImpl.
 * 
 * Tests device trust functionality including device registration,
 * verification, fingerprinting, and token management.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeviceTrustService Unit Tests")
class DeviceTrustServiceImplTest {

    @Mock
    private DeviceFingerprintRepository deviceRepository;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @InjectMocks
    private DeviceTrustServiceImpl deviceTrustService;
    
    private DeviceRegistrationRequest validRegistrationRequest;
    private DeviceFingerprint trustedDevice;
    private String testUserId;
    private String testDeviceInfo;
    private String testFingerprint;
    
    @BeforeEach
    void setUp() {
        // Set configuration values via reflection
        ReflectionTestUtils.setField(deviceTrustService, "defaultExpirationDays", 30);
        ReflectionTestUtils.setField(deviceTrustService, "maxDevicesPerUser", 10);
        ReflectionTestUtils.setField(deviceTrustService, "fingerprintAlgorithm", "SHA-256");
        ReflectionTestUtils.setField(deviceTrustService, "trustedTokenDurationMinutes", 43200);
        ReflectionTestUtils.setField(deviceTrustService, "riskThreshold", 70);
        
        testUserId = "user-123";
        testDeviceInfo = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36";
        testFingerprint = "a1b2c3d4e5f6789012345678901234567890abcdef";
        
        validRegistrationRequest = DeviceRegistrationRequest.builder()
                .userId(testUserId)
                .deviceInfo(testDeviceInfo)
                .deviceName("MacBook Pro")
                .deviceType(DeviceFingerprintDto.DeviceType.LAPTOP)
                .ipAddress("192.168.1.100")
                .trustDevice(true)
                .trustExpirationDays(30)
                .biometricSupported(true)
                .enableExtendedSession(true)
                .build();
        
        trustedDevice = DeviceFingerprint.builder()
                .deviceId("device-123")
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
                .build();
    }
    
    @Test
    @DisplayName("Should register new trusted device successfully")
    void shouldRegisterNewTrustedDeviceSuccessfully() {
        // Given
        when(deviceRepository.countTrustedDevicesByUserId(testUserId, DeviceFingerprintDto.TrustStatus.TRUSTED))
                .thenReturn(3L);
        when(deviceRepository.findByUserIdAndFingerprint(eq(testUserId), anyString()))
                .thenReturn(Optional.empty());
        when(deviceRepository.save(any(DeviceFingerprint.class)))
                .thenAnswer(invocation -> {
                    DeviceFingerprint saved = invocation.getArgument(0);
                    saved.setDeviceId("device-new-123");
                    return saved;
                });
        
        // When
        DeviceFingerprintDto result = deviceTrustService.registerTrustedDevice(validRegistrationRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(testUserId);
        assertThat(result.getDeviceName()).isEqualTo("MacBook Pro");
        assertThat(result.getDeviceType()).isEqualTo(DeviceFingerprintDto.DeviceType.DESKTOP);
        assertThat(result.getTrustStatus()).isEqualTo(DeviceFingerprintDto.TrustStatus.TRUSTED);
        
        ArgumentCaptor<DeviceFingerprint> deviceCaptor = ArgumentCaptor.forClass(DeviceFingerprint.class);
        verify(deviceRepository).save(deviceCaptor.capture());
        
        DeviceFingerprint savedDevice = deviceCaptor.getValue();
        assertThat(savedDevice.getUserId()).isEqualTo(testUserId);
        assertThat(savedDevice.getDeviceName()).isEqualTo("MacBook Pro");
        assertThat(savedDevice.getBiometricCapable()).isTrue();
    }
    
    @Test
    @DisplayName("Should update existing device when registering duplicate")
    void shouldUpdateExistingDeviceWhenRegisteringDuplicate() {
        // Given
        when(deviceRepository.countTrustedDevicesByUserId(testUserId, DeviceFingerprintDto.TrustStatus.TRUSTED))
                .thenReturn(3L);
        when(deviceRepository.findByUserIdAndFingerprint(eq(testUserId), anyString()))
                .thenReturn(Optional.of(trustedDevice));
        when(deviceRepository.save(any(DeviceFingerprint.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        DeviceFingerprintDto result = deviceTrustService.registerTrustedDevice(validRegistrationRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTrustStatus()).isEqualTo(DeviceFingerprintDto.TrustStatus.TRUSTED);
        
        verify(deviceRepository).save(trustedDevice);
        assertThat(trustedDevice.getTrustStatus()).isEqualTo(DeviceFingerprintDto.TrustStatus.TRUSTED);
    }
    
    @Test
    @DisplayName("Should throw exception when device limit exceeded")
    void shouldThrowExceptionWhenDeviceLimitExceeded() {
        // Given
        when(deviceRepository.countTrustedDevicesByUserId(testUserId, DeviceFingerprintDto.TrustStatus.TRUSTED))
                .thenReturn(10L);
        
        // When & Then
        assertThatThrownBy(() -> deviceTrustService.registerTrustedDevice(validRegistrationRequest))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Maximum number of trusted devices reached");
    }
    
    @Test
    @DisplayName("Should throw exception for invalid registration request")
    void shouldThrowExceptionForInvalidRegistrationRequest() {
        // Given
        DeviceRegistrationRequest invalidRequest = DeviceRegistrationRequest.builder()
                .userId("") // Invalid empty user ID
                .deviceInfo(testDeviceInfo)
                .build();
        
        // When & Then
        assertThatThrownBy(() -> deviceTrustService.registerTrustedDevice(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID is required");
    }
    
    @Test
    @DisplayName("Should verify trusted device successfully")
    void shouldVerifyTrustedDeviceSuccessfully() {
        // Given
        when(deviceRepository.findByUserIdAndFingerprint(eq(testUserId), anyString()))
                .thenReturn(Optional.of(trustedDevice));
        when(deviceRepository.save(any(DeviceFingerprint.class)))
                .thenReturn(trustedDevice);
        
        // When
        DeviceVerificationResult result = deviceTrustService.verifyDeviceTrust(testUserId, testDeviceInfo);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIsTrusted()).isTrue();
        assertThat(result.getTrustStatus()).isEqualTo(DeviceFingerprintDto.TrustStatus.TRUSTED);
        assertThat(result.getConfidenceLevel()).isEqualTo(95);
        assertThat(result.getRecommendedAction()).isEqualTo(DeviceVerificationResult.RecommendedAction.ALLOW);
        assertThat(result.getRequiresAdditionalAuth()).isFalse();
        assertThat(result.getAllowExtendedSession()).isTrue();
        
        // Verify activity was updated
        verify(deviceRepository).save(trustedDevice);
        assertThat(trustedDevice.getUsageCount()).isEqualTo(11L);
    }
    
    @Test
    @DisplayName("Should return not trusted for unknown device")
    void shouldReturnNotTrustedForUnknownDevice() {
        // Given
        when(deviceRepository.findByUserIdAndFingerprint(eq(testUserId), anyString()))
                .thenReturn(Optional.empty());
        
        // When
        DeviceVerificationResult result = deviceTrustService.verifyDeviceTrust(testUserId, testDeviceInfo);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIsTrusted()).isFalse();
        assertThat(result.getRecommendedAction()).isEqualTo(DeviceVerificationResult.RecommendedAction.REQUIRE_FULL_AUTH);
        assertThat(result.getRequiresAdditionalAuth()).isTrue();
        assertThat(result.getAllowExtendedSession()).isFalse();
        assertThat(result.getReasonCode()).isEqualTo("DEVICE_NOT_TRUSTED");
    }
    
    @Test
    @DisplayName("Should handle expired device verification")
    void shouldHandleExpiredDeviceVerification() {
        // Given
        trustedDevice.setExpiresAt(LocalDateTime.now().minusDays(1)); // Expired
        when(deviceRepository.findByUserIdAndFingerprint(eq(testUserId), anyString()))
                .thenReturn(Optional.of(trustedDevice));
        when(deviceRepository.save(any(DeviceFingerprint.class)))
                .thenReturn(trustedDevice);
        
        // When
        DeviceVerificationResult result = deviceTrustService.verifyDeviceTrust(testUserId, testDeviceInfo);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIsTrusted()).isFalse();
        assertThat(result.getTrustStatus()).isEqualTo(DeviceFingerprintDto.TrustStatus.EXPIRED);
        assertThat(result.getRecommendedAction()).isEqualTo(DeviceVerificationResult.RecommendedAction.ALLOW_WITH_MFA);
        assertThat(result.getReasonCode()).isEqualTo("DEVICE_TRUST_EXPIRED");
    }
    
    @Test
    @DisplayName("Should generate consistent device fingerprints")
    void shouldGenerateConsistentDeviceFingerprints() {
        // Given
        String deviceInfo1 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36";
        String deviceInfo2 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36";
        
        // When
        String fingerprint1 = deviceTrustService.generateDeviceFingerprint(deviceInfo1);
        String fingerprint2 = deviceTrustService.generateDeviceFingerprint(deviceInfo2);
        
        // Then
        assertThat(fingerprint1).isNotNull();
        assertThat(fingerprint2).isNotNull();
        assertThat(fingerprint1).isEqualTo(fingerprint2);
        assertThat(fingerprint1).hasSize(64); // SHA-256 hex string length
    }
    
    @Test
    @DisplayName("Should generate different fingerprints for different devices")
    void shouldGenerateDifferentFingerprintsForDifferentDevices() {
        // Given
        String deviceInfo1 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36";
        String deviceInfo2 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
        
        // When
        String fingerprint1 = deviceTrustService.generateDeviceFingerprint(deviceInfo1);
        String fingerprint2 = deviceTrustService.generateDeviceFingerprint(deviceInfo2);
        
        // Then
        assertThat(fingerprint1).isNotNull();
        assertThat(fingerprint2).isNotNull();
        assertThat(fingerprint1).isNotEqualTo(fingerprint2);
    }
    
    @Test
    @DisplayName("Should create trusted device token successfully")
    void shouldCreateTrustedDeviceTokenSuccessfully() {
        // Given
        TrustedDeviceTokenRequest tokenRequest = TrustedDeviceTokenRequest.builder()
                .userId(testUserId)
                .deviceFingerprint(testFingerprint)
                .tokenDurationMinutes(43200)
                .includeRefreshToken(true)
                .enableSso(false)
                .build();
        
        when(deviceRepository.findByUserIdAndFingerprint(testUserId, testFingerprint))
                .thenReturn(Optional.of(trustedDevice));
        
        // When
        String token = deviceTrustService.createTrustedDeviceToken(tokenRequest);
        
        // Then
        assertThat(token).isNotNull();
        assertThat(token).contains("TRUSTED_DEVICE_TOKEN");
        assertThat(token).contains(testUserId);
        assertThat(token).contains(testFingerprint);
    }
    
    @Test
    @DisplayName("Should throw exception when creating token for untrusted device")
    void shouldThrowExceptionWhenCreatingTokenForUntrustedDevice() {
        // Given
        TrustedDeviceTokenRequest tokenRequest = TrustedDeviceTokenRequest.builder()
                .userId(testUserId)
                .deviceFingerprint(testFingerprint)
                .build();
        
        when(deviceRepository.findByUserIdAndFingerprint(testUserId, testFingerprint))
                .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> deviceTrustService.createTrustedDeviceToken(tokenRequest))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Device is not trusted");
    }
    
    @Test
    @DisplayName("Should validate trusted device token successfully")
    void shouldValidateTrustedDeviceTokenSuccessfully() {
        // Given
        String validToken = "TRUSTED_DEVICE_TOKEN:user-123:fingerprint:1234567890";
        when(deviceRepository.findByFingerprint(testFingerprint))
                .thenReturn(Optional.of(trustedDevice));
        
        // When
        boolean isValid = deviceTrustService.validateTrustedDeviceToken(validToken, testFingerprint);
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("Should reject invalid tokens")
    void shouldRejectInvalidTokens() {
        // Given
        String invalidToken = "";
        
        // When
        boolean isValid = deviceTrustService.validateTrustedDeviceToken(invalidToken, testFingerprint);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Should revoke trusted device successfully")
    void shouldRevokeTrustedDeviceSuccessfully() {
        // Given
        when(deviceRepository.findByUserIdAndFingerprint(testUserId, testFingerprint))
                .thenReturn(Optional.of(trustedDevice));
        when(deviceRepository.save(any(DeviceFingerprint.class)))
                .thenReturn(trustedDevice);
        
        // When
        boolean revoked = deviceTrustService.revokeTrustedDevice(testUserId, testFingerprint);
        
        // Then
        assertThat(revoked).isTrue();
        verify(deviceRepository).save(trustedDevice);
        assertThat(trustedDevice.getTrustStatus()).isEqualTo(DeviceFingerprintDto.TrustStatus.REVOKED);
        assertThat(trustedDevice.getRevokedAt()).isNotNull();
        assertThat(trustedDevice.getRevocationReason()).isEqualTo("User requested revocation");
    }
    
    @Test
    @DisplayName("Should return false when revoking non-existent device")
    void shouldReturnFalseWhenRevokingNonExistentDevice() {
        // Given
        when(deviceRepository.findByUserIdAndFingerprint(testUserId, testFingerprint))
                .thenReturn(Optional.empty());
        
        // When
        boolean revoked = deviceTrustService.revokeTrustedDevice(testUserId, testFingerprint);
        
        // Then
        assertThat(revoked).isFalse();
        verify(deviceRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should get trusted devices for user")
    void shouldGetTrustedDevicesForUser() {
        // Given
        List<DeviceFingerprint> devices = Arrays.asList(trustedDevice);
        when(deviceRepository.findTrustedDevicesByUserId(testUserId, DeviceFingerprintDto.TrustStatus.TRUSTED))
                .thenReturn(devices);
        
        // When
        List<DeviceFingerprintDto> result = deviceTrustService.getTrustedDevices(testUserId);
        
        // Then
        assertThat(result).hasSize(1);
        DeviceFingerprintDto dto = result.get(0);
        assertThat(dto.getUserId()).isEqualTo(testUserId);
        assertThat(dto.getDeviceName()).isEqualTo("MacBook Pro");
        assertThat(dto.getTrustStatus()).isEqualTo(DeviceFingerprintDto.TrustStatus.TRUSTED);
    }
    
    @Test
    @DisplayName("Should find trusted device by fingerprint")
    void shouldFindTrustedDeviceByFingerprint() {
        // Given
        when(deviceRepository.findByUserIdAndFingerprint(testUserId, testFingerprint))
                .thenReturn(Optional.of(trustedDevice));
        
        // When
        Optional<DeviceFingerprintDto> result = deviceTrustService.findTrustedDevice(testUserId, testFingerprint);
        
        // Then
        assertThat(result).isPresent();
        DeviceFingerprintDto dto = result.get();
        assertThat(dto.getFingerprint()).isEqualTo(testFingerprint);
        assertThat(dto.getTrustStatus()).isEqualTo(DeviceFingerprintDto.TrustStatus.TRUSTED);
    }
    
    @Test
    @DisplayName("Should return empty when device not found or not trusted")
    void shouldReturnEmptyWhenDeviceNotFoundOrNotTrusted() {
        // Given
        trustedDevice.setTrustStatus(DeviceFingerprintDto.TrustStatus.REVOKED);
        when(deviceRepository.findByUserIdAndFingerprint(testUserId, testFingerprint))
                .thenReturn(Optional.of(trustedDevice));
        
        // When
        Optional<DeviceFingerprintDto> result = deviceTrustService.findTrustedDevice(testUserId, testFingerprint);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("Should update device activity")
    void shouldUpdateDeviceActivity() {
        // Given
        when(deviceRepository.findByFingerprint(testFingerprint))
                .thenReturn(Optional.of(trustedDevice));
        when(deviceRepository.save(any(DeviceFingerprint.class)))
                .thenReturn(trustedDevice);
        
        Long originalUsageCount = trustedDevice.getUsageCount();
        
        // When
        deviceTrustService.updateDeviceActivity(testFingerprint);
        
        // Then
        verify(deviceRepository).save(trustedDevice);
        assertThat(trustedDevice.getUsageCount()).isEqualTo(originalUsageCount + 1);
        assertThat(trustedDevice.getLastUsedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("Should cleanup expired devices")
    void shouldCleanupExpiredDevices() {
        // Given
        DeviceFingerprint expiredDevice = DeviceFingerprint.builder()
                .deviceId("expired-123")
                .userId(testUserId)
                .fingerprint("expired-fingerprint")
                .trustStatus(DeviceFingerprintDto.TrustStatus.TRUSTED)
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();
        
        DeviceFingerprint inactiveDevice = DeviceFingerprint.builder()
                .deviceId("inactive-123")
                .userId(testUserId)
                .fingerprint("inactive-fingerprint")
                .trustStatus(DeviceFingerprintDto.TrustStatus.TRUSTED)
                .lastUsedAt(LocalDateTime.now().minusDays(100))
                .build();
        
        when(deviceRepository.findExpiredDevices(any(LocalDateTime.class), eq(DeviceFingerprintDto.TrustStatus.TRUSTED)))
                .thenReturn(Arrays.asList(expiredDevice));
        when(deviceRepository.findInactiveDevices(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(inactiveDevice));
        when(deviceRepository.save(any(DeviceFingerprint.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        int cleanedUp = deviceTrustService.cleanupExpiredDevices();
        
        // Then
        assertThat(cleanedUp).isEqualTo(2);
        verify(deviceRepository, times(2)).save(any(DeviceFingerprint.class));
        assertThat(expiredDevice.getTrustStatus()).isEqualTo(DeviceFingerprintDto.TrustStatus.EXPIRED);
        assertThat(inactiveDevice.getTrustStatus()).isEqualTo(DeviceFingerprintDto.TrustStatus.REVOKED);
        assertThat(inactiveDevice.getRevocationReason()).isEqualTo("Inactive for 90+ days");
    }
}
