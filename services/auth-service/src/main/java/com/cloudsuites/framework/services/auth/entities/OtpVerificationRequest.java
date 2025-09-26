package com.cloudsuites.framework.services.auth.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request for verifying an OTP.
 * Contains the OTP code and related verification information.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class OtpVerificationRequest {

    /**
     * User identifier (email, phone, or user ID).
     */
    @NotBlank(message = "User identifier is required")
    private String userIdentifier;

    /**
     * Recipient of the OTP (email, phone, etc).
     */
    private String recipient;

    /**
     * OTP code to verify.
     */
    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP code must be 6 digits")
    private String otpCode;

    /**
     * Purpose or context of the OTP verification.
     */
    @Size(max = 100, message = "Purpose cannot exceed 100 characters")
    private String purpose;

    /**
     * IP address of the requester.
     */
    private String ipAddress;

    /**
     * User agent of the requester.
     */
    private String userAgent;

    // Constructors
    public OtpVerificationRequest() {}

    public OtpVerificationRequest(String userIdentifier, String otpCode) {
        this.userIdentifier = userIdentifier;
        this.otpCode = otpCode;
    }

    // Getters and Setters
    public String getUserIdentifier() { return userIdentifier; }
    public void setUserIdentifier(String userIdentifier) { this.userIdentifier = userIdentifier; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
}
