package com.cloudsuites.framework.modules.auth.service.impl;

import com.cloudsuites.framework.modules.auth.repository.DeviceFingerprintRepository;
import com.cloudsuites.framework.services.auth.DeviceTrustService;
import com.cloudsuites.framework.services.auth.entities.DeviceFingerprint;
import com.cloudsuites.framework.services.auth.entities.DeviceRegistrationRequest;
import com.cloudsuites.framework.services.auth.entities.DeviceVerificationResult;
import com.cloudsuites.framework.services.auth.entities.TrustedDeviceTokenRequest;
import com.cloudsuites.framework.services.auth.entities.TrustStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementation of DeviceTrustService for managing device trust relationships.
 * 
 * This service handles device fingerprinting, trust verification, and
 * trusted device token management to support "keep me logged in" functionality.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DeviceTrustServiceImpl implements DeviceTrustService {

    private final DeviceFingerprintRepository deviceRepository;
    private final ObjectMapper objectMapper;
    
    // Configuration values
    @Value("${cloudsuites.device-trust.default-expiration-days:30}")
    private int defaultExpirationDays;
    
    @Value("${cloudsuites.device-trust.max-devices-per-user:10}")
    private int maxDevicesPerUser;
    
    @Value("${cloudsuites.device-trust.fingerprint-algorithm:SHA-256}")
    private String fingerprintAlgorithm;
    
    @Value("${cloudsuites.device-trust.trusted-token-duration-minutes:43200}") // 30 days
    private int trustedTokenDurationMinutes;
    
    @Value("${cloudsuites.device-trust.risk-threshold:70}")
    private int riskThreshold;

    // Patterns for device detection
    private static final Pattern MOBILE_PATTERN = Pattern.compile("Mobile|Android|iPhone|iPad", Pattern.CASE_INSENSITIVE);
    private static final Pattern TABLET_PATTERN = Pattern.compile("iPad|Tablet", Pattern.CASE_INSENSITIVE);
    private static final Pattern DESKTOP_PATTERN = Pattern.compile("Windows|Macintosh|Linux", Pattern.CASE_INSENSITIVE);
    
    @Override
    public DeviceFingerprint registerTrustedDevice(DeviceRegistrationRequest request) {
        log.debug("Registering trusted device for user: {}", request.getUserId());
        
        // Validate request
        validateRegistrationRequest(request);
        
        // Check device limit
        checkDeviceLimit(request.getUserId());
        
        // Generate device fingerprint
        String fingerprint = generateDeviceFingerprint(request.getMetadata() != null ? request.getMetadata() : request.getUserAgent());
        
        // Check if device already exists
        Optional<DeviceFingerprint> existing = deviceRepository.findByUserIdAndFingerprint(
            request.getUserId(), fingerprint);
        
        if (existing.isPresent()) {
            log.info("Device already registered for user: {}, updating trust status", request.getUserId());
            return updateExistingDevice(existing.get(), request);
        }
        
        // Create new device fingerprint
        DeviceFingerprint device = createDeviceFingerprint(request, fingerprint);
        device = deviceRepository.save(device);
        
        log.info("Successfully registered new trusted device: {} for user: {}", 
                device.getDeviceId(), request.getUserId());
        
        return device;
    }

    @Override
    public DeviceVerificationResult verifyDeviceTrust(String userId, String deviceCharacteristics) {
        log.debug("Verifying device trust for user: {}", userId);
        
        String fingerprint = generateDeviceFingerprint(deviceCharacteristics);
        
        Optional<DeviceFingerprint> device = deviceRepository.findByUserIdAndFingerprint(userId, fingerprint);
        
        if (device.isEmpty()) {
            log.debug("Device not found in trusted devices for user: {}", userId);
            return DeviceVerificationResult.notTrusted(fingerprint);
        }
        
        DeviceFingerprint deviceEntity = device.get();
        
        // Update activity
        deviceEntity.updateActivity();
        deviceRepository.save(deviceEntity);
        
        // Check if device trust has expired
        if (deviceEntity.isExpired()) {
            log.warn("Device trust has expired for user: {}, device: {}", userId, deviceEntity.getDeviceId());
            return DeviceVerificationResult.expired(mapToDto(deviceEntity));
        }
        
        // Check trust status
        if (!deviceEntity.isTrusted()) {
            log.warn("Device is not in trusted status for user: {}, device: {}, status: {}", 
                    userId, deviceEntity.getDeviceId(), deviceEntity.getTrustStatus());
            return createVerificationResult(deviceEntity, false);
        }
        
        log.debug("Device trust verified successfully for user: {}, device: {}", 
                 userId, deviceEntity.getDeviceId());
        return DeviceVerificationResult.trusted(mapToDto(deviceEntity));
    }

    @Override
    public String generateDeviceFingerprint(String deviceInfo) {
        try {
            // Normalize device info
            String normalizedInfo = normalizeDeviceInfo(deviceInfo);
            
            // Create hash
            MessageDigest digest = MessageDigest.getInstance(fingerprintAlgorithm);
            byte[] hash = digest.digest(normalizedInfo.getBytes(StandardCharsets.UTF_8));
            
            // Convert to hex string
            return new String(Hex.encode(hash));
            
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate device fingerprint: {}", e.getMessage());
            throw new RuntimeException("Device fingerprint generation failed", e);
        }
    }

    @Override
    public String createTrustedDeviceToken(TrustedDeviceTokenRequest request) {
        log.debug("Creating trusted device token for user: {}, device: {}", 
                 request.getUserId(), request.getDeviceFingerprint());
        
        // Verify device is trusted
        Optional<DeviceFingerprint> device = deviceRepository.findByUserIdAndFingerprint(
            request.getUserId(), request.getDeviceFingerprint());
        
        if (device.isEmpty() || !device.get().isTrusted()) {
            throw new SecurityException("Device is not trusted for user: " + request.getUserId());
        }
        
        // Create enhanced JWT token with device context
        // Note: This would integrate with existing JWT service
        // For now, returning a placeholder token structure
        String tokenPayload = createTokenPayload(request, device.get());
        
        log.info("Created trusted device token for user: {}, device: {}", 
                request.getUserId(), device.get().getDeviceId());
        
        return tokenPayload;
    }

    @Override
    public boolean validateTrustedDeviceToken(String token, String deviceFingerprint) {
        log.debug("Validating trusted device token for device: {}", deviceFingerprint);
        
        try {
            // Parse token and extract device fingerprint claim
            // Note: This would integrate with existing JWT validation
            
            // For now, basic validation
            if (!StringUtils.hasText(token) || !StringUtils.hasText(deviceFingerprint)) {
                return false;
            }
            
            // Check if device still exists and is trusted
            Optional<DeviceFingerprint> device = deviceRepository.findByFingerprint(deviceFingerprint);
            
            return device.isPresent() && device.get().isTrusted();
            
        } catch (Exception e) {
            log.error("Token validation failed for device: {}, error: {}", deviceFingerprint, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean revokeTrustedDevice(String userId, String deviceFingerprint) {
        log.debug("Revoking trusted device for user: {}, device: {}", userId, deviceFingerprint);
        
        Optional<DeviceFingerprint> device = deviceRepository.findByUserIdAndFingerprint(userId, deviceFingerprint);
        
        if (device.isEmpty()) {
            log.warn("Device not found for revocation: user={}, fingerprint={}", userId, deviceFingerprint);
            return false;
        }
        
        DeviceFingerprint deviceEntity = device.get();
        deviceEntity.revoke("User requested revocation");
        deviceRepository.save(deviceEntity);
        
        log.info("Successfully revoked device trust: user={}, device={}", userId, deviceEntity.getDeviceId());
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceFingerprint> getTrustedDevices(String userId) {
        log.debug("Getting trusted devices for user: {}", userId);
        
        return deviceRepository.findTrustedDevicesByUserId(userId, TrustStatus.TRUSTED);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeviceFingerprint> findTrustedDevice(String userId, String deviceFingerprint) {
        log.debug("Finding trusted device for user: {}, fingerprint: {}", userId, deviceFingerprint);
        
        Optional<DeviceFingerprint> device = deviceRepository.findByUserIdAndFingerprint(userId, deviceFingerprint);
        
        if (device.isEmpty() || !device.get().isTrusted()) {
            return Optional.empty();
        }
        
        return device;
    }

    @Override
    public void updateDeviceActivity(String deviceFingerprint) {
        log.debug("Updating device activity for fingerprint: {}", deviceFingerprint);
        
        Optional<DeviceFingerprint> device = deviceRepository.findByFingerprint(deviceFingerprint);
        
        if (device.isPresent()) {
            DeviceFingerprint deviceEntity = device.get();
            deviceEntity.updateActivity();
            deviceRepository.save(deviceEntity);
        }
    }

    @Override
    public int cleanupExpiredDevices() {
        log.info("Starting cleanup of expired devices");
        
        LocalDateTime now = LocalDateTime.now();
        
        // Find expired devices
        List<DeviceFingerprint> expiredDevices = deviceRepository.findExpiredDevices(
            now, TrustStatus.TRUSTED);
        
        int cleanedUp = 0;
        for (DeviceFingerprint device : expiredDevices) {
            device.setTrustStatus(DeviceFingerprintDto.TrustStatus.EXPIRED);
            deviceRepository.save(device);
            cleanedUp++;
        }
        
        // Also cleanup devices that haven't been used in 90 days
        LocalDateTime inactiveThreshold = now.minusDays(90);
        List<DeviceFingerprint> inactiveDevices = deviceRepository.findInactiveDevices(inactiveThreshold);
        
        for (DeviceFingerprint device : inactiveDevices) {
            device.revoke("Inactive for 90+ days");
            deviceRepository.save(device);
            cleanedUp++;
        }
        
        log.info("Cleaned up {} expired/inactive devices", cleanedUp);
        return cleanedUp;
    }

    // Private helper methods

    private void validateRegistrationRequest(DeviceRegistrationRequest request) {
        if (!StringUtils.hasText(request.getUserId())) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (!StringUtils.hasText(request.getDeviceInfo())) {
            throw new IllegalArgumentException("Device information is required");
        }
    }

    private void checkDeviceLimit(String userId) {
        long trustedDeviceCount = deviceRepository.countTrustedDevicesByUserId(
            userId, DeviceFingerprintDto.TrustStatus.TRUSTED);
        
        if (trustedDeviceCount >= maxDevicesPerUser) {
            throw new SecurityException("Maximum number of trusted devices reached for user: " + userId);
        }
    }

    private DeviceFingerprint updateExistingDevice(DeviceFingerprint existing, DeviceRegistrationRequest request) {
        // Update device information if needed
        existing.setTrustStatus(DeviceFingerprintDto.TrustStatus.TRUSTED);
        existing.setLastUsedAt(LocalDateTime.now());
        
        if (request.getTrustExpirationDays() != null) {
            existing.setExpiresAt(LocalDateTime.now().plusDays(request.getTrustExpirationDays()));
        }
        
        if (StringUtils.hasText(request.getDeviceName())) {
            existing.setDeviceName(request.getDeviceName());
        }
        
        existing = deviceRepository.save(existing);
        return mapToDto(existing);
    }

    private DeviceFingerprint createDeviceFingerprint(DeviceRegistrationRequest request, String fingerprint) {
        DeviceFingerprint.DeviceFingerprintBuilder builder = DeviceFingerprint.builder()
                .userId(request.getUserId())
                .fingerprint(fingerprint)
                .deviceName(request.getDeviceName())
                .deviceType(determineDeviceType(request.getDeviceInfo()))
                .registrationIp(request.getIpAddress())
                .trustStatus(request.getTrustDevice() ? 
                    DeviceFingerprintDto.TrustStatus.TRUSTED : 
                    DeviceFingerprintDto.TrustStatus.PENDING)
                .biometricCapable(request.getBiometricSupported())
                .userAgent(extractUserAgent(request.getDeviceInfo()))
                .createdBy(request.getUserId());
        
        // Set expiration
        if (request.getTrustExpirationDays() != null) {
            builder.expiresAt(LocalDateTime.now().plusDays(request.getTrustExpirationDays()));
        } else {
            builder.expiresAt(LocalDateTime.now().plusDays(defaultExpirationDays));
        }
        
        // Store device characteristics as metadata
        if (request.getDeviceCharacteristics() != null && !request.getDeviceCharacteristics().isEmpty()) {
            try {
                String metadata = objectMapper.writeValueAsString(request.getDeviceCharacteristics());
                builder.metadata(metadata);
            } catch (JsonProcessingException e) {
                log.warn("Failed to serialize device characteristics: {}", e.getMessage());
            }
        }
        
        // Parse device info for additional details
        parseDeviceInfo(request.getDeviceInfo(), builder);
        
        return builder.build();
    }

    private DeviceFingerprintDto.DeviceType determineDeviceType(String deviceInfo) {
        if (MOBILE_PATTERN.matcher(deviceInfo).find()) {
            return DeviceFingerprintDto.DeviceType.MOBILE;
        } else if (TABLET_PATTERN.matcher(deviceInfo).find()) {
            return DeviceFingerprintDto.DeviceType.TABLET;
        } else if (DESKTOP_PATTERN.matcher(deviceInfo).find()) {
            return DeviceFingerprintDto.DeviceType.DESKTOP;
        }
        return DeviceFingerprintDto.DeviceType.UNKNOWN;
    }

    private String normalizeDeviceInfo(String deviceInfo) {
        if (!StringUtils.hasText(deviceInfo)) {
            return "";
        }
        
        // Remove volatile information and normalize
        return deviceInfo.toLowerCase()
                .replaceAll("\\s+", " ")
                .replaceAll("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}", "") // Remove IP addresses
                .replaceAll("session[a-z0-9]+", "") // Remove session IDs
                .trim();
    }

    private String extractUserAgent(String deviceInfo) {
        // Simple user agent extraction - in real implementation would be more sophisticated
        if (StringUtils.hasText(deviceInfo) && deviceInfo.contains("Mozilla")) {
            int end = deviceInfo.indexOf('\n');
            if (end > 0) {
                return deviceInfo.substring(0, end);
            }
            return deviceInfo.length() > 500 ? deviceInfo.substring(0, 500) : deviceInfo;
        }
        return null;
    }

    private void parseDeviceInfo(String deviceInfo, DeviceFingerprint.DeviceFingerprintBuilder builder) {
        // Extract OS information
        if (deviceInfo.contains("Windows")) {
            builder.osInfo("Windows");
        } else if (deviceInfo.contains("Mac OS")) {
            builder.osInfo("macOS");
        } else if (deviceInfo.contains("Linux")) {
            builder.osInfo("Linux");
        } else if (deviceInfo.contains("Android")) {
            builder.osInfo("Android");
        } else if (deviceInfo.contains("iOS")) {
            builder.osInfo("iOS");
        }
        
        // Extract browser information
        if (deviceInfo.contains("Chrome")) {
            builder.browserInfo("Chrome");
        } else if (deviceInfo.contains("Firefox")) {
            builder.browserInfo("Firefox");
        } else if (deviceInfo.contains("Safari")) {
            builder.browserInfo("Safari");
        } else if (deviceInfo.contains("Edge")) {
            builder.browserInfo("Edge");
        }
    }

    private DeviceVerificationResult createVerificationResult(DeviceFingerprint device, boolean trusted) {
        DeviceVerificationResult.RiskAssessment riskAssessment = calculateRiskAssessment(device);
        
        return DeviceVerificationResult.builder()
                .isTrusted(trusted)
                .deviceFingerprint(device.getFingerprint())
                .confidenceLevel(trusted ? 95 : 60)
                .trustStatus(device.getTrustStatus())
                .deviceInfo(mapToDto(device))
                .riskAssessment(riskAssessment)
                .recommendedAction(determineRecommendedAction(riskAssessment))
                .requiresAdditionalAuth(!trusted)
                .allowExtendedSession(trusted && riskAssessment.getRiskScore() < riskThreshold)
                .reasonCode(trusted ? "DEVICE_TRUSTED" : "DEVICE_NOT_TRUSTED")
                .reasonMessage(trusted ? "Device is verified and trusted" : "Device trust verification failed")
                .build();
    }

    private DeviceVerificationResult.RiskAssessment calculateRiskAssessment(DeviceFingerprint device) {
        int riskScore = device.getRiskScore() != null ? device.getRiskScore() : 30;
        
        // Adjust risk based on usage patterns
        if (device.getLastUsedAt() != null) {
            long daysSinceLastUse = java.time.temporal.ChronoUnit.DAYS.between(
                device.getLastUsedAt(), LocalDateTime.now());
            
            if (daysSinceLastUse > 30) {
                riskScore += 20;
            } else if (daysSinceLastUse > 7) {
                riskScore += 10;
            }
        }
        
        // Determine risk level
        DeviceVerificationResult.RiskLevel riskLevel;
        if (riskScore < 30) {
            riskLevel = DeviceVerificationResult.RiskLevel.LOW;
        } else if (riskScore < 60) {
            riskLevel = DeviceVerificationResult.RiskLevel.MEDIUM;
        } else if (riskScore < 85) {
            riskLevel = DeviceVerificationResult.RiskLevel.HIGH;
        } else {
            riskLevel = DeviceVerificationResult.RiskLevel.CRITICAL;
        }
        
        return DeviceVerificationResult.RiskAssessment.builder()
                .riskScore(riskScore)
                .riskLevel(riskLevel)
                .riskFactors(determineRiskFactors(device))
                .daysSinceLastUse(device.getLastUsedAt() != null ? 
                    (int) java.time.temporal.ChronoUnit.DAYS.between(device.getLastUsedAt(), LocalDateTime.now()) : null)
                .build();
    }

    private List<String> determineRiskFactors(DeviceFingerprint device) {
        List<String> factors = new ArrayList<>();
        
        if (device.getLastUsedAt() != null) {
            long daysSinceLastUse = java.time.temporal.ChronoUnit.DAYS.between(
                device.getLastUsedAt(), LocalDateTime.now());
            
            if (daysSinceLastUse > 30) {
                factors.add("Long period since last use");
            }
        }
        
        if (device.getUsageCount() != null && device.getUsageCount() < 5) {
            factors.add("Limited usage history");
        }
        
        if (device.getRiskScore() != null && device.getRiskScore() > 50) {
            factors.add("Elevated risk profile");
        }
        
        return factors;
    }

    private DeviceVerificationResult.RecommendedAction determineRecommendedAction(
            DeviceVerificationResult.RiskAssessment riskAssessment) {
        
        switch (riskAssessment.getRiskLevel()) {
            case LOW:
                return DeviceVerificationResult.RecommendedAction.ALLOW;
            case MEDIUM:
                return DeviceVerificationResult.RecommendedAction.ALLOW_WITH_MFA;
            case HIGH:
                return DeviceVerificationResult.RecommendedAction.REQUIRE_FULL_AUTH;
            case CRITICAL:
                return DeviceVerificationResult.RecommendedAction.BLOCK;
            default:
                return DeviceVerificationResult.RecommendedAction.REQUIRE_FULL_AUTH;
        }
    }

    private String createTokenPayload(TrustedDeviceTokenRequest request, DeviceFingerprint device) {
        // This is a placeholder - in real implementation would create JWT with device claims
        return String.format("TRUSTED_DEVICE_TOKEN:%s:%s:%d", 
                request.getUserId(), 
                device.getFingerprint(), 
                System.currentTimeMillis() + (request.getTokenDurationMinutes() != null ? 
                    request.getTokenDurationMinutes() * 60000L : trustedTokenDurationMinutes * 60000L));
    }

    private DeviceFingerprintDto mapToDto(DeviceFingerprint entity) {
        return DeviceFingerprintDto.builder()
                .deviceId(entity.getDeviceId())
                .userId(entity.getUserId())
                .fingerprint(entity.getFingerprint())
                .deviceName(entity.getDeviceName())
                .deviceType(entity.getDeviceType())
                .osInfo(entity.getOsInfo())
                .browserInfo(entity.getBrowserInfo())
                .registrationIp(entity.getRegistrationIp())
                .trustStatus(entity.getTrustStatus())
                .registeredAt(entity.getRegisteredAt())
                .lastUsedAt(entity.getLastUsedAt())
                .expiresAt(entity.getExpiresAt())
                .metadata(entity.getMetadata())
                .riskScore(entity.getRiskScore())
                .usageCount(entity.getUsageCount())
                .biometricCapable(entity.getBiometricCapable())
                .build();
    }
}
