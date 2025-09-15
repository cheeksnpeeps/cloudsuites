package com.cloudsuites.framework.services.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for password validation according to security policies.
 * 
 * Implements comprehensive password complexity rules including:
 * - Minimum length requirements
 * - Character diversity requirements
 * - Common password pattern detection
 * - Sequential character detection
 */
public class PasswordValidator {
    
    // Password policy constants
    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 128;
    public static final int MIN_UPPERCASE = 1;
    public static final int MIN_LOWERCASE = 1;
    public static final int MIN_DIGITS = 1;
    public static final int MIN_SPECIAL_CHARS = 1;
    
    // Regex patterns for validation
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");
    
    // Common weak passwords and patterns
    private static final String[] COMMON_PASSWORDS = {
        "password", "123456", "12345678", "qwerty", "abc123", "password123",
        "admin", "letmein", "welcome", "monkey", "dragon", "master",
        "trustno1", "111111", "iloveyou", "sunshine", "princess"
    };
    
    private static final String[] SEQUENTIAL_PATTERNS = {
        "123", "234", "345", "456", "567", "678", "789", "890",
        "abc", "bcd", "cde", "def", "efg", "fgh", "ghi", "hij"
    };
    
    /**
     * Validates password complexity according to security policies.
     * 
     * @param password the password to validate
     * @return true if password meets all complexity requirements
     */
    public static boolean isValidPassword(String password) {
        return getValidationErrors(password).isEmpty();
    }
    
    /**
     * Gets detailed list of validation errors for a password.
     * 
     * @param password the password to validate
     * @return list of validation error messages, empty if valid
     */
    public static List<String> getValidationErrors(String password) {
        List<String> errors = new ArrayList<>();
        
        if (password == null) {
            errors.add("Password cannot be null");
            return errors;
        }
        
        // Length validation
        if (password.length() < MIN_LENGTH) {
            errors.add(String.format("Password must be at least %d characters long", MIN_LENGTH));
        }
        
        if (password.length() > MAX_LENGTH) {
            errors.add(String.format("Password cannot exceed %d characters", MAX_LENGTH));
        }
        
        // Character diversity validation
        if (countMatches(password, UPPERCASE_PATTERN) < MIN_UPPERCASE) {
            errors.add(String.format("Password must contain at least %d uppercase letter(s)", MIN_UPPERCASE));
        }
        
        if (countMatches(password, LOWERCASE_PATTERN) < MIN_LOWERCASE) {
            errors.add(String.format("Password must contain at least %d lowercase letter(s)", MIN_LOWERCASE));
        }
        
        if (countMatches(password, DIGIT_PATTERN) < MIN_DIGITS) {
            errors.add(String.format("Password must contain at least %d digit(s)", MIN_DIGITS));
        }
        
        if (countMatches(password, SPECIAL_CHAR_PATTERN) < MIN_SPECIAL_CHARS) {
            errors.add(String.format("Password must contain at least %d special character(s)", MIN_SPECIAL_CHARS));
        }
        
        // Common password validation
        if (isCommonPassword(password)) {
            errors.add("Password is too common and easily guessable");
        }
        
        // Sequential pattern validation
        if (containsSequentialPattern(password)) {
            errors.add("Password contains sequential characters (e.g., 123, abc)");
        }
        
        // Repetitive character validation
        if (hasRepetitivePattern(password)) {
            errors.add("Password contains too many repetitive characters");
        }
        
        return errors;
    }
    
    /**
     * Calculates password strength score (0-100).
     * 
     * @param password the password to score
     * @return strength score from 0 (weakest) to 100 (strongest)
     */
    public static int calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }
        
        int score = 0;
        
        // Length scoring (0-25 points)
        if (password.length() >= 8) score += 10;
        if (password.length() >= 12) score += 10;
        if (password.length() >= 16) score += 5;
        
        // Character diversity scoring (0-40 points)
        if (UPPERCASE_PATTERN.matcher(password).find()) score += 10;
        if (LOWERCASE_PATTERN.matcher(password).find()) score += 10;
        if (DIGIT_PATTERN.matcher(password).find()) score += 10;
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) score += 10;
        
        // Complexity bonus (0-35 points)
        int uniqueChars = (int) password.chars().distinct().count();
        score += Math.min(15, uniqueChars * 2);
        
        if (!isCommonPassword(password)) score += 10;
        if (!containsSequentialPattern(password)) score += 5;
        if (!hasRepetitivePattern(password)) score += 5;
        
        return Math.min(100, score);
    }
    
    /**
     * Gets password strength description.
     * 
     * @param password the password to evaluate
     * @return human-readable strength description
     */
    public static String getPasswordStrengthDescription(String password) {
        int strength = calculatePasswordStrength(password);
        
        if (strength < 30) return "Very Weak";
        if (strength < 50) return "Weak";
        if (strength < 70) return "Medium";
        if (strength < 90) return "Strong";
        return "Very Strong";
    }
    
    // Helper methods
    
    private static int countMatches(String text, Pattern pattern) {
        return (int) pattern.matcher(text).results().count();
    }
    
    private static boolean isCommonPassword(String password) {
        String lowerPassword = password.toLowerCase();
        for (String common : COMMON_PASSWORDS) {
            if (lowerPassword.contains(common)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean containsSequentialPattern(String password) {
        String lowerPassword = password.toLowerCase();
        for (String pattern : SEQUENTIAL_PATTERNS) {
            if (lowerPassword.contains(pattern)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean hasRepetitivePattern(String password) {
        if (password.length() < 3) return false;
        
        // Check for 3+ consecutive identical characters
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) && 
                password.charAt(i) == password.charAt(i + 2)) {
                return true;
            }
        }
        
        return false;
    }
}
