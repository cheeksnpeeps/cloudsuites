package com.cloudsuites.framework.services.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for verifying OTP codes.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public class OtpVerificationRequest {

    @NotBlank(message = "Recipient is required")
    @Size(max = 100, message = "Recipient must not exceed 100 characters")
    private String recipient;

    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP code must be exactly 6 digits")
    private String otpCode;

    @NotBlank(message = "Purpose is required")
    @Size(max = 50, message = "Purpose must not exceed 50 characters")
    private String purpose;

    private String userAgent;
    private String ipAddress;
    private String sessionId;

    /**
     * Default constructor for framework use.
     */
    public OtpVerificationRequest() {}

    /**
     * Constructor for creating OTP verification request.
     * 
     * @param recipient phone number or email address
     * @param otpCode 6-digit OTP code
     * @param purpose purpose that matches the original OTP request
     */
    public OtpVerificationRequest(String recipient, String otpCode, String purpose) {
        this.recipient = recipient;
        this.otpCode = otpCode;
        this.purpose = purpose;
    }

    /**
     * Gets the recipient (phone number or email).
     * 
     * @return recipient identifier
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Sets the recipient.
     * 
     * @param recipient phone number or email
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Gets the OTP code to verify.
     * 
     * @return 6-digit OTP code
     */
    public String getOtpCode() {
        return otpCode;
    }

    /**
     * Sets the OTP code.
     * 
     * @param otpCode 6-digit verification code
     */
    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    /**
     * Gets the purpose of the OTP verification.
     * 
     * @return purpose (must match original request)
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the purpose of the OTP verification.
     * 
     * @param purpose use case for the OTP
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * Gets the user agent from the request.
     * 
     * @return user agent string
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Sets the user agent for security tracking.
     * 
     * @param userAgent browser/client user agent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Gets the IP address of the request.
     * 
     * @return client IP address
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the IP address for security tracking.
     * 
     * @param ipAddress client IP address
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Gets the session ID.
     * 
     * @return session identifier
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID for tracking.
     * 
     * @param sessionId session identifier
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "OtpVerificationRequest{" +
                "recipient='" + maskRecipient(recipient) + '\'' +
                ", otpCode='******'" +
                ", purpose='" + purpose + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }

    private String maskRecipient(String recipient) {
        if (recipient == null || recipient.length() <= 4) {
            return "****";
        }
        if (recipient.contains("@")) {
            // Email masking: first 2 chars + *** + domain
            int atIndex = recipient.indexOf("@");
            return recipient.substring(0, Math.min(2, atIndex)) + "***" + recipient.substring(atIndex);
        } else {
            // Phone masking: +1***-***-last4
            return "+***-***-" + recipient.substring(recipient.length() - 4);
        }
    }
}
