package com.cloudsuites.framework.services.user.entities;

/**
 * Enumeration for OTP delivery methods.
 * Matches the otp_delivery_method enum in the database (V2 migration).
 */
public enum OtpDeliveryMethod {
    /**
     * SMS delivery to phone number
     */
    SMS,
    
    /**
     * Email delivery to email address
     */
    EMAIL
}
