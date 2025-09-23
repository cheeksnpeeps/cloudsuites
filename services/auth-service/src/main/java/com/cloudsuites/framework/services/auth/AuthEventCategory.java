package com.cloudsuites.framework.services.auth;

/**
 * Enumeration for authentication event categories.
 * Groups related authentication events for better organization and reporting.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public enum AuthEventCategory {
    AUTHENTICATION("Authentication and authorization events"),
    SECURITY("Security-related events"),
    ACCOUNT_MANAGEMENT("Account lifecycle events"),
    SESSION_MANAGEMENT("Session lifecycle events"),
    PASSWORD_MANAGEMENT("Password-related events"),
    OTP_MANAGEMENT("One-time password events"),
    MULTI_FACTOR_AUTH("Multi-factor authentication events"),
    TOKEN_MANAGEMENT("JWT token lifecycle events"),
    PERMISSION_MANAGEMENT("Permission and role events"),
    AUDIT("Audit and compliance events");

    private final String description;

    AuthEventCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get the category for a specific event type.
     * 
     * @param eventType the authentication event type
     * @return the corresponding category
     */
    public static AuthEventCategory fromEventType(AuthEventType eventType) {
        return switch (eventType) {
            case LOGIN_SUCCESS, LOGIN_FAILURE, LOGOUT -> AUTHENTICATION;
            case OTP_REQUEST, OTP_VERIFY_SUCCESS, OTP_VERIFY_FAILURE -> OTP_MANAGEMENT;
            case PASSWORD_RESET_REQUEST, PASSWORD_RESET_SUCCESS, PASSWORD_CHANGE -> PASSWORD_MANAGEMENT;
            case ACCOUNT_LOCKED, ACCOUNT_UNLOCKED -> ACCOUNT_MANAGEMENT;
            case MFA_ENABLED, MFA_DISABLED -> MULTI_FACTOR_AUTH;
            case SESSION_CREATED, SESSION_EXPIRED, SESSION_TERMINATED -> SESSION_MANAGEMENT;
            case TOKEN_ISSUED, TOKEN_REFRESHED, TOKEN_REVOKED -> TOKEN_MANAGEMENT;
            case BRUTE_FORCE_DETECTED, SUSPICIOUS_ACTIVITY, IP_BLACKLISTED -> SECURITY;
            case PERMISSION_GRANTED, PERMISSION_REVOKED, ROLE_ASSIGNED, ROLE_REMOVED -> PERMISSION_MANAGEMENT;
        };
    }
}
