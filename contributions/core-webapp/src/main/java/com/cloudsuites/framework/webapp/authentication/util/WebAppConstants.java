package com.cloudsuites.framework.webapp.authentication.util;

public final class WebAppConstants {

    public static class Admin {
        public static final String LOG_FETCHING_ADMINS = "Fetching all admins";
        public static final String LOG_FETCHING_ADMIN_BY_ID = "Fetching admin with ID: {}";
        public static final String LOG_ADMIN_FETCHED = "Admin fetched with ID: {}";
        public static final String LOG_ADMIN_NOT_FOUND = "Admin not found with ID: {}";
        public static final String LOG_UPDATING_ADMIN = "Updating admin with ID: {}";
        public static final String LOG_ADMIN_UPDATED = "Admin updated successfully with ID: {}";
        public static final String LOG_DELETING_ADMIN = "Deleting admin with ID: {}";
        public static final String LOG_ADMIN_DELETED = "Admin deleted successfully with ID: {}";
        public static final String LOG_UNIT_REMOVED_SUCCESSFULLY = "Unit successfully removed from admin with ID: {}";
        public static final String LOG_REGISTERING_ADMIN = "Registering admin with username: {}";
        public static final String LOG_ADMIN_REGISTERED_SUCCESS = "Admin registered successfully with ID: {} and username: {}";
        public static final String LOG_REMOVING_UNIT_FROM_ADMIN = "Removing unit from admin with ID: {}";

        private Admin() {
        }
    }

    public static class Building {
        public static final String LOG_FETCHING_BUILDINGS = "Fetching all buildings";
        public static final String LOG_FETCHING_BUILDING_BY_ID = "Fetching building with ID: {}";
        public static final String LOG_BUILDING_FETCHED = "Building fetched with ID: {}";
        public static final String LOG_BUILDING_NOT_FOUND = "Building not found with ID: {}";
        public static final String LOG_UPDATING_BUILDING = "Updating building with ID: {}";
        public static final String LOG_BUILDING_UPDATED = "Building updated successfully with ID: {}";
        public static final String LOG_DELETING_BUILDING = "Deleting building with ID: {}";
        public static final String LOG_BUILDING_DELETED = "Building deleted successfully with ID: {}";
        public static final String LOG_REGISTERING_BUILDING = "Registering building with name: {}";
        public static final String LOG_BUILDING_REGISTERED_SUCCESS = "Building registered successfully with ID: {} and name: {}";
        public static final String LOG_FETCHING_BUILDING_UNITS = "Fetching all units for building with ID: {}";
        public static final String LOG_FETCHING_BUILDING_UNIT_BY_ID = "Fetching unit with ID: {} for building with ID: {}";
        public static final String LOG_BUILDING_UNIT_FETCHED = "Unit fetched with ID: {} for building with ID: {}";
        public static final String LOG_BUILDING_UNIT_NOT_FOUND = "Unit not found with ID: {} for building with ID: {}";
        public static final String LOG_UPDATING_BUILDING_UNIT = "Updating unit with ID: {} for building with ID: {}";
        public static final String LOG_BUILDING_UNIT_UPDATED = "Unit updated successfully with ID: {} for building with ID: {}";
        public static final String LOG_DELETING_BUILDING_UNIT = "Deleting unit with ID: {} for building with ID: {}";
        public static final String LOG_BUILDING_UNIT_DELETED = "Unit deleted successfully with ID: {} for building with ID: {}";
        public static final String LOG_REGISTERING_BUILDING_UNIT = "Registering unit with name: {} for building with ID: {}";
        public static final String LOG_BUILDING_UNIT_REGISTERED_SUCCESS = "Unit registered successfully with ID: {} and name: {} for building with ID: {}";
        public static final String LOG_UNIT_ALREADY_EXISTS = "Unit already exists for building with ID: {}";
        public static final String LOG_UNIT_REMOVED_SUCCESS = "Unit removed successfully from building with ID: {}";
        private Building() {
        }
    }

    public static class Unit {
        public static final String LOG_FETCHING_UNITS = "Fetching all units";
        public static final String LOG_FETCHING_UNIT_BY_ID = "Fetching unit with ID: {}";
        public static final String LOG_UNIT_FETCHED = "Unit fetched with ID: {}";
        public static final String LOG_UNIT_NOT_FOUND = "Unit not found with ID: {}";
        public static final String LOG_UPDATING_UNIT = "Updating unit with ID: {}";
        public static final String LOG_UNIT_UPDATED = "Unit updated successfully with ID: {}";
        public static final String LOG_DELETING_UNIT = "Deleting unit with ID: {}";
        public static final String LOG_UNIT_DELETED = "Unit deleted successfully with ID: {}";
        public static final String LOG_REGISTERING_UNIT = "Registering unit with name: {}";
        public static final String LOG_UNIT_REGISTERED_SUCCESS = "Unit registered successfully with ID: {} and name: {}";
        public static final String LOG_FETCHING_UNIT_TENANTS = "Fetching all tenants for unit with ID: {}";
        public static final String LOG_FETCHING_UNIT_TENANT_BY_ID = "Fetching tenant with ID: {} for unit with ID: {}";
        public static final String LOG_UNIT_TENANT_FETCHED = "Tenant fetched with ID: {} for unit with ID: {}";
        public static final String LOG_UNIT_TENANT_NOT_FOUND = "Tenant not found with ID: {} for unit with ID: {}";
        public static final String LOG_UPDATING_UNIT_TENANT = "Updating tenant with ID: {} for unit with ID: {}";
        public static final String LOG_UNIT_TENANT_UPDATED = "Tenant updated successfully with ID: {} for unit with ID: {}";
        public static final String LOG_DELETING_UNIT_TENANT = "Deleting tenant with ID: {} for unit with ID: {}";
        public static final String LOG_UNIT_TENANT_DELETED = "Tenant deleted successfully with ID: {} for unit with ID: {}";
        public static final String LOG_REGISTERING_UNIT_TENANT = "Registering tenant with phone number: {} for unit with ID: {}";
        public static final String LOG_UNIT_TENANT_REGISTERED_SUCCESS = "Tenant registered successfully with ID: {} and phone number: {} for unit with ID: {}";
        public static final String LOG_UNIT_TENANT_ALREADY_EXISTS = "Tenant already exists for unit with ID: {}";
        public static final String LOG_UNIT_TENANT_REMOVED_SUCCESS = "Tenant removed successfully from unit with ID: {}";
        private Unit() {
        }
    }

