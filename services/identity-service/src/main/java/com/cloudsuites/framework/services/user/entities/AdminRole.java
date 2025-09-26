package com.cloudsuites.framework.services.user.entities;

/**
 * Enumeration for admin roles in the system.
 * Defines different levels of administrative access.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public enum AdminRole {
    /**
     * Super admin with full system access.
     */
    SUPER_ADMIN("Super Administrator"),

    /**
     * Business admin with business-level access.
     */
    BUSINESS_ADMIN("Business Administrator"),

    /**
     * Buildings admin with building management access.
     */
    BUILDINGS_ADMIN("Buildings Administrator"),

    /**
     * Third party admin with limited access.
     */
    THIRD_PARTY_ADMIN("Third Party Administrator"),

    /**
     * General admin with standard access.
     */
    ALL_ADMIN("Administrator"),

    /**
     * Property manager with property-specific access.
     */
    PROPERTY_MANAGER("Property Manager"),

    /**
     * Building supervisor with building-specific access.
     */
    BUILDING_SUPERVISOR("Building Supervisor"),

    /**
     * Building security with security-specific access.
     */
    BUILDING_SECURITY("Building Security"),

    /**
     * Accounting and finance manager.
     */
    ACCOUNTING_FINANCE_MANAGER("Accounting & Finance Manager"),

    /**
     * Leasing agent with tenant management access.
     */
    LEASING_AGENT("Leasing Agent"),

    /**
     * Customer service representative.
     */
    CUSTOMER_SERVICE_REPRESENTATIVE("Customer Service Representative"),

    /**
     * Maintenance technician.
     */
    MAINTENANCE_TECHNICIAN("Maintenance Technician"),

    /**
     * Other staff member.
     */
    OTHER("Other Staff"),

    /**
     * General staff member.
     */
    ALL_STAFF("Staff Member"),

    /**
     * General user role (legacy compatibility).
     */
    USER("User"),

    /**
     * Deleted admin role (soft delete marker).
     */
    DELETED("Deleted");

    private final String displayName;

    AdminRole(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the user-friendly display name for this admin role.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this is a super admin role.
     */
    public boolean isSuperAdmin() {
        return this == SUPER_ADMIN;
    }

    /**
     * Checks if this is a business admin role.
     */
    public boolean isBusinessAdmin() {
        return this == BUSINESS_ADMIN;
    }

    /**
     * Checks if this is a building-level admin role.
     */
    public boolean isBuildingAdmin() {
        return this == BUILDINGS_ADMIN || this == BUILDING_SUPERVISOR || this == BUILDING_SECURITY;
    }

    /**
     * Checks if this is a property management role.
     */
    public boolean isPropertyManagement() {
        return this == PROPERTY_MANAGER || this == BUILDING_SUPERVISOR;
    }

    /**
     * Checks if this is a staff-level role.
     */
    public boolean isStaff() {
        return this == ALL_STAFF || this == OTHER || this == MAINTENANCE_TECHNICIAN || 
               this == CUSTOMER_SERVICE_REPRESENTATIVE || this == LEASING_AGENT;
    }
}
