package com.cloudsuites.framework.webapp.authentication.util;

public final class WebAppConstants {

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
