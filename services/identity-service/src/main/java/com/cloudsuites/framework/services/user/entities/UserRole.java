package com.cloudsuites.framework.services.user.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"identity_id", "persona_id", "user_type", "role"})
})
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Surrogate primary key
    @Column(name = "user_role_id")
    private Long userRoleId; // Surrogate key for uniqueness

    @Column(name = "identity_id", nullable = false)
    private String identityId; // Reference to the Identity table

    @Column(name = "persona_id", nullable = false)
    private String personaId; // The ID of the persona

    @Enumerated(EnumType.STRING) // Using Enum for user types
    @Column(name = "user_type", nullable = false)
    private UserType userType; // Enum for user types (e.g., TENANT, OWNER, etc.)

    @Column(name = "role", nullable = false)
    private String role; // e.g., "ROLE_USER", "ROLE_ADMIN", etc.
}
