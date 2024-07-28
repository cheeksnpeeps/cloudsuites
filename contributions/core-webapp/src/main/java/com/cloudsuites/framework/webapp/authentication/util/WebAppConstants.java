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
}
