package com.cloudsuites.framework.modules.auth.service.impl;

import com.cloudsuites.framework.services.auth.PasswordValidator;
import com.cloudsuites.framework.services.auth.dto.PasswordChangeRequest;
import com.cloudsuites.framework.services.auth.dto.PasswordResetRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for PasswordServiceImpl.
 * 
 * Tests cover:
 * - Password hashing and verification
 * - Password complexity validation
 * - Reset token generation and validation
 * - Password change operations
 * - Strong password generation
 * - Breach checking
 */
class PasswordServiceImplTest {
    
    private PasswordServiceImpl passwordService;
    
    @BeforeEach
    void setUp() {
        passwordService = new PasswordServiceImpl();
    }
    
    @Test
    @DisplayName("Should hash password securely with BCrypt")
    void testPasswordHashing() {
        String plainPassword = "TestPassword123!";
        
        String hashedPassword = passwordService.hashPassword(plainPassword);
        
        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$"));
        assertTrue(hashedPassword.length() >= 59); // BCrypt hash length
    }
    
    @Test
    @DisplayName("Should verify correct password")
    void testPasswordVerification() {
        String plainPassword = "TestPassword123!";
        String hashedPassword = passwordService.hashPassword(plainPassword);
        
        boolean isValid = passwordService.verifyPassword(plainPassword, hashedPassword);
        
        assertTrue(isValid);
    }
    
    @Test
    @DisplayName("Should reject incorrect password")
    void testIncorrectPasswordVerification() {
        String plainPassword = "TestPassword123!";
        String wrongPassword = "WrongPassword123!";
        String hashedPassword = passwordService.hashPassword(plainPassword);
        
        boolean isValid = passwordService.verifyPassword(wrongPassword, hashedPassword);
        
        assertFalse(isValid);
    }
    
    @Test
    @DisplayName("Should handle null inputs in password verification")
    void testPasswordVerificationWithNulls() {
        assertFalse(passwordService.verifyPassword(null, "hash"));
        assertFalse(passwordService.verifyPassword("password", null));
        assertFalse(passwordService.verifyPassword(null, null));
    }
    
