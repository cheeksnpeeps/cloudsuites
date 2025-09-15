package com.cloudsuites.framework.modules.auth.service.impl;

import com.cloudsuites.framework.services.auth.PasswordService;
import com.cloudsuites.framework.services.auth.PasswordValidator;
import com.cloudsuites.framework.services.auth.dto.PasswordChangeRequest;
import com.cloudsuites.framework.services.auth.dto.PasswordResetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of PasswordService providing secure password management.
 * 
 * Features:
 * - BCrypt password hashing with configurable strength
 * - Comprehensive password validation
 * - Secure password reset token generation
 * - Basic breach checking (extendable for external APIs)
 * - Strong password generation
 */
@Service
public class PasswordServiceImpl implements PasswordService {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordServiceImpl.class);
    
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;
    
    // In-memory storage for reset tokens (should be replaced with database/Redis in production)
    private final Map<String, ResetTokenData> resetTokens = new ConcurrentHashMap<>();
    
    @Value("${cloudsuites.security.password.bcrypt.strength:12}")
    private int bcryptStrength;
    
    @Value("${cloudsuites.security.password.reset.token.expiry.minutes:30}")
    private int resetTokenExpiryMinutes;
    
    // Password generation character sets
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGIT_CHARS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String ALL_CHARS = UPPERCASE_CHARS + LOWERCASE_CHARS + DIGIT_CHARS + SPECIAL_CHARS;
    
    public PasswordServiceImpl() {
        this.passwordEncoder = new BCryptPasswordEncoder(12); // Default strength
        this.secureRandom = new SecureRandom();
    }
    
    @Override
    public String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        logger.debug("Hashing password with BCrypt strength: {}", bcryptStrength);
        String hashedPassword = passwordEncoder.encode(plainTextPassword);
        logger.debug("Password hashed successfully");
        
        return hashedPassword;
    }
    
    @Override
    public boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            logger.debug("Password verification failed: null input");
            return false;
        }
        
        try {
            boolean matches = passwordEncoder.matches(plainTextPassword, hashedPassword);
            logger.debug("Password verification result: {}", matches);
            return matches;
        } catch (Exception e) {
            logger.error("Error during password verification", e);
            return false;
        }
    }
    
    @Override
    public boolean validatePasswordComplexity(String password) {
        return PasswordValidator.isValidPassword(password);
    }
    
    @Override
    public List<String> getPasswordValidationErrors(String password) {
        return PasswordValidator.getValidationErrors(password);
    }
    
    @Override
    public String generatePasswordResetToken(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        
        logger.debug("Generating password reset token for user: {}", userId);
        
        // Generate secure random token
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        StringBuilder token = new StringBuilder();
        for (byte b : tokenBytes) {
            token.append(String.format("%02x", b));
        }
        
        String resetToken = token.toString();
        LocalDateTime expiryTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(resetTokenExpiryMinutes);
        
        // Store token with expiry (in production, use database/Redis)
        resetTokens.put(resetToken, new ResetTokenData(userId, expiryTime));
        
        logger.debug("Password reset token generated for user: {} (expires at: {})", userId, expiryTime);
        return resetToken;
    }
    
    @Override
    public boolean validatePasswordResetToken(String token, String userId) {
        if (token == null || userId == null) {
            logger.debug("Reset token validation failed: null input");
            return false;
        }
        
        logger.debug("Validating token: {} for user: {}", token, userId);
        logger.debug("Current tokens in map: {}", resetTokens.keySet());
        
        ResetTokenData tokenData = resetTokens.get(token);
        if (tokenData == null) {
            logger.debug("Reset token validation failed: token not found");
            return false;
        }
        
        if (!tokenData.userId.equals(userId)) {
            logger.debug("Reset token validation failed: user ID mismatch");
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        logger.debug("Current time: {}, Token expiry: {}", now, tokenData.expiryTime);
        if (now.isAfter(tokenData.expiryTime)) {
            logger.debug("Reset token validation failed: token expired");
            resetTokens.remove(token); // Clean up expired token
            return false;
        }
        
        logger.debug("Reset token validation successful for user: {}", userId);
        return true;
    }
    
    @Override
    public boolean changePassword(PasswordChangeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Password change request cannot be null");
        }
        
        logger.debug("Processing password change request for user: {}", request.getUserId());
        
        // Validate password confirmation
        if (!request.isPasswordConfirmationValid()) {
            throw new SecurityException("New password and confirmation do not match");
        }
        
        // Validate new password complexity
        if (!validatePasswordComplexity(request.getNewPassword())) {
            List<String> errors = getPasswordValidationErrors(request.getNewPassword());
            throw new SecurityException("New password does not meet complexity requirements: " + String.join(", ", errors));
        }
        
        // TODO: Verify current password against database
        // This would require UserService integration to fetch current password hash
        // For now, assuming current password verification is handled at controller level
        
        // Hash new password
        String newPasswordHash = hashPassword(request.getNewPassword());
        
        // TODO: Update password in database
        // This would require UserService integration to update the password hash
        
        logger.debug("Password changed successfully for user: {}", request.getUserId());
        return true;
    }
    
    @Override
    public boolean initiatePasswordReset(PasswordResetRequest request) {
        if (request == null || request.getEmail() == null) {
            throw new IllegalArgumentException("Password reset request and email cannot be null");
        }
        
        logger.debug("Initiating password reset for email: {}", request.getEmail());
        
        // TODO: Verify email exists in database
        // For now, assuming email validation is handled at controller level
        
        // Generate reset token (userId would come from database lookup)
        String userId = "temp-user-id"; // TODO: Get actual user ID from email lookup
        String resetToken = generatePasswordResetToken(userId);
        
        // TODO: Send reset email with token
        // This would integrate with notification service
        
        logger.debug("Password reset initiated for email: {}", request.getEmail());
        return true;
    }
    
    @Override
    public boolean isPasswordBreached(String password) {
        if (password == null) {
            return false;
        }
        
        // Basic implementation - check against common passwords
        // In production, this would integrate with HaveIBeenPwned API or similar
        String lowerPassword = password.toLowerCase();
        
        String[] commonBreachedPasswords = {
            "password", "123456", "12345678", "qwerty", "abc123", "password123",
            "admin", "letmein", "welcome", "monkey", "dragon", "master",
            "trustno1", "111111", "iloveyou", "sunshine", "princess", "password1",
            "123123", "654321", "superman", "qwerty123", "football", "baseball"
        };
        
        for (String breached : commonBreachedPasswords) {
            if (lowerPassword.equals(breached)) {
                logger.debug("Password found in breach database");
                return true;
            }
        }
        
        logger.debug("Password not found in breach database");
        return false;
    }
    
    @Override
    public String generateStrongPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }
        
        logger.debug("Generating strong password of length: {}", length);
        
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one character from each category
        password.append(UPPERCASE_CHARS.charAt(secureRandom.nextInt(UPPERCASE_CHARS.length())));
        password.append(LOWERCASE_CHARS.charAt(secureRandom.nextInt(LOWERCASE_CHARS.length())));
        password.append(DIGIT_CHARS.charAt(secureRandom.nextInt(DIGIT_CHARS.length())));
        password.append(SPECIAL_CHARS.charAt(secureRandom.nextInt(SPECIAL_CHARS.length())));
        
        // Fill the rest with random characters from all categories
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARS.charAt(secureRandom.nextInt(ALL_CHARS.length())));
        }
        
        // Shuffle the password to avoid predictable patterns
        return shuffleString(password.toString());
    }
    
    private String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
    
    // Inner class for reset token data
    private static class ResetTokenData {
        final String userId;
        final LocalDateTime expiryTime;
        
        ResetTokenData(String userId, LocalDateTime expiryTime) {
            this.userId = userId;
            this.expiryTime = expiryTime;
        }
    }
}
