package com.cloudsuites.framework.modules.auth.service.impl;

import com.cloudsuites.framework.modules.auth.repository.DeviceFingerprintRepository;
import com.cloudsuites.framework.services.auth.DeviceTrustService;
import com.cloudsuites.framework.services.auth.entities.DeviceFingerprint;
import com.cloudsuites.framework.services.auth.entities.DeviceRegistrationRequest;
import com.cloudsuites.framework.services.auth.entities.DeviceVerificationResult;
import com.cloudsuites.framework.services.auth.entities.TrustedDeviceTokenRequest;
import com.cloudsuites.framework.services.auth.entities.TrustStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of DeviceTrustService for managing device trust relationships.
 * 
 * This service provides functionality to:
 * - Register and verify trusted devices
 * - Generate device fingerprints using SHA-256 hashing
 * - Manage device-based authentication
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
@Service
public class DeviceTrustServiceImpl implements DeviceTrustService {

    private final DeviceFingerprintRepository deviceFingerprintRepository;

    @Autowired
    public DeviceTrustServiceImpl(DeviceFingerprintRepository deviceFingerprintRepository) {
        this.deviceFingerprintRepository = deviceFingerprintRepository;
    }

    @Override
    public String generateDeviceFingerprint(String deviceInfo) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(deviceInfo.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    @Override
    public DeviceVerificationResult verifyDeviceTrust(String userId, String deviceCharacteristics) {
        String fingerprint = generateDeviceFingerprint(deviceCharacteristics);
        Optional<DeviceFingerprint> trustedDevice = deviceFingerprintRepository.findByUserIdAndFingerprint(userId, fingerprint);
        
        if (trustedDevice.isPresent()) {
            DeviceFingerprint device = trustedDevice.get();
            if (device.getTrustStatus() == TrustStatus.TRUSTED) {
                return DeviceVerificationResult.trusted(device);
            } else {
                return new DeviceVerificationResult(false, device.getTrustStatus());
            }
        } else {
            return DeviceVerificationResult.untrusted("Unknown device");
        }
    }

    @Override
    public DeviceFingerprint registerTrustedDevice(DeviceRegistrationRequest request) {
        String fingerprint = generateDeviceFingerprint(request.getDeviceCharacteristics());
        
        // Check if device is already registered
        Optional<DeviceFingerprint> existing = deviceFingerprintRepository.findByUserIdAndFingerprint(
                request.getUserId(), fingerprint);
        
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Device already registered for user");
        }
        
        DeviceFingerprint deviceFingerprint = DeviceFingerprint.builder()
                .userId(request.getUserId())
                .fingerprint(fingerprint)
                .trustStatus(TrustStatus.TRUSTED)
                .registeredAt(LocalDateTime.now())
                .lastUsedAt(LocalDateTime.now())
                .build();
        
        return deviceFingerprintRepository.save(deviceFingerprint);
    }

    @Override
    public String createTrustedDeviceToken(TrustedDeviceTokenRequest request) {
        // Placeholder implementation - would integrate with JWT service
        throw new UnsupportedOperationException("Trusted device token creation not implemented yet");
    }

    @Override
    public boolean validateTrustedDeviceToken(String token, String deviceFingerprint) {
        // Placeholder implementation - would integrate with JWT service
        throw new UnsupportedOperationException("Trusted device token validation not implemented yet");
    }

    @Override
    public boolean revokeTrustedDevice(String userId, String deviceFingerprint) {
        Optional<DeviceFingerprint> device = deviceFingerprintRepository.findByUserIdAndFingerprint(userId, deviceFingerprint);
        if (device.isPresent()) {
            deviceFingerprintRepository.delete(device.get());
            return true;
        }
        return false;
    }

    @Override
    public List<DeviceFingerprint> getTrustedDevices(String userId) {
        return deviceFingerprintRepository.findTrustedDevicesByUserId(userId, TrustStatus.TRUSTED);
    }

    @Override
    public Optional<DeviceFingerprint> findTrustedDevice(String userId, String deviceFingerprint) {
        return deviceFingerprintRepository.findByUserIdAndFingerprint(userId, deviceFingerprint);
    }

    @Override
    public void updateDeviceActivity(String deviceFingerprint) {
        deviceFingerprintRepository.findByFingerprint(deviceFingerprint)
                .ifPresent(device -> {
                    device.setLastUsedAt(LocalDateTime.now());
                    deviceFingerprintRepository.save(device);
                });
    }

    @Override
    public int cleanupExpiredDevices() {
        // Placeholder implementation - would clean up devices based on expiration policy
        return 0;
    }
}
