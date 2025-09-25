package com.cloudsuites.framework.services.auth;

import com.cloudsuites.framework.services.auth.entities.OtpRequest;
import com.cloudsuites.framework.services.auth.entities.OtpVerificationRequest;
import com.cloudsuites.framework.services.auth.entities.OtpResponse;
import com.cloudsuites.framework.services.auth.entities.OtpStatistics;

/**
 * Multi-channel OTP (One-Time Password) service interface.
 * Supports SMS and Email delivery channels with rate limiting and security features.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public interface OtpService {

    /**
     * Sends an OTP code to the specified recipient via the requested channel.
     * Implements rate limiting (3 attempts per 5 minutes per recipient).
     * 
     * @param request OTP request containing recipient, channel, and context
     * @return OtpResponse with success status, message, and tracking info
     * @throws SecurityException if rate limit exceeded
     * @throws ValidationException if request validation fails
     */
    OtpResponse sendOtp(OtpRequest request);

    /**
     * Verifies an OTP code for a specific recipient and purpose.
     * OTP codes expire after 5 minutes and are single-use.
     * 
     * @param request verification request with code, recipient, and context
     * @return true if OTP is valid and not expired, false otherwise
     * @throws SecurityException if verification rate limit exceeded
     */
    boolean verifyOtp(OtpVerificationRequest request);

    /**
     * Resends the last OTP to the same recipient if within resend window.
     * Limited to 2 resends per OTP session.
     * 
     * @param recipient phone number or email address
     * @return OtpResponse with resend status
     * @throws SecurityException if resend limit exceeded
     */
    OtpResponse resendOtp(String recipient);

    /**
     * Invalidates all active OTP codes for a specific recipient.
     * Used for security purposes (e.g., account lockout, suspicious activity).
     * 
     * @param recipient phone number or email address
     * @return number of OTP codes invalidated
     */
    int invalidateOtpCodes(String recipient);

    /**
     * Checks if a recipient has exceeded the rate limit for OTP requests.
     * Rate limit: 3 OTP requests per 5 minutes per recipient.
     * 
     * @param recipient phone number or email address
     * @return true if rate limit exceeded, false otherwise
     */
    boolean isRateLimited(String recipient);

    /**
     * Gets the remaining time before a recipient can request another OTP.
     * 
     * @param recipient phone number or email address
     * @return seconds until next OTP can be requested, 0 if no limit
     */
    long getRateLimitResetTime(String recipient);

    /**
     * Validates if the recipient format is correct for the specified channel.
     * 
     * @param recipient phone number (E.164 format) or email address
     * @param channel SMS or EMAIL
     * @return true if format is valid for the channel
     */
    boolean isValidRecipient(String recipient, OtpChannel channel);

    /**
     * Gets OTP statistics for monitoring and analytics.
     * 
     * @param recipient optional recipient filter
     * @return OTP usage statistics
     */
    OtpStatistics getOtpStatistics(String recipient);
}
