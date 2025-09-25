package com.cloudsuites.framework.services.auth;

import com.cloudsuites.framework.services.auth.entities.PasswordChangeRequest;
import com.cloudsuites.framework.services.auth.entities.PasswordResetRequest;

/**
 * Service interface for password management operations.
 * 
 * Provides secure password hashing, validation, reset functionality,
 * and breach checking capabilities.
 */
public interface PasswordService {
    
    /**
     * Hashes a plain text password using BCrypt.
     * 
     * @param plainTextPassword the plain text password to hash
     * @return BCrypt hashed password
     * @throws IllegalArgumentException if password is null or empty
     */
    String hashPassword(String plainTextPassword);
    
    /**
     * Verifies a plain text password against a hashed password.
     * 
     * @param plainTextPassword the plain text password to verify
     * @param hashedPassword the hashed password to verify against
     * @return true if passwords match, false otherwise
     */
    boolean verifyPassword(String plainTextPassword, String hashedPassword);
    
    /**
     * Validates password complexity according to security policies.
     * 
     * @param password the password to validate
     * @return true if password meets complexity requirements
     */
    boolean validatePasswordComplexity(String password);
    
    /**
     * Gets detailed password validation errors.
     * 
     * @param password the password to validate
     * @return list of validation error messages, empty if valid
     */
    java.util.List<String> getPasswordValidationErrors(String password);
    
    /**
     * Generates a secure password reset token.
     * 
     * @param userId the user ID for whom to generate the token
     * @return secure password reset token
     */
    String generatePasswordResetToken(String userId);
    
    /**
     * Validates a password reset token.
     * 
     * @param token the reset token to validate
     * @param userId the user ID to validate against
     * @return true if token is valid and not expired
     */
    boolean validatePasswordResetToken(String token, String userId);
    
    /**
     * Changes a user's password with proper validation.
     * 
     * @param request the password change request
     * @return true if password was successfully changed
     * @throws SecurityException if old password doesn't match or new password is invalid
     */
    boolean changePassword(PasswordChangeRequest request);
    
    /**
     * Initiates password reset process.
     * 
     * @param request the password reset request
     * @return true if reset was initiated successfully
     */
    boolean initiatePasswordReset(PasswordResetRequest request);
    
    /**
     * Checks if password has been compromised in known breaches.
     * 
     * @param password the password to check
     * @return true if password appears in breach databases
     */
    boolean isPasswordBreached(String password);
    
    /**
     * Generates a strong random password.
     * 
     * @param length the desired password length (minimum 8)
     * @return randomly generated strong password
     */
    String generateStrongPassword(int length);
}
