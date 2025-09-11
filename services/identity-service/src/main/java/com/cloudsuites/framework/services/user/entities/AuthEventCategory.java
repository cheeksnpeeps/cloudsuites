package com.cloudsuites.framework.services.user.entities;

/**
 * Enumeration for authentication event categories.
 * Matches the event_category check constraint in the database (V4 migration).
 */
public enum AuthEventCategory {
    /**
     * Authentication-related events (login, logout, OTP verification)
     */
    AUTHENTICATION("Authentication Events"),
    
    /**
     * Authorization-related events (permission checks, access control)
     */
    AUTHORIZATION("Authorization Events"),
    
    /**
     * Account management events (password changes, account locks)
     */
    ACCOUNT_MANAGEMENT("Account Management"),
    
    /**
     * Security-related events (suspicious activity, violations)
     */
    SECURITY("Security Events"),
    
    /**
     * Administrative events (admin actions, system changes)
     */
    ADMINISTRATION("Administrative Events");

    private final String displayName;

    AuthEventCategory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the user-friendly display name for this category.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Determines if this category represents security-sensitive events.
     */
    public boolean isSecuritySensitive() {
        return this == SECURITY || this == AUTHORIZATION || this == ADMINISTRATION;
    }

    /**
     * Gets the default risk level for events in this category.
     */
    public RiskLevel getDefaultRiskLevel() {
        return switch (this) {
            case SECURITY -> RiskLevel.HIGH;
            case AUTHORIZATION, ADMINISTRATION -> RiskLevel.MEDIUM;
            case AUTHENTICATION, ACCOUNT_MANAGEMENT -> RiskLevel.LOW;
        };
    }
}
