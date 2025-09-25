package com.cloudsuites.framework.services.user.entities;

/**
 * Enumeration of user types supported by the CloudSuites platform.
 */
public enum UserType {
    /**
     * System administrator with full access.
     */
    ADMIN,

    /**
     * Staff member with building-specific access.
     */
    STAFF,

    /**
     * Tenant residing in a unit.
     */
    TENANT,

    /**
     * Unit owner.
     */
    OWNER,

    /**
     * Third-party service provider.
     */
    THIRD_PARTY,

    /**
     * Guest user with limited access.
     */
    GUEST
}
