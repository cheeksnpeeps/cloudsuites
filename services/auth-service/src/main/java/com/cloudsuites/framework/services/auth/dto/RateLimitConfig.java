package com.cloudsuites.framework.services.auth.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;

/**
 * Configuration for rate limiting operations.
 * Defines limits, windows, and lockout behavior for specific operations.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
public class RateLimitConfig {

    @NotBlank
    private final String operation;

    @Min(1)
    private final int limit;

    @NotNull
    private final Duration window;

    private final boolean enableLockout;

    @Min(0)
    private final int lockoutThreshold;

    @NotNull
    private final Duration lockoutDuration;

    private final boolean exponentialBackoff;

    private final Duration maxLockoutDuration;

    private final String description;

    /**
     * Basic constructor for simple rate limiting.
     */
    public RateLimitConfig(String operation, int limit, Duration window) {
        this(operation, limit, window, false, 0, Duration.ZERO, false,
             Duration.ofDays(1), operation + " rate limiting");
    }

    /**
     * Constructor with lockout configuration.
     */
    public RateLimitConfig(String operation, int limit, Duration window,
                          boolean enableLockout, int lockoutThreshold,
                          Duration lockoutDuration) {
        this(operation, limit, window, enableLockout, lockoutThreshold,
             lockoutDuration, false, Duration.ofDays(1),
             operation + " rate limiting with lockout");
    }

    /**
     * Full constructor with all options.
     */
    public RateLimitConfig(String operation, int limit, Duration window,
                          boolean enableLockout, int lockoutThreshold,
                          Duration lockoutDuration, boolean exponentialBackoff,
                          Duration maxLockoutDuration, String description) {
        this.operation = operation;
        this.limit = limit;
        this.window = window;
        this.enableLockout = enableLockout;
        this.lockoutThreshold = lockoutThreshold;
        this.lockoutDuration = lockoutDuration;
        this.exponentialBackoff = exponentialBackoff;
        this.maxLockoutDuration = maxLockoutDuration;
        this.description = description;
    }

    // Getters
    public String getOperation() {
        return operation;
    }

    public int getLimit() {
        return limit;
    }

    public Duration getWindow() {
        return window;
    }

    public boolean isEnableLockout() {
        return enableLockout;
    }

    public int getLockoutThreshold() {
        return lockoutThreshold;
    }

    public Duration getLockoutDuration() {
        return lockoutDuration;
    }

    public boolean isExponentialBackoff() {
        return exponentialBackoff;
    }

    public Duration getMaxLockoutDuration() {
        return maxLockoutDuration;
    }

    public String getDescription() {
        return description;
    }

    // Static factory methods for common configurations

    /**
     * Creates configuration for login rate limiting.
     * 5 attempts per 15 minutes with account lockout.
     */
    public static RateLimitConfig loginConfig() {
        return new RateLimitConfig(
            "login",
            5,
            Duration.ofMinutes(15),
            true,
            5,
            Duration.ofMinutes(15),
            true,
            Duration.ofHours(24),
            "Login attempt rate limiting with exponential lockout"
        );
    }

    /**
     * Creates configuration for OTP sending.
     * 3 attempts per 5 minutes without lockout.
     */
    public static RateLimitConfig otpSendConfig() {
        return new RateLimitConfig(
            "otp_send",
            3,
            Duration.ofMinutes(5),
            false,
            0,
            Duration.ZERO,
            false,
            Duration.ZERO,
            "OTP sending rate limiting"
        );
    }

    /**
     * Creates configuration for OTP verification.
     * 3 attempts per OTP code with temporary lockout.
     */
    public static RateLimitConfig otpVerifyConfig() {
        return new RateLimitConfig(
            "otp_verify",
            3,
            Duration.ofMinutes(5),
            true,
            3,
            Duration.ofMinutes(5),
            false,
            Duration.ofMinutes(30),
            "OTP verification rate limiting"
        );
    }

    /**
     * Creates configuration for password reset requests.
     * 3 attempts per hour with short lockout.
     */
    public static RateLimitConfig passwordResetConfig() {
        return new RateLimitConfig(
            "password_reset",
            3,
            Duration.ofHours(1),
            true,
            3,
            Duration.ofMinutes(30),
            false,
            Duration.ofHours(6),
            "Password reset request rate limiting"
        );
    }

    /**
     * Creates configuration for API access.
     * 100 requests per minute without lockout.
     */
    public static RateLimitConfig apiAccessConfig() {
        return new RateLimitConfig(
            "api_access",
            100,
            Duration.ofMinutes(1),
            false,
            0,
            Duration.ZERO,
            false,
            Duration.ZERO,
            "General API access rate limiting"
        );
    }

    /**
     * Creates configuration for registration attempts.
     * 5 registrations per hour per IP.
     */
    public static RateLimitConfig registrationConfig() {
        return new RateLimitConfig(
            "registration",
            5,
            Duration.ofHours(1),
            true,
            5,
            Duration.ofHours(2),
            false,
            Duration.ofHours(24),
            "Account registration rate limiting"
        );
    }

    @Override
    public String toString() {
        return String.format("RateLimitConfig{operation='%s', limit=%d, window=%s, lockout=%s}",
                           operation, limit, window, enableLockout);
    }
}
