package com.cloudsuites.framework.services.user.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing an admin user in the system.
 * Contains administrative user information and access levels.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
@Entity
@Table(name = "admins")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    /**
     * Unique identifier for the admin.
     */
    @Id
    @Column(name = "admin_id", length = 36)
    private String adminId;

    /**
     * Admin email address (unique).
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Admin's first name.
     */
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    /**
     * Admin's last name.
     */
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /**
     * Admin's phone number.
     */
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * Whether the admin is active.
     */
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    /**
     * When this admin was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * When this admin was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Who created this admin record.
     */
    @Column(name = "created_by", length = 36)
    private String createdBy;

    /**
     * Who last modified this admin record.
     */
    @Column(name = "last_modified_by", length = 36)
    private String lastModifiedBy;

    /**
     * Admin role (relationship with AdminRole).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 50)
    private AdminRole role;

    /**
     * Admin status.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private AdminStatus status = AdminStatus.ACTIVE;

    /**
     * Associated user role.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_role_id")
    private UserRole userRole;

    /**
     * Associated identity.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "identity_id")
    private Identity identity;

    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return isActive != null && isActive;
    }

    /**
     * Updates this admin with data from another admin instance.
     */
    public void updateAdmin(Admin other) {
        if (other.getFirstName() != null) {
            this.firstName = other.getFirstName();
        }
        if (other.getLastName() != null) {
            this.lastName = other.getLastName();
        }
        if (other.getEmail() != null) {
            this.email = other.getEmail();
        }
        if (other.getPhoneNumber() != null) {
            this.phoneNumber = other.getPhoneNumber();
        }
        if (other.getIsActive() != null) {
            this.isActive = other.getIsActive();
        }
        if (other.getRole() != null) {
            this.role = other.getRole();
        }
        if (other.getStatus() != null) {
            this.status = other.getStatus();
        }
    }
}