    @Test
    @DisplayName("Should throw exception for null password hashing")
    void testHashPasswordWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.hashPassword(null);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.hashPassword("");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.hashPassword("   ");
        });
    }
    
    @Test
    @DisplayName("Should validate strong password complexity")
    void testStrongPasswordValidation() {
        String strongPassword = "MySec4re!P@ssw8rd";  // Use same valid password from other tests
        
        boolean isValid = passwordService.validatePasswordComplexity(strongPassword);
        List<String> errors = passwordService.getPasswordValidationErrors(strongPassword);
        
        assertTrue(isValid);
        assertTrue(errors.isEmpty());
    }
    
    @Test
    @DisplayName("Should reject weak passwords")
    void testWeakPasswordValidation() {
        String[] weakPasswords = {
            "weak",           // Too short
            "password",       // Common password
            "12345678",       // Only digits
            "UPPERCASE",      // Only uppercase
            "lowercase",      // Only lowercase
            "NoSpecial123",   // No special characters
        };
        
        for (String weakPassword : weakPasswords) {
            boolean isValid = passwordService.validatePasswordComplexity(weakPassword);
            List<String> errors = passwordService.getPasswordValidationErrors(weakPassword);
            
            assertFalse(isValid, "Password should be invalid: " + weakPassword);
            assertFalse(errors.isEmpty(), "Should have validation errors for: " + weakPassword);
        }
    }
    
    @Test
    @DisplayName("Should generate password reset token")
    void testPasswordResetTokenGeneration() {
        String userId = "user123";
        
        String token = passwordService.generatePasswordResetToken(userId);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(64, token.length()); // 32 bytes = 64 hex characters
        assertTrue(token.matches("[0-9a-f]+"));
    }
    
    @Test
    @DisplayName("Should validate reset token correctly")
    void testPasswordResetTokenValidation() {
        String userId = "user123";
        
        // Use the same service instance that generated the token
        String token = passwordService.generatePasswordResetToken(userId);
        System.out.println("Generated token: " + token);
        System.out.println("Validating with userId: " + userId);
        
        boolean isValid = passwordService.validatePasswordResetToken(token, userId);
        System.out.println("Validation result: " + isValid);
        
        assertTrue(isValid);
    }
    
    @Test
    @DisplayName("Should reject invalid reset tokens")
    void testInvalidResetTokenValidation() {
        String userId = "user123";
        String validToken = passwordService.generatePasswordResetToken(userId);
        
        // Wrong token
        assertFalse(passwordService.validatePasswordResetToken("invalid-token", userId));
        
        // Wrong user ID
        assertFalse(passwordService.validatePasswordResetToken(validToken, "wrong-user"));
        
        // Null inputs
        assertFalse(passwordService.validatePasswordResetToken(null, userId));
        assertFalse(passwordService.validatePasswordResetToken(validToken, null));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid reset token generation")
    void testInvalidResetTokenGeneration() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.generatePasswordResetToken(null);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.generatePasswordResetToken("");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.generatePasswordResetToken("   ");
        });
    }
    
    @Test
    @DisplayName("Should process valid password change request")
    void testValidPasswordChange() {
        PasswordChangeRequest request = new PasswordChangeRequest(
            "user123", 
            "OldPassword123!", 
            "NewStrongP@ssw0rd", 
            "NewStrongP@ssw0rd"
        );
        
        boolean result = passwordService.changePassword(request);
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should reject password change with mismatched confirmation")
    void testPasswordChangeWithMismatchedConfirmation() {
        PasswordChangeRequest request = new PasswordChangeRequest(
            "user123", 
            "OldPassword123!", 
            "NewStrongP@ssw0rd", 
            "DifferentPassword"
        );
        
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            passwordService.changePassword(request);
        });
        
        assertTrue(exception.getMessage().contains("do not match"));
    }
    
    @Test
    @DisplayName("Should reject password change with weak new password")
    void testPasswordChangeWithWeakPassword() {
        PasswordChangeRequest request = new PasswordChangeRequest(
            "user123", 
            "OldPassword123!", 
            "weak", 
            "weak"
        );
        
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            passwordService.changePassword(request);
        });
        
        assertTrue(exception.getMessage().contains("complexity requirements"));
    }
    
    @Test
    @DisplayName("Should initiate password reset successfully")
    void testPasswordResetInitiation() {
        PasswordResetRequest request = new PasswordResetRequest("user@example.com");
        
        boolean result = passwordService.initiatePasswordReset(request);
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should detect common breached passwords")
    void testBreachedPasswordDetection() {
        String[] breachedPasswords = {
            "password", "123456", "qwerty", "admin", "letmein"
        };
        
        for (String breached : breachedPasswords) {
            boolean isBreached = passwordService.isPasswordBreached(breached);
            assertTrue(isBreached, "Should detect breached password: " + breached);
        }
    }
    
    @Test
    @DisplayName("Should not flag strong unique passwords as breached")
    void testNonBreachedPasswordDetection() {
        String strongPassword = "UniqueStr0ngP@ssw0rd!";
        
        boolean isBreached = passwordService.isPasswordBreached(strongPassword);
        
        assertFalse(isBreached);
    }
    
    @Test
    @DisplayName("Should generate strong passwords of specified length")
    void testStrongPasswordGeneration() {
        int[] lengths = {8, 12, 16, 20};
        
        for (int length : lengths) {
            String generated = passwordService.generateStrongPassword(length);
            
            assertNotNull(generated);
            assertEquals(length, generated.length());
            assertTrue(passwordService.validatePasswordComplexity(generated), 
                      "Generated password should be valid: " + generated);
        }
    }
    
    @Test
    @DisplayName("Should throw exception for invalid password generation length")
    void testInvalidPasswordGenerationLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.generateStrongPassword(7); // Too short
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.generateStrongPassword(0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.generateStrongPassword(-1);
        });
    }
    
    @Test
    @DisplayName("Should generate different passwords each time")
    void testPasswordGenerationUniqueness() {
        String password1 = passwordService.generateStrongPassword(12);
        String password2 = passwordService.generateStrongPassword(12);
        String password3 = passwordService.generateStrongPassword(12);
        
        assertNotEquals(password1, password2);
        assertNotEquals(password2, password3);
        assertNotEquals(password1, password3);
    }
    
    @Test
    @DisplayName("Should handle null input gracefully in breach checking")
    void testBreachCheckingWithNull() {
        boolean isBreached = passwordService.isPasswordBreached(null);
        
        assertFalse(isBreached);
    }
    
    @Test
    @DisplayName("Should validate PasswordValidator integration")
    void testPasswordValidatorIntegration() {
        // Test that service uses PasswordValidator correctly
        String testPassword = "TestP@ssw0rd123";
        
        boolean serviceResult = passwordService.validatePasswordComplexity(testPassword);
        boolean validatorResult = PasswordValidator.isValidPassword(testPassword);
        
        assertEquals(validatorResult, serviceResult);
        
        List<String> serviceErrors = passwordService.getPasswordValidationErrors(testPassword);
        List<String> validatorErrors = PasswordValidator.getValidationErrors(testPassword);
        
        assertEquals(validatorErrors, serviceErrors);
    }
}
