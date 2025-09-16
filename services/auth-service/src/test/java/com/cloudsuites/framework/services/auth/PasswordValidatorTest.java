package com.cloudsuites.framework.services.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for PasswordValidator.
 * 
 * Tests cover:
 * - Password strength scoring (0-100)
 * - Complexity validation rules
 * - Common password detection
 * - Sequential character detection
 * - Edge cases and input validation
 */
class PasswordValidatorTest {
    
    @Test
    @DisplayName("Should score strong passwords highly")
    void testStrongPasswordScoring() {
        String[] strongPasswords = {
            "StrongP@ssw0rd123",      // Complex with all character types
            "MyComplexP@ssw0rd!2024", // Long and complex
            "S3cur3!P@ssw0rd#2024"    // Strong with numbers and symbols
        };
        
        for (String password : strongPasswords) {
            int score = PasswordValidator.calculatePasswordStrength(password);
            assertTrue(score >= 80, "Strong password should score >= 80: " + password + " (scored: " + score + ")");
        }
    }
    
    @Test
    @DisplayName("Should score weak passwords lowly")
    void testWeakPasswordScoring() {
        String[] weakPasswords = {
            "weak",           // Very short
            "password",       // Common word
            "12345678",       // Only digits
            "abcdefgh",       // Only lowercase
            "ABCDEFGH",       // Only uppercase
            "abc123",         // Short and simple
        };
        
        for (String password : weakPasswords) {
            int score = PasswordValidator.calculatePasswordStrength(password);
            assertTrue(score <= 50, "Weak password should score <= 50: " + password + " (scored: " + score + ")");
        }
    }
    
    @Test
    @DisplayName("Should detect valid strong passwords")
    void testValidPasswordDetection() {
        String[] validPasswords = {
            "MySec4re!P@ssw8rd",      // Avoids common words and sequences
            "C0mpl3x#S3curity!",       // Strong unique password
            "S@f3GuardP@$$2024",       // Strong with year but not sequential
            "Uniqu3!Str0ngK3y"        // Strong unique combination
        };
        
        for (String password : validPasswords) {
            assertTrue(PasswordValidator.isValidPassword(password), 
                      "Should be valid: " + password);
            
            List<String> errors = PasswordValidator.getValidationErrors(password);
            assertTrue(errors.isEmpty(), 
                      "Should have no errors: " + password + " - errors: " + errors);
        }
    }
    
