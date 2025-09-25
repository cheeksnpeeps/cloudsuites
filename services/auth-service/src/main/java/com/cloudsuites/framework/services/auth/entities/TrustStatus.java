package com.cloudsuites.framework.services.auth.entities;

/**
 * Enumeration for device trust status.
 * Represents the current trust level of a device in the system.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public enum TrustStatus {
    /**
     * Device trust is pending approval or verification.
     * Default state when a device is first registered.
     */
    PENDING("Pending Verification"),

    /**
     * Device is trusted and can be used for extended authentication.
     * Allows "keep me logged in" functionality.
     */
    TRUSTED("Trusted"),

    /**
     * Device trust has been revoked due to security concerns.
     * Device cannot be used for extended authentication.
     */
    REVOKED("Revoked"),

    /**
     * Device trust has expired and needs renewal.
     * May be automatically renewed on next successful authentication.
     */
    EXPIRED("Expired"),

    /**
     * Device is temporarily suspended for security review.
     * Can be reactivated after manual review.
     */
    SUSPENDED("Suspended");

    private final String displayName;

    TrustStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the user-friendly display name for this trust status.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this status allows extended authentication sessions.
     */
    public boolean isActiveTrust() {
        return this == TRUSTED;
    }

    /**
     * Checks if this status requires user intervention.
     */
    public boolean requiresAction() {
        return this == PENDING || this == EXPIRED || this == SUSPENDED;
    }

    /**
     * Checks if this status represents a security concern.
     */
    public boolean isSecurityConcern() {
        return this == REVOKED || this == SUSPENDED;
    }
}
