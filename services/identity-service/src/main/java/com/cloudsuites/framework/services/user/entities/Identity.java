package com.cloudsuites.framework.services.user.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing user identity in the system.
 * Contains core user information and authentication details.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
@Entity
@Table(name = "identities", indexes = {
    @Index(name = "idx_identity_email", columnList = "email", unique = true),
    @Index(name = "idx_identity_username", columnList = "username", unique = true),
    @Index(name = "idx_identity_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Identity {

    /**
     * Unique identifier for the identity.
     */
    @Id
    @Column(name = "identity_id", length = 36)
    private String identityId;

    /**
     * Unique username for login.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * User's email address (unique).
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * User's first name.
     */
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    /**
     * User's last name.
     */
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /**
     * User's phone number.
     */
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * User's gender.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    /**
     * Hashed password.
     */
    @NotBlank(message = "Password is required")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Whether email has been verified.
     */
    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    /**
     * Whether phone has been verified.
     */
    @Column(name = "phone_verified")
    @Builder.Default
    private Boolean phoneVerified = false;

    /**
     * Whether MFA is enabled.
     */
    @Column(name = "mfa_enabled")
    @Builder.Default
    private Boolean mfaEnabled = false;

    /**
     * Account status.
     */
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private IdentityStatus status = IdentityStatus.PENDING;

    /**
     * When this identity was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * When this identity was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Last login timestamp.
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * Who created this identity record.
     */
    @Column(name = "created_by", length = 36)
    private String createdBy;

    /**
     * Who last modified this identity record.
     */
    @Column(name = "last_modified_by", length = 36)
    private String lastModifiedBy;

    /**
     * Associated user ID for this identity.
     */
    @Column(name = "user_id", length = 36)
    private String userId;

    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return status == IdentityStatus.ACTIVE;
    }

    public boolean isVerified() {
        return emailVerified != null && emailVerified;
    }

    public void markEmailVerified() {
        this.emailVerified = true;
        if (this.status == IdentityStatus.PENDING) {
            this.status = IdentityStatus.ACTIVE;
        }
    }

    public void markPhoneVerified() {
        this.phoneVerified = true;
    }

    /**
     * Updates this identity with data from another identity instance.
     */
    public void updateIdentity(Identity other) {
        if (other.getUsername() != null) {
            this.username = other.getUsername();
        }
        if (other.getEmail() != null) {
            this.email = other.getEmail();
        }
        if (other.getFirstName() != null) {
            this.firstName = other.getFirstName();
        }
        if (other.getLastName() != null) {
            this.lastName = other.getLastName();
        }
        if (other.getPhoneNumber() != null) {
            this.phoneNumber = other.getPhoneNumber();
        }
        if (other.getStatus() != null) {
            this.status = other.getStatus();
        }
    }

    /**
     * Enumeration for identity status.
     */
    public enum IdentityStatus {
        PENDING("Pending Verification"),
        ACTIVE("Active"),
        SUSPENDED("Suspended"),
        ARCHIVED("Archived");

        private final String displayName;

        IdentityStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isActive() {
            return this == ACTIVE;
        }
    }
}
