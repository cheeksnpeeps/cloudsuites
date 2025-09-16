package com.cloudsuites.framework.modules.auth.service.impl;

import com.cloudsuites.framework.services.auth.OtpChannel;
import com.cloudsuites.framework.services.auth.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for OtpServiceImpl.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
@DisplayName("OTP Service Implementation Tests")
class OtpServiceImplTest {

    private OtpServiceImpl otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpServiceImpl();
        
        // Set test configuration using reflection
        ReflectionTestUtils.setField(otpService, "codeLength", 6);
        ReflectionTestUtils.setField(otpService, "expiryMinutes", 5);
        ReflectionTestUtils.setField(otpService, "rateLimitCount", 3);
        ReflectionTestUtils.setField(otpService, "rateLimitWindowMinutes", 5);
        ReflectionTestUtils.setField(otpService, "maxResends", 2);
        ReflectionTestUtils.setField(otpService, "maxVerificationAttempts", 3);
    }

    @Test
    @DisplayName("Should send SMS OTP successfully")
    void shouldSendSmsOtpSuccessfully() {
        OtpRequest request = new OtpRequest("+1234567890", OtpChannel.SMS, "login");
        request.setIpAddress("192.168.1.1");
        request.setUserAgent("Mozilla/5.0");

        OtpResponse response = otpService.sendOtp(request);

        assertTrue(response.isSuccess());
        assertEquals("OTP sent successfully via SMS Text Message", response.getMessage());
        assertNotNull(response.getOtpId());
        assertEquals(OtpChannel.SMS, response.getChannel());
        assertEquals("+***-***-7890", response.getRecipient());
        assertNotNull(response.getExpiresAt());
    }

    @Test
    @DisplayName("Should send Email OTP successfully")
    void shouldSendEmailOtpSuccessfully() {
        OtpRequest request = new OtpRequest("user@example.com", OtpChannel.EMAIL, "registration");
        request.setIpAddress("192.168.1.1");

        OtpResponse response = otpService.sendOtp(request);

        assertTrue(response.isSuccess());
        assertEquals("OTP sent successfully via Email Message", response.getMessage());
        assertNotNull(response.getOtpId());
        assertEquals(OtpChannel.EMAIL, response.getChannel());
        assertEquals("us***@example.com", response.getRecipient());
        assertNotNull(response.getExpiresAt());
    }

    @Test
    @DisplayName("Should reject invalid phone number for SMS")
    void shouldRejectInvalidPhoneNumberForSms() {
        OtpRequest request = new OtpRequest("1234567890", OtpChannel.SMS, "login"); // Missing +

        OtpResponse response = otpService.sendOtp(request);

        assertFalse(response.isSuccess());
        assertEquals("Invalid recipient format for SMS Text Message", response.getMessage());
    }

    @Test
    @DisplayName("Should reject invalid email address for Email")
    void shouldRejectInvalidEmailAddressForEmail() {
        OtpRequest request = new OtpRequest("invalid-email", OtpChannel.EMAIL, "registration");

        OtpResponse response = otpService.sendOtp(request);

        assertFalse(response.isSuccess());
        assertEquals("Invalid recipient format for Email Message", response.getMessage());
    }

    @Test
    @DisplayName("Should enforce rate limiting")
    void shouldEnforceRateLimiting() {
        String phoneNumber = "+1234567890";
        OtpRequest request = new OtpRequest(phoneNumber, OtpChannel.SMS, "login");

        // Send 3 OTPs (within rate limit)
        for (int i = 0; i < 3; i++) {
            OtpResponse response = otpService.sendOtp(request);
            assertTrue(response.isSuccess(), "Request " + (i + 1) + " should succeed");
        }

        // 4th request should be rate limited
        OtpResponse rateLimitedResponse = otpService.sendOtp(request);
        assertFalse(rateLimitedResponse.isSuccess());
        assertTrue(rateLimitedResponse.getMessage().contains("Too many OTP requests"));
        assertTrue(rateLimitedResponse.getRateLimitResetSeconds() > 0);
    }

    @Test
    @DisplayName("Should verify OTP correctly")
    void shouldVerifyOtpCorrectly() {
        // Send OTP first
        OtpRequest sendRequest = new OtpRequest("+1234567890", OtpChannel.SMS, "login");
        OtpResponse sendResponse = otpService.sendOtp(sendRequest);
        assertTrue(sendResponse.isSuccess());

        // Get the generated OTP code using reflection (for testing)
        String otpCode = getStoredOtpCode(sendResponse.getOtpId());

        // Verify with correct code
        OtpVerificationRequest verifyRequest = new OtpVerificationRequest("+1234567890", otpCode, "login");
        boolean isValid = otpService.verifyOtp(verifyRequest);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should reject incorrect OTP code")
    void shouldRejectIncorrectOtpCode() {
        // Send OTP first
        OtpRequest sendRequest = new OtpRequest("+1234567890", OtpChannel.SMS, "login");
        otpService.sendOtp(sendRequest);

        // Verify with incorrect code
        OtpVerificationRequest verifyRequest = new OtpVerificationRequest("+1234567890", "999999", "login");
        boolean isValid = otpService.verifyOtp(verifyRequest);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject OTP verification with wrong purpose")
    void shouldRejectOtpVerificationWithWrongPurpose() {
        // Send OTP for login
        OtpRequest sendRequest = new OtpRequest("+1234567890", OtpChannel.SMS, "login");
        OtpResponse sendResponse = otpService.sendOtp(sendRequest);
        assertTrue(sendResponse.isSuccess());

        String otpCode = getStoredOtpCode(sendResponse.getOtpId());

        // Try to verify with different purpose
        OtpVerificationRequest verifyRequest = new OtpVerificationRequest("+1234567890", otpCode, "registration");
        boolean isValid = otpService.verifyOtp(verifyRequest);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should resend OTP successfully")
    void shouldResendOtpSuccessfully() {
        // Send initial OTP
        OtpRequest request = new OtpRequest("+1234567890", OtpChannel.SMS, "login");
        OtpResponse initialResponse = otpService.sendOtp(request);
        assertTrue(initialResponse.isSuccess());

        // Resend OTP
        OtpResponse resendResponse = otpService.resendOtp("+1234567890");
        
        assertTrue(resendResponse.isSuccess());
        assertEquals("OTP resent successfully via SMS Text Message", resendResponse.getMessage());
        assertEquals(initialResponse.getOtpId(), resendResponse.getOtpId()); // Same OTP ID
    }

    @Test
    @DisplayName("Should limit resend attempts")
    void shouldLimitResendAttempts() {
        // Send initial OTP
        OtpRequest request = new OtpRequest("+1234567890", OtpChannel.SMS, "login");
        otpService.sendOtp(request);

        // Resend OTP twice (within limit)
        for (int i = 0; i < 2; i++) {
            OtpResponse resendResponse = otpService.resendOtp("+1234567890");
            assertTrue(resendResponse.isSuccess(), "Resend " + (i + 1) + " should succeed");
        }

        // Third resend should fail
        OtpResponse failedResend = otpService.resendOtp("+1234567890");
        assertFalse(failedResend.isSuccess());
        assertEquals("Maximum resend attempts exceeded or OTP expired", failedResend.getMessage());
    }

    @Test
    @DisplayName("Should invalidate OTP codes")
    void shouldInvalidateOtpCodes() {
        String phoneNumber = "+1234567890";
        
        // Send multiple OTPs for different purposes
        otpService.sendOtp(new OtpRequest(phoneNumber, OtpChannel.SMS, "login"));
        otpService.sendOtp(new OtpRequest(phoneNumber, OtpChannel.SMS, "registration"));

        // Invalidate all OTPs for the recipient
        int invalidatedCount = otpService.invalidateOtpCodes(phoneNumber);
        
        assertEquals(2, invalidatedCount);

        // Verify that verification fails after invalidation
        OtpVerificationRequest verifyRequest = new OtpVerificationRequest(phoneNumber, "123456", "login");
        boolean isValid = otpService.verifyOtp(verifyRequest);
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should validate recipients correctly")
    void shouldValidateRecipientsCorrectly() {
        // Valid recipients
        assertTrue(otpService.isValidRecipient("+1234567890", OtpChannel.SMS));
        assertTrue(otpService.isValidRecipient("user@example.com", OtpChannel.EMAIL));

        // Invalid recipients
        assertFalse(otpService.isValidRecipient("1234567890", OtpChannel.SMS)); // Missing +
        assertFalse(otpService.isValidRecipient("invalid-email", OtpChannel.EMAIL));
        assertFalse(otpService.isValidRecipient(null, OtpChannel.SMS));
        assertFalse(otpService.isValidRecipient("+1234567890", null));
    }

    @Test
    @DisplayName("Should track rate limiting correctly")
    void shouldTrackRateLimitingCorrectly() {
        String phoneNumber = "+1234567890";
        
        // Initially not rate limited
        assertFalse(otpService.isRateLimited(phoneNumber));
        assertEquals(0, otpService.getRateLimitResetTime(phoneNumber));

        // Send requests up to the limit
        for (int i = 0; i < 3; i++) {
            otpService.sendOtp(new OtpRequest(phoneNumber, OtpChannel.SMS, "login"));
        }

        // Should be rate limited after 3 requests
        assertTrue(otpService.isRateLimited(phoneNumber));
        assertTrue(otpService.getRateLimitResetTime(phoneNumber) > 0);
    }

    @Test
    @DisplayName("Should provide OTP statistics")
    void shouldProvideOtpStatistics() {
        String phoneNumber = "+1234567890";

        // Send some OTPs
        otpService.sendOtp(new OtpRequest(phoneNumber, OtpChannel.SMS, "login"));
        otpService.sendOtp(new OtpRequest(phoneNumber, OtpChannel.SMS, "registration"));

        // Get statistics
        OtpStatistics stats = otpService.getOtpStatistics(phoneNumber);
        
        assertNotNull(stats);
        assertEquals(phoneNumber, stats.getRecipient());
        assertEquals(2, stats.getTotalOtpsSent());
        assertNotNull(stats.getFirstOtpSent());
        assertNotNull(stats.getLastOtpSent());
    }

    @Test
    @DisplayName("Should provide global statistics")
    void shouldProvideGlobalStatistics() {
        // Send OTPs to different recipients
        otpService.sendOtp(new OtpRequest("+1234567890", OtpChannel.SMS, "login"));
        otpService.sendOtp(new OtpRequest("user@example.com", OtpChannel.EMAIL, "registration"));

        // Get global statistics
        OtpStatistics globalStats = otpService.getOtpStatistics(null);
        
        assertNotNull(globalStats);
        assertNull(globalStats.getRecipient());
        assertEquals(2, globalStats.getTotalOtpsSent());
    }

    @Test
    @DisplayName("Should handle concurrent requests safely")
    void shouldHandleConcurrentRequestsSafely() throws InterruptedException {
        String phoneNumber = "+1234567890";
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        boolean[] results = new boolean[threadCount];

        // Create threads that send OTP requests concurrently
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                OtpRequest request = new OtpRequest(phoneNumber, OtpChannel.SMS, "login");
                OtpResponse response = otpService.sendOtp(request);
                results[index] = response.isSuccess();
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Only first 3 requests should succeed due to rate limiting
        int successCount = 0;
        for (boolean result : results) {
            if (result) successCount++;
        }

        assertEquals(3, successCount, "Only 3 concurrent requests should succeed due to rate limiting");
    }

    @Test
    @DisplayName("Should handle OTP expiry correctly")
    void shouldHandleOtpExpiryCorrectly() {
        // Set very short expiry for testing
        ReflectionTestUtils.setField(otpService, "expiryMinutes", 0); // Immediate expiry

        OtpRequest request = new OtpRequest("+1234567890", OtpChannel.SMS, "login");
        OtpResponse response = otpService.sendOtp(request);
        assertTrue(response.isSuccess());

        String otpCode = getStoredOtpCode(response.getOtpId());

        // Verification should fail due to immediate expiry
        OtpVerificationRequest verifyRequest = new OtpVerificationRequest("+1234567890", otpCode, "login");
        boolean isValid = otpService.verifyOtp(verifyRequest);
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should limit verification attempts")
    void shouldLimitVerificationAttempts() {
        // Send OTP
        OtpRequest request = new OtpRequest("+1234567890", OtpChannel.SMS, "login");
        otpService.sendOtp(request);

        // Try incorrect verification 3 times
        for (int i = 0; i < 3; i++) {
            OtpVerificationRequest verifyRequest = new OtpVerificationRequest("+1234567890", "999999", "login");
            boolean isValid = otpService.verifyOtp(verifyRequest);
            assertFalse(isValid, "Attempt " + (i + 1) + " should fail");
        }

        // OTP should be removed after max attempts, so even correct code won't work
        OtpVerificationRequest verifyRequest = new OtpVerificationRequest("+1234567890", "123456", "login");
        boolean isValid = otpService.verifyOtp(verifyRequest);
        assertFalse(isValid);
    }

    // Helper method to get stored OTP code for testing
    @SuppressWarnings("unchecked")
    private String getStoredOtpCode(String otpId) {
        try {
            var activeOtps = (java.util.Map<String, Object>) ReflectionTestUtils.getField(otpService, "activeOtps");
            if (activeOtps == null) {
                throw new RuntimeException("Active OTPs map is null");
            }
            Object otpData = activeOtps.get(otpId);
            if (otpData == null) {
                throw new RuntimeException("OTP data not found for ID: " + otpId);
            }
            return (String) ReflectionTestUtils.getField(otpData, "code");
        } catch (Exception e) {
            throw new RuntimeException("Failed to get OTP code for testing", e);
        }
    }
}
