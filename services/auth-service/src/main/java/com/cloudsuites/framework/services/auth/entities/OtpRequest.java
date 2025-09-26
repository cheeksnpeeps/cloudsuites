package com.cloudsuites.framework.services.auth.entities;

import com.cloudsuites.framework.services.auth.OtpChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request for generating an OTP.
 * Contains the information needed to send an OTP to a user.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class OtpRequest {

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
     * Channel to send the OTP through.
     */
    @NotNull(message = "OTP channel is required")
    private OtpChannel channel;

    /**
     * Purpose or context of the OTP.
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
    public OtpRequest() {}

    public OtpRequest(String userIdentifier, OtpChannel channel) {
        this.userIdentifier = userIdentifier;
        this.channel = channel;
    }

    // Getters and Setters
    public String getUserIdentifier() { return userIdentifier; }
    public void setUserIdentifier(String userIdentifier) { this.userIdentifier = userIdentifier; }

    public OtpChannel getChannel() { return channel; }
    public void setChannel(OtpChannel channel) { this.channel = channel; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
}
