package com.cloudsuites.framework.services.user.entities;

/**
 * Enumeration for admin status in the system.
 * Represents the current status of an admin account.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public enum AdminStatus {
    /**
     * Admin account is active and can access the system.
     */
    ACTIVE("Active"),

    /**
     * Admin account is inactive and cannot access the system.
     */
    INACTIVE("Inactive"),

    /**
     * Admin account is suspended due to security or policy violations.
     */
    SUSPENDED("Suspended"),

    /**
     * Admin account is pending activation.
     */
    PENDING("Pending Activation"),

    /**
     * Admin account has been archived (soft deleted).
     */
    ARCHIVED("Archived");

    private final String displayName;

    AdminStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the user-friendly display name for this admin status.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this status allows system access.
     */
    public boolean isAccessAllowed() {
        return this == ACTIVE;
    }

    /**
     * Checks if this status is temporary and may change.
     */
    public boolean isTemporary() {
        return this == PENDING || this == SUSPENDED;
    }

    /**
     * Checks if this status represents an inactive state.
     */
    public boolean isInactive() {
        return this == INACTIVE || this == SUSPENDED || this == ARCHIVED;
    }
}
