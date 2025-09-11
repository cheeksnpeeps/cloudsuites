package com.cloudsuites.framework.services.user.entities;

/**
 * Enumeration for authentication event types.
 * Matches the event_type check constraint in the database (V4 migration).
 */
public enum AuthEventType {
    // Authentication events
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    LOGOUT,
    
    // OTP events
    OTP_REQUEST,
    OTP_VERIFY_SUCCESS,
    OTP_VERIFY_FAILURE,
    
    // Password management
    PASSWORD_RESET_REQUEST,
    PASSWORD_RESET_SUCCESS,
    PASSWORD_CHANGE,
    
    // Account management
    ACCOUNT_LOCKED,
    ACCOUNT_UNLOCKED,
    
    // Multi-factor authentication
    MFA_ENABLED,
    MFA_DISABLED,
    
    // Device management
    DEVICE_TRUSTED,
    DEVICE_UNTRUSTED,
    
    // Token management
    TOKEN_REFRESH,
    TOKEN_REVOKED,
    SESSION_EXPIRED,
    
    // Security events
    SUSPICIOUS_ACTIVITY,
    ADMIN_IMPERSONATION,
    PERMISSION_DENIED,
    RATE_LIMIT_EXCEEDED,
    SECURITY_POLICY_VIOLATION;

    /**
     * Determines if this event type represents a security concern.
     */
    public boolean isSecurityEvent() {
        return switch (this) {
            case LOGIN_FAILURE, ACCOUNT_LOCKED, SUSPICIOUS_ACTIVITY, 
                 PERMISSION_DENIED, RATE_LIMIT_EXCEEDED, SECURITY_POLICY_VIOLATION,
                 OTP_VERIFY_FAILURE -> true;
            default -> false;
        };
    }

    /**
     * Determines if this event type represents a successful operation.
     */
    public boolean isSuccessEvent() {
        return switch (this) {
            case LOGIN_SUCCESS, OTP_VERIFY_SUCCESS, PASSWORD_RESET_SUCCESS,
                 PASSWORD_CHANGE, ACCOUNT_UNLOCKED, MFA_ENABLED, MFA_DISABLED,
                 DEVICE_TRUSTED, DEVICE_UNTRUSTED, TOKEN_REFRESH -> true;
            default -> false;
        };
    }

    /**
     * Gets the default risk level for this event type.
     */
    public RiskLevel getDefaultRiskLevel() {
        return switch (this) {
            case SUSPICIOUS_ACTIVITY, SECURITY_POLICY_VIOLATION -> RiskLevel.HIGH;
            case LOGIN_FAILURE, ACCOUNT_LOCKED, PERMISSION_DENIED, 
                 RATE_LIMIT_EXCEEDED, OTP_VERIFY_FAILURE -> RiskLevel.MEDIUM;
            default -> RiskLevel.LOW;
        };
    }
}
