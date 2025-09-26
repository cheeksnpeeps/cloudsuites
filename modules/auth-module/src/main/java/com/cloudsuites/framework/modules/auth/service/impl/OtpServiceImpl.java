package com.cloudsuites.framework.modules.auth.service.impl;

import com.cloudsuites.framework.services.auth.OtpService;
import com.cloudsuites.framework.services.auth.OtpChannel;
import com.cloudsuites.framework.services.auth.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Multi-channel OTP service implementation with rate limiting and security features.
 * Supports SMS and Email delivery with comprehensive tracking and validation.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
@Service
public class OtpServiceImpl implements OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpServiceImpl.class);

    // Configuration
    @Value("${cloudsuites.otp.code-length:6}")
    private int codeLength;

    @Value("${cloudsuites.otp.expiry-minutes:5}")
    private int expiryMinutes;

    @Value("${cloudsuites.otp.rate-limit-count:3}")
    private int rateLimitCount;

    @Value("${cloudsuites.otp.rate-limit-window-minutes:5}")
    private int rateLimitWindowMinutes;

    @Value("${cloudsuites.otp.max-resends:2}")
    private int maxResends;

    @Value("${cloudsuites.otp.verification-attempts:3}")
    private int maxVerificationAttempts;

    // In-memory storage (in production, use Redis or database)
    private final Map<String, OtpData> activeOtps = new ConcurrentHashMap<>();
    private final Map<String, RateLimitData> rateLimits = new ConcurrentHashMap<>();
    private final Map<String, OtpStatistics> statistics = new ConcurrentHashMap<>();
    
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Data class for storing OTP information.
     */
    private static class OtpData {
        final String otpId;
        final String code;
        final String recipient;
        final OtpChannel channel;
        final String purpose;
        final LocalDateTime createdAt;
        final LocalDateTime expiresAt;
        final String ipAddress;
        final String userAgent;
        final AtomicInteger verificationAttempts;
        final AtomicInteger resendCount;

        OtpData(String otpId, String code, String recipient, OtpChannel channel, String purpose,
                String ipAddress, String userAgent, int expiryMinutes) {
            this.otpId = otpId;
            this.code = code;
            this.recipient = recipient;
            this.channel = channel;
            this.purpose = purpose;
            this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            this.expiresAt = this.createdAt.plusMinutes(expiryMinutes);
            this.ipAddress = ipAddress;
            this.userAgent = userAgent;
            this.verificationAttempts = new AtomicInteger(0);
            this.resendCount = new AtomicInteger(0);
        }

        boolean isExpired() {
            return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).isAfter(expiresAt) ||
                   LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).equals(expiresAt);
        }

        boolean canVerify() {
            return !isExpired() && verificationAttempts.get() < 3; // Max 3 verification attempts
        }

        boolean canResend(int maxResends) {
            return !isExpired() && resendCount.get() < maxResends;
        }
    }

    /**
     * Data class for tracking rate limits.
     */
    private static class RateLimitData {
        final AtomicInteger count;
        final LocalDateTime windowStart;
        final LocalDateTime windowEnd;

        RateLimitData(int windowMinutes) {
            this.count = new AtomicInteger(1);
            this.windowStart = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            this.windowEnd = this.windowStart.plusMinutes(windowMinutes);
        }

        boolean isInWindow() {
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            return !now.isAfter(windowEnd);
        }

        boolean isLimitExceeded(int maxCount) {
            return isInWindow() && count.get() >= maxCount;
        }

        void increment() {
            if (isInWindow()) {
                count.incrementAndGet();
            }
        }

        long getResetTimeSeconds() {
            if (!isInWindow()) {
                return 0;
            }
            return ChronoUnit.SECONDS.between(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), windowEnd);
        }
    }

    @Override
    public OtpResponse sendOtp(OtpRequest request) {
        logger.debug("Sending OTP to {} via {} for purpose: {}", 
                    maskRecipient(request.getRecipient()), request.getChannel(), request.getPurpose());

        try {
            // Validate request
            if (!isValidRecipient(request.getRecipient(), request.getChannel())) {
                return OtpResponse.failure("Invalid recipient format for " + request.getChannel().getDisplayName());
            }

            // Check and update rate limiting atomically
            synchronized (this) {
                if (isRateLimited(request.getRecipient())) {
                    long resetTime = getRateLimitResetTime(request.getRecipient());
                    LocalDateTime resetDateTime = LocalDateTime.now().plusSeconds(resetTime);
                    return OtpResponse.rateLimited(
                        "Too many OTP requests. Try again in " + resetTime + " seconds.", resetDateTime);
                }

                // Update rate limiting first to prevent race conditions
                updateRateLimit(request.getRecipient());
            }

            // Generate OTP
            String otpCode = generateOtpCode();
            String otpId = generateOtpId();

            // Store OTP data
            OtpData otpData = new OtpData(otpId, otpCode, request.getRecipient(), 
                                        request.getChannel(), request.getPurpose(),
                                        request.getIpAddress(), request.getUserAgent(), expiryMinutes);
            
            activeOtps.put(otpId, otpData);

            // Update statistics
            updateStatistics(request.getRecipient(), true, false);

            // Send OTP (mock implementation - in production integrate with Twilio/SMTP)
            boolean sent = deliverOtp(otpData);
            
            if (sent) {
                logger.info("OTP sent successfully to {} via {} (ID: {})", 
                          maskRecipient(request.getRecipient()), request.getChannel(), otpId);
                
                return OtpResponse.success(
                    "OTP sent successfully via " + request.getChannel().getDisplayName(),
                    otpId,
                    request.getChannel(),
                    maskRecipient(request.getRecipient()),
                    otpData.expiresAt
                );
            } else {
                activeOtps.remove(otpId); // Clean up failed OTP
                return OtpResponse.failure("Failed to send OTP via " + request.getChannel().getDisplayName());
            }

        } catch (Exception e) {
            logger.error("Error sending OTP to {}: {}", maskRecipient(request.getRecipient()), e.getMessage(), e);
            return OtpResponse.failure("Internal error occurred while sending OTP");
        }
    }

    @Override
    public boolean verifyOtp(OtpVerificationRequest request) {
        logger.debug("Verifying OTP for {} with purpose: {}", 
                    maskRecipient(request.getRecipient()), request.getPurpose());

        try {
            // Find active OTP for recipient and purpose
            OtpData otpData = findActiveOtp(request.getRecipient(), request.getPurpose());
            
            if (otpData == null) {
                logger.debug("No active OTP found for {} with purpose: {}", 
                           maskRecipient(request.getRecipient()), request.getPurpose());
                updateStatistics(request.getRecipient(), false, true);
                return false;
            }

            // Check if OTP can be verified
            if (!otpData.canVerify()) {
                logger.debug("OTP cannot be verified - expired or max attempts reached for {}", 
                           maskRecipient(request.getRecipient()));
                updateStatistics(request.getRecipient(), false, true);
                return false;
            }

            // Increment verification attempts
            otpData.verificationAttempts.incrementAndGet();

            // Verify OTP code
            boolean isValid = otpData.code.equals(request.getOtpCode());
            
            if (isValid) {
                logger.info("OTP verified successfully for {} (ID: {})", 
                          maskRecipient(request.getRecipient()), otpData.otpId);
                
                // Remove used OTP
                activeOtps.remove(otpData.otpId);
                updateStatistics(request.getRecipient(), false, true);
                return true;
            } else {
                logger.debug("Invalid OTP code provided for {} (Attempt: {}/{})", 
                           maskRecipient(request.getRecipient()), 
                           otpData.verificationAttempts.get(), maxVerificationAttempts);
                
                // Remove OTP if max attempts reached
                if (otpData.verificationAttempts.get() >= maxVerificationAttempts) {
                    activeOtps.remove(otpData.otpId);
                    logger.debug("OTP removed due to max verification attempts for {}", 
                               maskRecipient(request.getRecipient()));
                }
                
                updateStatistics(request.getRecipient(), false, true);
                return false;
            }

        } catch (Exception e) {
            logger.error("Error verifying OTP for {}: {}", maskRecipient(request.getRecipient()), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public OtpResponse resendOtp(String recipient) {
        logger.debug("Resending OTP to {}", maskRecipient(recipient));

        try {
            // Find the most recent active OTP for this recipient
            OtpData latestOtp = findLatestActiveOtp(recipient);
            
            if (latestOtp == null) {
                return OtpResponse.failure("No active OTP found to resend");
            }

            if (!latestOtp.canResend(maxResends)) {
                return OtpResponse.failure("Maximum resend attempts exceeded or OTP expired");
            }

            // Check rate limiting
            if (isRateLimited(recipient)) {
                long resetTime = getRateLimitResetTime(recipient);
                LocalDateTime resetDateTime = LocalDateTime.now().plusSeconds(resetTime);
                return OtpResponse.rateLimited(
                    "Too many requests. Try again in " + resetTime + " seconds.", resetDateTime);
            }

            // Increment resend count
            latestOtp.resendCount.incrementAndGet();

            // Update rate limiting
            updateRateLimit(recipient);

            // Resend OTP
            boolean sent = deliverOtp(latestOtp);
            
            if (sent) {
                logger.info("OTP resent successfully to {} (ID: {}, Resend: {})", 
                          maskRecipient(recipient), latestOtp.otpId, latestOtp.resendCount.get());
                
                return OtpResponse.success(
                    "OTP resent successfully via " + latestOtp.channel.getDisplayName(),
                    latestOtp.otpId,
                    latestOtp.channel,
                    maskRecipient(recipient),
                    latestOtp.expiresAt
                );
            } else {
                return OtpResponse.failure("Failed to resend OTP");
            }

        } catch (Exception e) {
            logger.error("Error resending OTP to {}: {}", maskRecipient(recipient), e.getMessage(), e);
            return OtpResponse.failure("Internal error occurred while resending OTP");
        }
    }

    @Override
    public int invalidateOtpCodes(String recipient) {
        logger.debug("Invalidating all OTP codes for {}", maskRecipient(recipient));

        int invalidatedCount = 0;
        try {
            // Find and remove all active OTPs for the recipient
            var iterator = activeOtps.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                if (entry.getValue().recipient.equals(recipient)) {
                    logger.debug("Invalidated OTP ID: {} for {}", entry.getKey(), maskRecipient(recipient));
                    iterator.remove();
                    invalidatedCount++;
                }
            }

            if (invalidatedCount > 0) {
                logger.info("Invalidated {} OTP codes for {}", invalidatedCount, maskRecipient(recipient));
            }

        } catch (Exception e) {
            logger.error("Error invalidating OTP codes for {}: {}", maskRecipient(recipient), e.getMessage(), e);
        }

        return invalidatedCount;
    }

    @Override
    public boolean isRateLimited(String recipient) {
        cleanupExpiredRateLimits();
        
        RateLimitData rateLimitData = rateLimits.get(recipient);
        if (rateLimitData == null) {
            return false;
        }
        
        return rateLimitData.isLimitExceeded(rateLimitCount);
    }

    @Override
    public long getRateLimitResetTime(String recipient) {
        RateLimitData rateLimitData = rateLimits.get(recipient);
        if (rateLimitData == null) {
            return 0;
        }
        
        return rateLimitData.getResetTimeSeconds();
    }

    @Override
    public boolean isValidRecipient(String recipient, OtpChannel channel) {
        if (recipient == null || channel == null) {
            return false;
        }
        
        return channel.isValidRecipient(recipient);
    }

    @Override
    public OtpStatistics getOtpStatistics(String recipient) {
        cleanupExpiredOtps();
        
        if (recipient == null) {
            // Return global statistics
            return calculateGlobalStatistics();
        }
        
        OtpStatistics stats = statistics.get(recipient);
        if (stats == null) {
            stats = new OtpStatistics(recipient);
            statistics.put(recipient, stats);
        }
        
        // Update current rate limit info
        RateLimitData rateLimitData = rateLimits.get(recipient);
        if (rateLimitData != null && rateLimitData.isInWindow()) {
            stats.setCurrentRateLimitCount(rateLimitData.count.get());
            stats.setRateLimitResetTime(rateLimitData.windowEnd);
        } else {
            stats.setCurrentRateLimitCount(0);
            stats.setRateLimitResetTime(null);
        }
        
        return stats;
    }

    // Helper methods

    private String generateOtpCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            code.append(secureRandom.nextInt(10));
        }
        return code.toString();
    }

    private String generateOtpId() {
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        StringBuilder id = new StringBuilder();
        for (byte b : bytes) {
            id.append(String.format("%02x", b));
        }
        return "otp-" + id.toString();
    }

    private boolean deliverOtp(OtpData otpData) {
        // Mock implementation - in production, integrate with actual services
        switch (otpData.channel) {
            case SMS:
                return deliverSms(otpData);
            case EMAIL:
                return deliverEmail(otpData);
            default:
                logger.warn("Unsupported OTP channel: {}", otpData.channel);
                return false;
        }
    }

    private boolean deliverSms(OtpData otpData) {
        // Mock SMS delivery - integrate with Twilio in production
        logger.info("SMS OTP would be sent to {} with code: {} (ID: {})", 
                   maskRecipient(otpData.recipient), "******", otpData.otpId);
        
        // Simulate delivery success/failure
        return true; // 100% success rate for testing
    }

    private boolean deliverEmail(OtpData otpData) {
        // Mock email delivery - integrate with SMTP in production
        logger.info("Email OTP would be sent to {} with code: {} (ID: {})", 
                   maskRecipient(otpData.recipient), "******", otpData.otpId);
        
        // Simulate delivery success/failure
        return true; // 100% success rate for testing
    }

    private OtpData findActiveOtp(String recipient, String purpose) {
        return activeOtps.values().stream()
                .filter(otp -> otp.recipient.equals(recipient) && otp.purpose.equals(purpose))
                .filter(otp -> !otp.isExpired())
                .findFirst()
                .orElse(null);
    }

    private OtpData findLatestActiveOtp(String recipient) {
        return activeOtps.values().stream()
                .filter(otp -> otp.recipient.equals(recipient))
                .filter(otp -> !otp.isExpired())
                .max((a, b) -> a.createdAt.compareTo(b.createdAt))
                .orElse(null);
    }

    private void updateRateLimit(String recipient) {
        RateLimitData rateLimitData = rateLimits.get(recipient);
        
        if (rateLimitData == null || !rateLimitData.isInWindow()) {
            // Create new rate limit window
            rateLimits.put(recipient, new RateLimitData(rateLimitWindowMinutes));
        } else {
            // Increment existing window
            rateLimitData.increment();
        }
    }

    private void updateStatistics(String recipient, boolean otpSent, boolean verificationAttempt) {
        OtpStatistics stats = statistics.computeIfAbsent(recipient, OtpStatistics::new);
        
        if (otpSent) {
            stats.setTotalOtpsSent(stats.getTotalOtpsSent() + 1);
            stats.setLastOtpSent(LocalDateTime.now());
            
            if (stats.getFirstOtpSent() == null) {
                stats.setFirstOtpSent(LocalDateTime.now());
            }
        }
        
        if (verificationAttempt) {
            stats.setTotalOtpsVerified(stats.getTotalOtpsVerified() + 1);
            // Note: successful verifications are updated in verifyOtp method
        }
        
        stats.calculateSuccessRate();
    }

    private OtpStatistics calculateGlobalStatistics() {
        OtpStatistics globalStats = new OtpStatistics(null);
        
        for (OtpStatistics stats : statistics.values()) {
            globalStats.setTotalOtpsSent(globalStats.getTotalOtpsSent() + stats.getTotalOtpsSent());
            globalStats.setTotalOtpsVerified(globalStats.getTotalOtpsVerified() + stats.getTotalOtpsVerified());
            globalStats.setSuccessfulVerifications(globalStats.getSuccessfulVerifications() + stats.getSuccessfulVerifications());
            globalStats.setFailedVerifications(globalStats.getFailedVerifications() + stats.getFailedVerifications());
            
            if (globalStats.getFirstOtpSent() == null || 
                (stats.getFirstOtpSent() != null && stats.getFirstOtpSent().isBefore(globalStats.getFirstOtpSent()))) {
                globalStats.setFirstOtpSent(stats.getFirstOtpSent());
            }
            
            if (globalStats.getLastOtpSent() == null || 
                (stats.getLastOtpSent() != null && stats.getLastOtpSent().isAfter(globalStats.getLastOtpSent()))) {
                globalStats.setLastOtpSent(stats.getLastOtpSent());
            }
        }
        
        globalStats.calculateSuccessRate();
        return globalStats;
    }

    private String maskRecipient(String recipient) {
        if (recipient == null || recipient.length() <= 4) {
            return "****";
        }
        if (recipient.contains("@")) {
            // Email masking: first 2 chars + *** + domain
            int atIndex = recipient.indexOf("@");
            return recipient.substring(0, Math.min(2, atIndex)) + "***" + recipient.substring(atIndex);
        } else {
            // Phone masking: +1***-***-last4
            return "+***-***-" + recipient.substring(recipient.length() - 4);
        }
    }

    private void cleanupExpiredOtps() {
        activeOtps.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    private void cleanupExpiredRateLimits() {
        rateLimits.entrySet().removeIf(entry -> !entry.getValue().isInWindow());
    }
}
