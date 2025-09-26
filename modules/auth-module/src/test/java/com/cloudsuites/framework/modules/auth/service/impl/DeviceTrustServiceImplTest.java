package com.cloudsuites.framework.modules.auth.service.impl;

import com.cloudsuites.framework.services.auth.entities.DeviceFingerprint;
import com.cloudsuites.framework.modules.auth.repository.DeviceFingerprintRepository;
import com.cloudsuites.framework.services.auth.entities.DeviceVerificationResult;
import com.cloudsuites.framework.services.auth.entities.TrustStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Simplified unit tests for DeviceTrustServiceImpl focusing on core functionality.
 */
@ExtendWith(MockitoExtension.class)
class DeviceTrustServiceImplTest {

    @Mock
    private DeviceFingerprintRepository deviceFingerprintRepository;

    @InjectMocks
    private DeviceTrustServiceImpl deviceTrustService;

    @Test
    void testGenerateDeviceFingerprint() {
        // Test data
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

        // Execute method
        String fingerprint = deviceTrustService.generateDeviceFingerprint(userAgent);

        // Verify result
        assertNotNull(fingerprint);
        assertEquals(64, fingerprint.length()); // SHA-256 hex string is 64 characters
        assertTrue(fingerprint.matches("[a-f0-9]+"));
    }

    @Test
    void testVerifyDeviceTrust() {
        // Setup test data
        String userId = "test-user-123";
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
        
        // Generate the expected fingerprint for mocking
        String expectedFingerprint = deviceTrustService.generateDeviceFingerprint(userAgent);
        
        DeviceFingerprint mockDevice = DeviceFingerprint.builder()
                .userId(userId)
                .fingerprint(expectedFingerprint)
                .trustStatus(TrustStatus.TRUSTED)
                .registeredAt(LocalDateTime.now())
                .lastUsedAt(LocalDateTime.now())
                .build();

        // Mock repository behavior
        when(deviceFingerprintRepository.findByUserIdAndFingerprint(userId, expectedFingerprint))
                .thenReturn(Optional.of(mockDevice));

        // Execute method
        DeviceVerificationResult result = deviceTrustService.verifyDeviceTrust(userId, userAgent);

        // Verify result
        assertTrue(result.isTrusted());
        verify(deviceFingerprintRepository).findByUserIdAndFingerprint(userId, expectedFingerprint);
    }

    @Test
    void testHandleUnknownDevice() {
        // Setup test data
        String userId = "test-user-123";
        String userAgent = "Unknown-Browser/1.0";
        
        // Generate the expected fingerprint for mocking
        String expectedFingerprint = deviceTrustService.generateDeviceFingerprint(userAgent);

        // Mock repository to return empty (unknown device)
        when(deviceFingerprintRepository.findByUserIdAndFingerprint(userId, expectedFingerprint))
                .thenReturn(Optional.empty());

        // Execute method
        DeviceVerificationResult result = deviceTrustService.verifyDeviceTrust(userId, userAgent);

        // Verify result
        assertFalse(result.isTrusted());
        verify(deviceFingerprintRepository).findByUserIdAndFingerprint(userId, expectedFingerprint);
    }
}