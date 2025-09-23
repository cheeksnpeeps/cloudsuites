package com.cloudsuites.framework.services.auth;

/**
 * Enumeration for risk levels in authentication events.
 * Used for security monitoring and automated response systems.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public enum RiskLevel {
    LOW("Normal operation", 1),
    MEDIUM("Slightly elevated risk", 2),
    HIGH("Elevated risk requiring attention", 3),
    CRITICAL("Critical risk requiring immediate action", 4);

    private final String description;
    private final int severity;

    RiskLevel(String description, int severity) {
        this.description = description;
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public int getSeverity() {
        return severity;
    }

    /**
     * Determine risk level based on event type.
     * 
     * @param eventType the authentication event type
     * @return the corresponding risk level
     */
    public static RiskLevel fromEventType(AuthEventType eventType) {
        return switch (eventType) {
            case LOGIN_SUCCESS, LOGOUT, OTP_REQUEST, OTP_VERIFY_SUCCESS,
                 PASSWORD_CHANGE, SESSION_CREATED, TOKEN_ISSUED, TOKEN_REFRESHED -> LOW;
            
            case LOGIN_FAILURE, OTP_VERIFY_FAILURE, PASSWORD_RESET_REQUEST,
                 SESSION_EXPIRED, SESSION_TERMINATED, TOKEN_REVOKED -> MEDIUM;
            
            case ACCOUNT_LOCKED, SUSPICIOUS_ACTIVITY, PERMISSION_GRANTED,
                 PERMISSION_REVOKED, ROLE_ASSIGNED, ROLE_REMOVED -> HIGH;
            
            case BRUTE_FORCE_DETECTED, IP_BLACKLISTED, ACCOUNT_UNLOCKED,
                 PASSWORD_RESET_SUCCESS, MFA_ENABLED, MFA_DISABLED -> CRITICAL;
        };
    }
}
