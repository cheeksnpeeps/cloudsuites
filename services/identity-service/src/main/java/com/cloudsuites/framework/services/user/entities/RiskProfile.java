package com.cloudsuites.framework.services.user.entities;

/**
 * Risk assessment profile for users based on login patterns, behavior, and security settings.
 * Used for adaptive authentication and security monitoring.
 */
public enum RiskProfile {
    /**
     * Low risk - Trusted user with consistent patterns and strong security
     */
    LOW,
    
    /**
     * Normal risk - Standard user with typical usage patterns
     */
    NORMAL,
    
    /**
     * Elevated risk - User showing some unusual patterns or moderate security concerns
     */
    ELEVATED,
    
    /**
     * High risk - User with suspicious activity or significant security concerns
     */
    HIGH
}
