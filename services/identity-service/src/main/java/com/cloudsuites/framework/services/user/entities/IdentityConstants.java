package com.cloudsuites.framework.services.user.entities;

/**
 * Constants used throughout the identity service layer.
 */
public final class IdentityConstants {

    private IdentityConstants() {
        // Utility class - prevent instantiation
    }

    /**
     * Default session duration in hours.
     */
    public static final int DEFAULT_SESSION_DURATION_HOURS = 24;

    /**
     * Maximum number of active sessions per user.
     */
    public static final int MAX_ACTIVE_SESSIONS_PER_USER = 5;

    /**
     * Default device trust duration in days.
     */
    public static final int DEFAULT_DEVICE_TRUST_DAYS = 30;

    /**
     * Maximum failed login attempts before account lockout.
     */
    public static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    /**
     * Default password reset token validity in hours.
     */
    public static final int PASSWORD_RESET_TOKEN_VALIDITY_HOURS = 2;

    /**
     * Default email verification token validity in hours.
     */
    public static final int EMAIL_VERIFICATION_TOKEN_VALIDITY_HOURS = 24;

    /**
     * Constants related to admin operations.
     */
    public static final class Admin {
        public static final String LOG_ADMIN_NOT_FOUND = "Admin not found with ID: {}";
        public static final String LOG_CREATING_ADMIN = "Creating admin: {}";
        public static final String LOG_ADMIN_CREATED = "Admin created successfully: {}";
        public static final String LOG_UPDATING_ADMIN = "Updating admin with ID: {}";
        public static final String LOG_ADMIN_UPDATED = "Admin updated successfully with ID: {}";
        public static final String LOG_DELETING_ADMIN = "Deleting admin with ID: {}";
        public static final String LOG_ROLE_DELETED = "Admin role deleted for ID: {}";
        public static final String LOG_ADMIN_DELETED = "Admin deleted successfully with ID: {}";
        public static final String LOG_ADMIN_NOT_FOUND_EMAIL = "Admin not found with email: {}";
        public static final String LOG_ADMIN_NOT_FOUND_NAME = "Admin not found with first name: {}";
        public static final String LOG_ADMIN_NOT_FOUND_USER_ID = "Admin not found with user ID: {}";
        
        private Admin() {
            // Utility class - prevent instantiation
        }
    }
}
