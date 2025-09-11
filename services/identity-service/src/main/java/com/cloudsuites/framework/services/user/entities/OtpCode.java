package com.cloudsuites.framework.services.user.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * OTP Code entity for one-time password verification.
 * Maps to the otp_codes table created in V2 migration.
 * Supports SMS and EMAIL delivery with rate limiting and security tracking.
 */
@Data
@Entity
@Table(name = "otp_codes", indexes = {
    @Index(name = "idx_otp_user_id", columnList = "user_id"),
    @Index(name = "idx_otp_code_active", columnList = "otp_code, is_used, expires_at"),
    @Index(name = "idx_otp_cleanup", columnList = "expires_at")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpCode {

    private static final Logger logger = LoggerFactory.getLogger(OtpCode.class);

    @Id
    @Column(name = "otp_id", unique = true, nullable = false)
    private String otpId;

    @NotNull(message = "User ID is mandatory")
    @Size(max = 255, message = "User ID must not exceed 255 characters")
    @Column(name = "user_id", nullable = false)
    private String userId;

    @NotNull(message = "OTP code is mandatory")
    @Pattern(regexp = "^[0-9]{4,10}$", message = "OTP code must be 4-10 digits")
    @JsonIgnore // Never expose OTP code in JSON responses
    @Column(name = "otp_code", nullable = false, length = 10)
    private String otpCode;

    @NotNull(message = "Delivery method is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method", nullable = false)
    private OtpDeliveryMethod deliveryMethod;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Email(message = "Invalid email format")
    @Size(max = 320, message = "Email must not exceed 320 characters")
    @Column(name = "email_address", length = 320)
    private String emailAddress;

    @Builder.Default
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime createdAt;

    @NotNull(message = "Expiration time is mandatory")
    @Column(name = "expires_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime expiresAt;

    @Column(name = "verified_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime verifiedAt;

    @Builder.Default
    @Min(value = 0, message = "Attempts count cannot be negative")
    @Column(name = "attempts_count", nullable = false)
    private Integer attemptsCount = 0;

    @Builder.Default
    @Min(value = 1, message = "Max attempts must be at least 1")
    @Max(value = 10, message = "Max attempts cannot exceed 10")
    @Column(name = "max_attempts", nullable = false)
    private Integer maxAttempts = 3;

    @Column(name = "ip_address")
    private String ipAddress;

    @Size(max = 1000, message = "User agent must not exceed 1000 characters")
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    // ============================================================================
    // LIFECYCLE CALLBACKS
    // ============================================================================

    @PrePersist
    protected void onCreate() {
        this.otpId = IdGenerator.generateULID("OTP-");
        this.createdAt = LocalDateTime.now();
        
        // Validate delivery contact information
        validateDeliveryContact();
        
        // Ensure expiration is after creation
        if (this.expiresAt != null && this.expiresAt.isBefore(this.createdAt)) {
            throw new IllegalArgumentException("Expiration time must be after creation time");
        }
        
        logger.debug("Created OTP code: {} for user: {} via {}", this.otpId, this.userId, this.deliveryMethod);
    }

    // ============================================================================
    // BUSINESS METHODS
    // ============================================================================

    /**
     * Checks if the OTP code is still valid (not expired, not used, attempts remaining).
     */
    public boolean isValid() {
        return !isUsed 
            && expiresAt.isAfter(LocalDateTime.now())
            && attemptsCount < maxAttempts;
    }

    /**
     * Checks if the OTP code has expired.
     */
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    /**
     * Checks if maximum attempts have been reached.
     */
    public boolean hasExceededMaxAttempts() {
        return attemptsCount >= maxAttempts;
    }

    /**
     * Increments the attempts count for this OTP code.
     */
    public void incrementAttempts() {
        this.attemptsCount++;
        logger.debug("Incremented attempts for OTP {}: {}/{}", this.otpId, this.attemptsCount, this.maxAttempts);
    }

    /**
     * Marks the OTP code as used and sets verification timestamp.
     */
    public void markAsUsed() {
        this.isUsed = true;
        this.verifiedAt = LocalDateTime.now();
        logger.debug("Marked OTP {} as used at {}", this.otpId, this.verifiedAt);
    }

    /**
     * Gets the delivery contact (phone number or email) based on delivery method.
     */
    public String getDeliveryContact() {
        return switch (deliveryMethod) {
            case SMS -> phoneNumber;
            case EMAIL -> emailAddress;
        };
    }

    /**
     * Validates that the appropriate contact information is provided for the delivery method.
     */
    private void validateDeliveryContact() {
        switch (deliveryMethod) {
            case SMS -> {
                if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                    throw new IllegalArgumentException("Phone number is required for SMS delivery");
                }
            }
            case EMAIL -> {
                if (emailAddress == null || emailAddress.trim().isEmpty()) {
                    throw new IllegalArgumentException("Email address is required for EMAIL delivery");
                }
            }
        }
    }

    /**
     * Creates a builder with default values for common OTP scenarios.
     */
    public static OtpCodeBuilder smsOtp(String userId, String phoneNumber, String otpCode, int validityMinutes) {
        return OtpCode.builder()
            .userId(userId)
            .phoneNumber(phoneNumber)
            .otpCode(otpCode)
            .deliveryMethod(OtpDeliveryMethod.SMS)
            .expiresAt(LocalDateTime.now().plusMinutes(validityMinutes));
    }

    /**
     * Creates a builder with default values for email OTP scenarios.
     */
    public static OtpCodeBuilder emailOtp(String userId, String emailAddress, String otpCode, int validityMinutes) {
        return OtpCode.builder()
            .userId(userId)
            .emailAddress(emailAddress)
            .otpCode(otpCode)
            .deliveryMethod(OtpDeliveryMethod.EMAIL)
            .expiresAt(LocalDateTime.now().plusMinutes(validityMinutes));
    }
}
