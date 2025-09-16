package com.cloudsuites.framework.services.auth.dto;

import com.cloudsuites.framework.services.auth.OtpChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for sending OTP codes.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public class OtpRequest {

    @NotBlank(message = "Recipient is required")
    @Size(max = 100, message = "Recipient must not exceed 100 characters")
    private String recipient;

    @NotNull(message = "OTP channel is required")
    private OtpChannel channel;

    @NotBlank(message = "Purpose is required")
    @Size(max = 50, message = "Purpose must not exceed 50 characters")
    private String purpose;

    private String userAgent;
    private String ipAddress;
    private String sessionId;

    /**
     * Default constructor for framework use.
     */
    public OtpRequest() {}

    /**
     * Constructor for creating OTP request.
     * 
     * @param recipient phone number or email address
     * @param channel SMS or EMAIL
     * @param purpose purpose of OTP (login, registration, password_reset, etc.)
     */
    public OtpRequest(String recipient, OtpChannel channel, String purpose) {
        this.recipient = recipient;
        this.channel = channel;
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
     * Gets the delivery channel.
     * 
     * @return OTP delivery channel
     */
    public OtpChannel getChannel() {
        return channel;
    }

    /**
     * Sets the delivery channel.
     * 
     * @param channel SMS or EMAIL
     */
    public void setChannel(OtpChannel channel) {
        this.channel = channel;
    }

    /**
     * Gets the purpose of the OTP.
     * 
     * @return purpose (login, registration, password_reset, etc.)
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the purpose of the OTP.
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
        return "OtpRequest{" +
                "recipient='" + maskRecipient(recipient) + '\'' +
                ", channel=" + channel +
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