    public static class Tenant {
        public static final String LOG_REGISTERING_TENANT = "Registering tenant with phone number: {}";
        public static final String LOG_TENANT_REGISTERED_SUCCESS = "Tenant registered successfully with ID: {} and phone number: {}";
        public static final String LOG_FETCHING_TENANTS_BY_BUILDING = "Fetching all tenants for building with ID: {}";
        public static final String LOG_FETCHING_TENANTS_BY_UNIT = "Fetching all tenants for unit with ID: {}";
        public static final String LOG_UPDATING_TENANT = "Updating tenant with ID: {}";
        public static final String LOG_FOUND_TENANTS_BY_BUILDING = "Found tenants for building with ID: {}";

        private Tenant() {
        }
    }

    private WebAppConstants() {
    }

    // Owner Authentication Constants
    public static class Auth {
        public static final String REGISTERING_OWNER_LOG = "Registering owner with phone number: {}";
        public static final String OWNER_REGISTERED_SUCCESS_LOG = "Owner registered successfully with ID: {} and phone number: {}";
        public static final String INVALID_OTP_LOG = "Invalid OTP for phone number: {}";
        public static final String OWNER_NOT_UNIT_OWNER_ERROR = "Owner is not the owner of the unit"; // Keep as ERROR for API response
        private Auth() {
        }
    }

    // OTP Constants
    public static class Otp {
        public static final String OTP_GENERATED_LOG = "Generated OTP for phone number {}: {}";
        public static final String OTP_SENT_LOG = "Sent OTP to phone number: {}";
        public static final String INVALID_OTP_ERROR = "Invalid OTP provided for phone number: {}"; // Keep as ERROR for API response
        public static final String OTP_VERIFIED_LOG = "OTP verified for phone number: {} and owner: {}";
        private Otp() {
        }
    }

    // Token Constants
    public static class Token {
        public static final String INVALID_REFRESH_TOKEN_ERROR = "Invalid refresh token"; // Keep as ERROR for API response
        public static final String TOKEN_REFRESHED_SUCCESS_LOG = "Token refreshed successfully for owner: {}";
        public static final String TOKEN_CLAIMS_MISMATCH_LOG = "Token claims do not match with the request parameters: ownerId={}, buildingId={}, unitId={}"; // Changed to LOG
        public static final String TOKEN_IDENTITY_MISMATCH_LOG = "Token identity mismatch: owner Identity: {} and Token identity: {}";
        private Token() {
        }
    }

    public static class Claim {
        public static final String PERSONA_ID = "personaId";
        public static final String BUILDING_ID = "buildingId";
        public static final String UNIT_ID = "unitId";
        public static final String USER_ID = "userId";
        public static final String AUDIENCE = "CloudSuites";
        public static final String TYPE = "type";

        private Claim() {
        }
    }

    public static class Owner {
        public static final String LOG_FETCHING_OWNERS = "Fetching all owners";
        public static final String LOG_FETCHING_OWNER_BY_ID = "Fetching owner with ID: {}";
        public static final String LOG_OWNER_FETCHED = "Owner fetched with ID: {}";
        public static final String LOG_OWNER_NOT_FOUND = "Owner not found with ID: {}";
        public static final String LOG_UPDATING_OWNER = "Updating owner with ID: {}";
        public static final String LOG_OWNER_UPDATED = "Owner updated successfully with ID: {}";
        public static final String LOG_DELETING_OWNER = "Deleting owner with ID: {}";
        public static final String LOG_OWNER_DELETED = "Owner deleted successfully with ID: {}";
        public static final String LOG_ADDING_UNIT_TO_OWNER = "Adding unit to owner with ownerId={}, buildingId={}, unitId={}";
        public static final String LOG_UNIT_ADDED_TO_OWNER = "Unit successfully added to owner with ID: {}";
        public static final String LOG_UNIT_ALREADY_EXISTS = "Unit already exists for owner with ID: {}";
        public static final String LOG_UNIT_REMOVED_SUCCESSFULLY = "Unit successfully removed from owner with ID: {}";
        public static final String LOG_REMOVING_UNIT_FROM_PREVIOUS_OWNER = "Removing unit from previous owner with ID: {}";
        public static final String LOG_OWNER_OR_UNIT_NOT_FOUND = "Owner or Unit not found: ownerId={}, buildingId={}, unitId={}";
        public static final String LOG_REGISTERING_OWNER = "Registering owner with username: {}";
        public static final String LOG_OWNER_REGISTERED_SUCCESS = "Owner registered successfully with ID: {} and username: {}";
        public static final String LOG_UNIT_ADDED_SUCCESSFULLY = "Unit added successfully to owner with ID: {}";
        public static final String LOG_REMOVING_UNIT_FROM_OWNER = "Removing unit from owner with ID: {}";

        private Owner() {
        }
    }

}
