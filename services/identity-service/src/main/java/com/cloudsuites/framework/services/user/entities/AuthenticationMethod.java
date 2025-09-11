package com.cloudsuites.framework.services.user.entities;

/**
 * Enumeration for authentication methods.
 * Matches the authentication_method check constraint in the database (V4 migration).
 */
public enum AuthenticationMethod {
    /**
     * OTP sent via SMS
     */
    OTP_SMS("OTP via SMS"),
    
    /**
     * OTP sent via email
     */
    OTP_EMAIL("OTP via Email"),
    
    /**
     * Traditional password authentication
     */
    PASSWORD("Password"),
    
    /**
     * Biometric authentication (fingerprint, face recognition)
     */
    BIOMETRIC("Biometric"),
    
    /**
     * Single Sign-On authentication
     */
    SSO("Single Sign-On"),
    
    /**
     * Refresh token authentication
     */
    REFRESH_TOKEN("Refresh Token"),
    
    /**
     * Multi-factor authentication (combination of methods)
     */
    MFA("Multi-Factor Authentication");

    private final String displayName;

    AuthenticationMethod(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the user-friendly display name for this authentication method.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Determines if this method is considered a strong authentication method.
     */
    public boolean isStrongAuth() {
        return this == BIOMETRIC || this == MFA || this == OTP_SMS || this == OTP_EMAIL;
    }

    /**
     * Determines if this method requires a second factor.
     */
    public boolean requiresSecondFactor() {
        return this == PASSWORD && !isStrongAuth();
    }

    /**
     * Gets the security score for this authentication method (0-100).
     */
    public int getSecurityScore() {
        return switch (this) {
            case MFA -> 95;
            case BIOMETRIC -> 90;
            case OTP_SMS, OTP_EMAIL -> 80;
            case SSO -> 70;
            case REFRESH_TOKEN -> 60;
            case PASSWORD -> 40;
        };
    }

    /**
     * Determines if this method is OTP-based.
     */
    public boolean isOtpBased() {
        return this == OTP_SMS || this == OTP_EMAIL;
    }
}