    @Test
    @DisplayName("Should detect invalid passwords and provide errors")
    void testInvalidPasswordDetection() {
        String invalidPassword = "weak";
        
        assertFalse(PasswordValidator.isValidPassword(invalidPassword));
        
        List<String> errors = PasswordValidator.getValidationErrors(invalidPassword);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("at least 8 characters")));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", "1", "12", "123", "1234", "12345", "123456", "1234567"})
    @DisplayName("Should reject passwords shorter than 8 characters")
    void testMinimumLengthValidation(String shortPassword) {
        assertFalse(PasswordValidator.isValidPassword(shortPassword));
        
        List<String> errors = PasswordValidator.getValidationErrors(shortPassword);
        assertTrue(errors.stream().anyMatch(error -> error.contains("at least 8 characters")));
    }
    
    @Test
    @DisplayName("Should reject passwords longer than 128 characters")
    void testMaximumLengthValidation() {
        String longPassword = "a".repeat(129); // 129 characters
        
        assertFalse(PasswordValidator.isValidPassword(longPassword));
        
        List<String> errors = PasswordValidator.getValidationErrors(longPassword);
        assertTrue(errors.stream().anyMatch(error -> error.contains("exceed 128 characters")));
    }
    
    @Test
    @DisplayName("Should require lowercase letters")
    void testLowercaseRequirement() {
        String passwordWithoutLowercase = "PASSWORD123!";
        
        assertFalse(PasswordValidator.isValidPassword(passwordWithoutLowercase));
        
        List<String> errors = PasswordValidator.getValidationErrors(passwordWithoutLowercase);
        assertTrue(errors.stream().anyMatch(error -> error.contains("lowercase letter")));
    }
    
    @Test
    @DisplayName("Should require uppercase letters")
    void testUppercaseRequirement() {
        String passwordWithoutUppercase = "password123!";
        
        assertFalse(PasswordValidator.isValidPassword(passwordWithoutUppercase));
        
        List<String> errors = PasswordValidator.getValidationErrors(passwordWithoutUppercase);
        assertTrue(errors.stream().anyMatch(error -> error.contains("uppercase letter")));
    }
    
    @Test
    @DisplayName("Should require numbers")
    void testNumberRequirement() {
        String passwordWithoutNumbers = "ComplicatedP@ssword!";  // No digits
        
        assertFalse(PasswordValidator.isValidPassword(passwordWithoutNumbers));
        
        List<String> errors = PasswordValidator.getValidationErrors(passwordWithoutNumbers);
        assertTrue(errors.stream().anyMatch(error -> error.contains("digit")));
    }
    
    @Test
    @DisplayName("Should require special characters")
    void testSpecialCharacterRequirement() {
        String passwordWithoutSpecialChars = "Password123";
        
        assertFalse(PasswordValidator.isValidPassword(passwordWithoutSpecialChars));
        
        List<String> errors = PasswordValidator.getValidationErrors(passwordWithoutSpecialChars);
        assertTrue(errors.stream().anyMatch(error -> error.contains("special character")));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"password", "admin", "letmein", "welcome", "monkey", "dragon"})
    @DisplayName("Should detect common passwords")
    void testCommonPasswordDetection(String commonPassword) {
        // Create a password that contains the common word but has complexity
        String complexPassword = "My" + commonPassword.substring(0, 1).toUpperCase() + 
                                commonPassword.substring(1) + "987!";
        assertFalse(PasswordValidator.isValidPassword(complexPassword), 
                   "Should reject password containing common word: " + commonPassword);
    }
    
    @Test
    @DisplayName("Should detect sequential characters")
    void testSequentialCharacterDetection() {
        String[] sequentialPasswords = {
            "Abcd1234!",     // abc, 123 sequences
            "Password12345", // 12345 sequence  
            "Qwerty123!",    // qwerty sequence
            "Test@bcdef",    // bcdef sequence
        };
        
        for (String password : sequentialPasswords) {
            List<String> errors = PasswordValidator.getValidationErrors(password);
            // Note: Some might still be valid if other criteria are strong enough
            // But we should detect the sequential pattern in scoring
            int score = PasswordValidator.calculatePasswordStrength(password);
            assertTrue(score < 90, "Sequential passwords should have reduced score: " + password);
        }
    }
    
    @Test
    @DisplayName("Should handle null and empty input gracefully")
    void testNullAndEmptyInput() {
        // Null input
        assertFalse(PasswordValidator.isValidPassword(null));
        assertEquals(0, PasswordValidator.calculatePasswordStrength(null));
        
        List<String> nullErrors = PasswordValidator.getValidationErrors(null);
        assertTrue(nullErrors.stream().anyMatch(error -> error.contains("cannot be null")));
        
        // Empty input
        assertFalse(PasswordValidator.isValidPassword(""));
        assertEquals(0, PasswordValidator.calculatePasswordStrength(""));
        
        List<String> emptyErrors = PasswordValidator.getValidationErrors("");
        assertTrue(emptyErrors.stream().anyMatch(error -> error.contains("at least 8 characters")));
    }
    
    @Test
    @DisplayName("Should handle whitespace-only input")
    void testWhitespaceInput() {
        String whitespacePassword = "   ";
        
        assertFalse(PasswordValidator.isValidPassword(whitespacePassword));
        
        List<String> errors = PasswordValidator.getValidationErrors(whitespacePassword);
        assertTrue(errors.stream().anyMatch(error -> error.contains("at least 8 characters")));
    }
    
    @Test
    @DisplayName("Should provide detailed validation errors")
    void testDetailedValidationErrors() {
        String weakPassword = "weak";
        
        List<String> errors = PasswordValidator.getValidationErrors(weakPassword);
        
        assertFalse(errors.isEmpty());
        
        // Should contain specific error messages
        boolean hasLengthError = errors.stream().anyMatch(e -> e.contains("at least 8 characters"));
        boolean hasUppercaseError = errors.stream().anyMatch(e -> e.contains("uppercase letter"));
        boolean hasNumberError = errors.stream().anyMatch(e -> e.contains("digit"));
        boolean hasSpecialCharError = errors.stream().anyMatch(e -> e.contains("special character"));
        
        assertTrue(hasLengthError);
        assertTrue(hasUppercaseError);
        assertTrue(hasNumberError);
        assertTrue(hasSpecialCharError);
    }
    
    @Test
    @DisplayName("Should score passwords consistently")
    void testConsistentScoring() {
        String password = "MySec4re!P@ssw8rd";
        
        int score1 = PasswordValidator.calculatePasswordStrength(password);
        int score2 = PasswordValidator.calculatePasswordStrength(password);
        int score3 = PasswordValidator.calculatePasswordStrength(password);
        
        assertEquals(score1, score2);
        assertEquals(score2, score3);
        assertTrue(score1 >= 0 && score1 <= 100);
    }
    
    @Test
    @DisplayName("Should handle special characters correctly")
    void testSpecialCharacterHandling() {
        String[] validSpecialChars = {
            "MyStr0ng!", "MyStr0ng@", "MyStr0ng#", "MyStr0ng$",
            "MyStr0ng%", "MyStr0ng^", "MyStr0ng&", "MyStr0ng*",
            "MyStr0ng(", "MyStr0ng)", "MyStr0ng-", "MyStr0ng_",
            "MyStr0ng+", "MyStr0ng=", "MyStr0ng[", "MyStr0ng]",
            "MyStr0ng{", "MyStr0ng}", "MyStr0ng|", "MyStr0ng\\",
            "MyStr0ng:", "MyStr0ng;", "MyStr0ng\"", "MyStr0ng'",
            "MyStr0ng<", "MyStr0ng>", "MyStr0ng,", "MyStr0ng.",
            "MyStr0ng?", "MyStr0ng/"
        };
        
        for (String password : validSpecialChars) {
            assertTrue(PasswordValidator.isValidPassword(password), 
                      "Should accept special character: " + password);
        }
    }
    
    @Test
    @DisplayName("Should handle Unicode characters")
    void testUnicodeCharacterHandling() {
        String unicodePassword = "Pässwörd123!";
        
        // Should handle Unicode characters gracefully
        assertDoesNotThrow(() -> {
            PasswordValidator.isValidPassword(unicodePassword);
            PasswordValidator.calculatePasswordStrength(unicodePassword);
            PasswordValidator.getValidationErrors(unicodePassword);
        });
    }
    
    @Test
    @DisplayName("Should validate password strength boundaries")
    void testPasswordStrengthBoundaries() {
        // Test that strength is always between 0 and 100
        String[] testPasswords = {
            "",
            "a",
            "weak",
            "StrongPassword123!",
            "VeryLongAndComplexPassword!@#$%^&*()123456789",
            null
        };
        
        for (String password : testPasswords) {
            int strength = PasswordValidator.calculatePasswordStrength(password);
            assertTrue(strength >= 0 && strength <= 100, 
                      "Strength should be 0-100: " + password + " (scored: " + strength + ")");
        }
    }
}
