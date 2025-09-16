package com.cloudsuites.framework.services.auth.dto;

import com.cloudsuites.framework.services.auth.OtpChannel;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

/**
 * Unit tests for OTP DTOs validation.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
@DisplayName("OTP DTOs Validation Tests")
class OtpDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Valid OtpRequest should pass validation")
    void validOtpRequestShouldPassValidation() {
        OtpRequest request = new OtpRequest("+1234567890", OtpChannel.SMS, "login");
        request.setUserAgent("Mozilla/5.0");
        request.setIpAddress("192.168.1.1");
        request.setSessionId("sess-123");

        Set<ConstraintViolation<OtpRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty(), "Valid request should have no violations");
        assertEquals("+1234567890", request.getRecipient());
        assertEquals(OtpChannel.SMS, request.getChannel());
        assertEquals("login", request.getPurpose());
    }

    @Test
    @DisplayName("OtpRequest with null recipient should fail validation")
    void otpRequestWithNullRecipientShouldFail() {
        OtpRequest request = new OtpRequest(null, OtpChannel.SMS, "login");
        
        Set<ConstraintViolation<OtpRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Recipient is required")));
    }

    @Test
    @DisplayName("OtpRequest with empty recipient should fail validation")
    void otpRequestWithEmptyRecipientShouldFail() {
        OtpRequest request = new OtpRequest("", OtpChannel.EMAIL, "registration");
        
        Set<ConstraintViolation<OtpRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Recipient is required")));
    }

    @Test
    @DisplayName("OtpRequest with null channel should fail validation")
    void otpRequestWithNullChannelShouldFail() {
        OtpRequest request = new OtpRequest("user@example.com", null, "password_reset");
        
        Set<ConstraintViolation<OtpRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("OTP channel is required")));
    }

    @Test
    @DisplayName("OtpRequest with null purpose should fail validation")
    void otpRequestWithNullPurposeShouldFail() {
        OtpRequest request = new OtpRequest("user@example.com", OtpChannel.EMAIL, null);
        
        Set<ConstraintViolation<OtpRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Purpose is required")));
    }

    @Test
    @DisplayName("OtpRequest with too long recipient should fail validation")
    void otpRequestWithTooLongRecipientShouldFail() {
        String longRecipient = "a".repeat(101) + "@example.com";
        OtpRequest request = new OtpRequest(longRecipient, OtpChannel.EMAIL, "login");
        
        Set<ConstraintViolation<OtpRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must not exceed 100 characters")));
    }

    @Test
    @DisplayName("OtpRequest with too long purpose should fail validation")
    void otpRequestWithTooLongPurposeShouldFail() {
        String longPurpose = "a".repeat(51);
        OtpRequest request = new OtpRequest("user@example.com", OtpChannel.EMAIL, longPurpose);
        
        Set<ConstraintViolation<OtpRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must not exceed 50 characters")));
    }

    @Test
    @DisplayName("OtpRequest toString should mask recipient properly")
    void otpRequestToStringShouldMaskRecipient() {
        OtpRequest smsRequest = new OtpRequest("+1234567890", OtpChannel.SMS, "login");
        smsRequest.setIpAddress("192.168.1.1");
        
        String smsString = smsRequest.toString();
        assertFalse(smsString.contains("+1234567890"));
        assertTrue(smsString.contains("+***-***-7890"));
        
        OtpRequest emailRequest = new OtpRequest("user@example.com", OtpChannel.EMAIL, "login");
        String emailString = emailRequest.toString();
        assertFalse(emailString.contains("user@example.com"));
        assertTrue(emailString.contains("us***@example.com"));
    }

    @Test
    @DisplayName("Valid OtpVerificationRequest should pass validation")
    void validOtpVerificationRequestShouldPassValidation() {
        OtpVerificationRequest request = new OtpVerificationRequest("user@example.com", "123456", "login");
        request.setUserAgent("Mozilla/5.0");
        request.setIpAddress("192.168.1.1");
        
        Set<ConstraintViolation<OtpVerificationRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty());
        assertEquals("user@example.com", request.getRecipient());
        assertEquals("123456", request.getOtpCode());
        assertEquals("login", request.getPurpose());
    }

    @Test
    @DisplayName("OtpVerificationRequest with invalid OTP code should fail validation")
    void otpVerificationRequestWithInvalidOtpCodeShouldFail() {
        // Test various invalid OTP codes
        String[] invalidCodes = {"12345", "1234567", "abc123", "12-34-56", "", null};
        
        for (String invalidCode : invalidCodes) {
            OtpVerificationRequest request = new OtpVerificationRequest("user@example.com", invalidCode, "login");
            Set<ConstraintViolation<OtpVerificationRequest>> violations = validator.validate(request);
            
            assertFalse(violations.isEmpty(), "Invalid OTP code should fail validation: " + invalidCode);
            
            if (invalidCode == null || invalidCode.isEmpty()) {
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("OTP code is required")));
            } else {
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be exactly 6 digits")));
            }
        }
    }

    @Test
    @DisplayName("OtpVerificationRequest toString should mask sensitive data")
    void otpVerificationRequestToStringShouldMaskSensitiveData() {
        OtpVerificationRequest request = new OtpVerificationRequest("user@example.com", "123456", "login");
        request.setIpAddress("192.168.1.1");
        
        String requestString = request.toString();
        
        // Should mask recipient
        assertFalse(requestString.contains("user@example.com"));
        assertTrue(requestString.contains("us***@example.com"));
        
        // Should mask OTP code
        assertFalse(requestString.contains("123456"));
        assertTrue(requestString.contains("******"));
        
        // Should include other info
        assertTrue(requestString.contains("login"));
        assertTrue(requestString.contains("192.168.1.1"));
    }

    @Test
    @DisplayName("OtpResponse should create success responses correctly")
    void otpResponseShouldCreateSuccessResponsesCorrectly() {
        OtpResponse response = OtpResponse.success(
            "OTP sent successfully",
            "otp-123",
            OtpChannel.SMS,
            "+***-***-7890",
            null
        );
        
        assertTrue(response.isSuccess());
        assertEquals("OTP sent successfully", response.getMessage());
        assertEquals("otp-123", response.getOtpId());
        assertEquals(OtpChannel.SMS, response.getChannel());
        assertEquals("+***-***-7890", response.getRecipient());
    }

    @Test
    @DisplayName("OtpResponse should create failure responses correctly")
    void otpResponseShouldCreateFailureResponsesCorrectly() {
        OtpResponse response = OtpResponse.failure("Invalid recipient format");
        
        assertFalse(response.isSuccess());
        assertEquals("Invalid recipient format", response.getMessage());
        assertNull(response.getOtpId());
        assertNull(response.getChannel());
    }

    @Test
    @DisplayName("OtpResponse should create rate limited responses correctly")
    void otpResponseShouldCreateRateLimitedResponsesCorrectly() {
        OtpResponse response = OtpResponse.rateLimited("Rate limit exceeded", 300);
        
        assertFalse(response.isSuccess());
        assertEquals("Rate limit exceeded", response.getMessage());
        assertEquals(300, response.getRateLimitResetSeconds());
    }

    @Test
    @DisplayName("OtpStatistics should calculate success rate correctly")
    void otpStatisticsShouldCalculateSuccessRateCorrectly() {
        OtpStatistics stats = new OtpStatistics("user@example.com");
        
        // Test with no verifications
        stats.calculateSuccessRate();
        assertEquals(0.0, stats.getSuccessRate());
        
        // Test with some verifications
        stats.setTotalOtpsVerified(10);
        stats.setSuccessfulVerifications(8);
        stats.calculateSuccessRate();
        assertEquals(80.0, stats.getSuccessRate(), 0.01);
        
        // Test with 100% success rate
        stats.setSuccessfulVerifications(10);
        stats.calculateSuccessRate();
        assertEquals(100.0, stats.getSuccessRate(), 0.01);
    }

    @Test
    @DisplayName("OtpStatistics toString should mask recipient properly")
    void otpStatisticsToStringShouldMaskRecipientProperly() {
        OtpStatistics phoneStats = new OtpStatistics("+1234567890");
        phoneStats.setTotalOtpsSent(5);
        phoneStats.setSuccessRate(80.0);
        
        String phoneString = phoneStats.toString();
        assertFalse(phoneString.contains("+1234567890"));
        assertTrue(phoneString.contains("+***-***-7890"));
        
        OtpStatistics emailStats = new OtpStatistics("user@example.com");
        String emailString = emailStats.toString();
        assertFalse(emailString.contains("user@example.com"));
        assertTrue(emailString.contains("us***@example.com"));
        
        OtpStatistics globalStats = new OtpStatistics(null);
        String globalString = globalStats.toString();
        assertTrue(globalString.contains("GLOBAL"));
    }
}
