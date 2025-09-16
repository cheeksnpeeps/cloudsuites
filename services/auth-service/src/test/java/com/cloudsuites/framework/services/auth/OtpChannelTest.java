package com.cloudsuites.framework.services.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OtpChannel enum.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
@DisplayName("OTP Channel Tests")
class OtpChannelTest {

    @Test
    @DisplayName("Should have correct channel codes")
    void shouldHaveCorrectChannelCodes() {
        assertEquals("sms", OtpChannel.SMS.getCode());
        assertEquals("email", OtpChannel.EMAIL.getCode());
    }

    @Test
    @DisplayName("Should have correct display names")
    void shouldHaveCorrectDisplayNames() {
        assertEquals("SMS Text Message", OtpChannel.SMS.getDisplayName());
        assertEquals("Email Message", OtpChannel.EMAIL.getDisplayName());
    }

    @Test
    @DisplayName("Should get channel from valid code")
    void shouldGetChannelFromValidCode() {
        assertEquals(OtpChannel.SMS, OtpChannel.fromCode("sms"));
        assertEquals(OtpChannel.EMAIL, OtpChannel.fromCode("email"));
        assertEquals(OtpChannel.SMS, OtpChannel.fromCode("SMS"));
        assertEquals(OtpChannel.EMAIL, OtpChannel.fromCode("EMAIL"));
    }

    @Test
    @DisplayName("Should throw exception for invalid code")
    void shouldThrowExceptionForInvalidCode() {
        assertThrows(IllegalArgumentException.class, () -> OtpChannel.fromCode("invalid"));
        assertThrows(IllegalArgumentException.class, () -> OtpChannel.fromCode(""));
        assertThrows(IllegalArgumentException.class, () -> OtpChannel.fromCode(null));
    }

    @Test
    @DisplayName("SMS should validate phone numbers correctly")
    void smsChannelShouldValidatePhoneNumbers() {
        // Valid E.164 format phone numbers
        assertTrue(OtpChannel.SMS.isValidRecipient("+1234567890"));
        assertTrue(OtpChannel.SMS.isValidRecipient("+12345678901"));
        assertTrue(OtpChannel.SMS.isValidRecipient("+123456789012345")); // Max 15 digits
        
        // Invalid phone numbers
        assertFalse(OtpChannel.SMS.isValidRecipient("1234567890")); // Missing +
        assertFalse(OtpChannel.SMS.isValidRecipient("+0123456789")); // Starts with 0
        assertFalse(OtpChannel.SMS.isValidRecipient("+1234567890123456")); // Too long
        assertFalse(OtpChannel.SMS.isValidRecipient("+1")); // Too short
        assertFalse(OtpChannel.SMS.isValidRecipient("+12abc34567")); // Contains letters
        assertFalse(OtpChannel.SMS.isValidRecipient("")); // Empty
        assertFalse(OtpChannel.SMS.isValidRecipient(null)); // Null
    }

    @Test
    @DisplayName("EMAIL should validate email addresses correctly")
    void emailChannelShouldValidateEmailAddresses() {
        // Valid email addresses
        assertTrue(OtpChannel.EMAIL.isValidRecipient("user@example.com"));
        assertTrue(OtpChannel.EMAIL.isValidRecipient("user.name@example.com"));
        assertTrue(OtpChannel.EMAIL.isValidRecipient("user+tag@example.co.uk"));
        assertTrue(OtpChannel.EMAIL.isValidRecipient("test123@domain-name.org"));
        
        // Invalid email addresses
        assertFalse(OtpChannel.EMAIL.isValidRecipient("user@")); // Missing domain
        assertFalse(OtpChannel.EMAIL.isValidRecipient("@example.com")); // Missing user
        assertFalse(OtpChannel.EMAIL.isValidRecipient("user.example.com")); // Missing @
        assertFalse(OtpChannel.EMAIL.isValidRecipient("user@example")); // Missing TLD
        assertFalse(OtpChannel.EMAIL.isValidRecipient("user space@example.com")); // Space in user
        assertFalse(OtpChannel.EMAIL.isValidRecipient("")); // Empty
        assertFalse(OtpChannel.EMAIL.isValidRecipient(null)); // Null
    }

    @Test
    @DisplayName("Should cross-validate channels and recipients")
    void shouldCrossValidateChannelsAndRecipients() {
        // SMS channel should reject email addresses
        assertFalse(OtpChannel.SMS.isValidRecipient("user@example.com"));
        
        // EMAIL channel should reject phone numbers
        assertFalse(OtpChannel.EMAIL.isValidRecipient("+1234567890"));
    }

    @Test
    @DisplayName("Should handle edge cases")
    void shouldHandleEdgeCases() {
        // Whitespace handling
        assertFalse(OtpChannel.SMS.isValidRecipient("  "));
        assertFalse(OtpChannel.EMAIL.isValidRecipient("  "));
        
        // Very long inputs
        String longPhone = "+1" + "2".repeat(20);
        assertFalse(OtpChannel.SMS.isValidRecipient(longPhone));
        
        String longEmail = "a".repeat(50) + "@" + "b".repeat(50) + ".com";
        // Should still validate based on pattern, not length
        assertTrue(OtpChannel.EMAIL.isValidRecipient(longEmail));
    }
}
