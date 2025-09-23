package com.cloudsuites.framework.services.auth;

/**
 * Enumeration for authentication event types.
 * Tracks all authentication-related activities for audit purposes.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public enum AuthEventType {
    // Authentication events
    LOGIN_SUCCESS("User successfully authenticated"),
    LOGIN_FAILURE("Authentication attempt failed"),
    LOGOUT("User logged out"),
    
    // OTP events
    OTP_REQUEST("OTP code requested"),
    OTP_VERIFY_SUCCESS("OTP verification successful"),
    OTP_VERIFY_FAILURE("OTP verification failed"),
    
    // Password management
    PASSWORD_RESET_REQUEST("Password reset requested"),
    PASSWORD_RESET_SUCCESS("Password reset completed"),
    PASSWORD_CHANGE("Password changed"),
    
    // Account management
    ACCOUNT_LOCKED("Account locked due to security"),
    ACCOUNT_UNLOCKED("Account unlocked"),
    
    // Multi-factor authentication
    MFA_ENABLED("Multi-factor authentication enabled"),
    MFA_DISABLED("Multi-factor authentication disabled"),
    
    // Session management
    SESSION_CREATED("New session created"),
    SESSION_EXPIRED("Session expired"),
    SESSION_TERMINATED("Session terminated"),
    
    // Token management
    TOKEN_ISSUED("JWT token issued"),
    TOKEN_REFRESHED("JWT token refreshed"),
    TOKEN_REVOKED("JWT token revoked"),
    
    // Security events
    BRUTE_FORCE_DETECTED("Brute force attack detected"),
    SUSPICIOUS_ACTIVITY("Suspicious activity detected"),
    IP_BLACKLISTED("IP address blacklisted"),
    
    // Administrative events
    PERMISSION_GRANTED("Permission granted"),
    PERMISSION_REVOKED("Permission revoked"),
    ROLE_ASSIGNED("Role assigned to user"),
    ROLE_REMOVED("Role removed from user");

    private final String description;

    AuthEventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
