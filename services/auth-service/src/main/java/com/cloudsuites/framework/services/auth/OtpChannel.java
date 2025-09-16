package com.cloudsuites.framework.services.auth;

/**
 * Enumeration of supported OTP delivery channels.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public enum OtpChannel {
    
    /**
     * SMS delivery via Twilio service.
     * Requires phone number in E.164 format (+1234567890).
     */
    SMS("sms", "SMS Text Message"),
    
    /**
     * Email delivery via SMTP service.
     * Requires valid email address format.
     */
    EMAIL("email", "Email Message");

    private final String code;
    private final String displayName;

    OtpChannel(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * Gets the channel code for API usage.
     * 
     * @return channel code (sms, email)
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the human-readable display name.
     * 
     * @return display name for UI
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets OtpChannel from code string.
     * 
     * @param code channel code
     * @return OtpChannel enum value
     * @throws IllegalArgumentException if code is invalid
     */
    public static OtpChannel fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("OTP channel code cannot be null");
        }
        
        for (OtpChannel channel : values()) {
            if (channel.code.equalsIgnoreCase(code)) {
                return channel;
            }
        }
        
        throw new IllegalArgumentException("Invalid OTP channel code: " + code);
    }

    /**
     * Checks if the channel supports the given recipient format.
     * 
     * @param recipient phone number or email
     * @return true if recipient format matches channel requirements
     */
    public boolean isValidRecipient(String recipient) {
        if (recipient == null || recipient.trim().isEmpty()) {
            return false;
        }
        
        return switch (this) {
            case SMS -> isValidPhoneNumber(recipient);
            case EMAIL -> isValidEmailAddress(recipient);
        };
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // E.164 format: +[country code][number] (max 15 digits)
        return phoneNumber.matches("^\\+[1-9]\\d{1,14}$");
    }

    private boolean isValidEmailAddress(String email) {
        // Basic email validation - more comprehensive validation in service layer
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
