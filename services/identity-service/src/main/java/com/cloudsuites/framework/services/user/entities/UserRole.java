package com.cloudsuites.framework.services.user.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity representing a user role in the system.
 * Used to define the role a user has within a specific building or context.
 */
@Entity
@Table(name = "user_roles", indexes = {
    @Index(name = "idx_user_role_user_id", columnList = "user_id"),
    @Index(name = "idx_user_role_user_building", columnList = "user_id, building_id"),
    @Index(name = "idx_user_role_active", columnList = "is_active")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    /**
     * Unique identifier for this user role assignment.
     */
    @Id
    @Column(name = "role_id", length = 36)
    private String roleId;

    /**
     * The ID of the user this role is assigned to.
     */
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    /**
     * The type of user (e.g., ADMIN, STAFF, TENANT, OWNER).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;

    /**
     * The building ID where this role applies (null for global roles).
     */
    @Column(name = "building_id", length = 36)
    private String buildingId;

    /**
     * Whether this role assignment is currently active.
     */
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Additional role-specific data.
     */
    @Column(name = "role_data", columnDefinition = "TEXT")
    private String roleData;

    /**
     * Persona ID for role-based access.
     */
    @Column(name = "persona_id", length = 36)
    private String personaId;

    /**
     * Role name/type.
     */
    @Column(name = "role", length = 50)
    private String role;

    /**
     * Sets the user ID for this role assignment.
     * 
     * @param userId the user ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Sets the persona ID for this role assignment.
     * 
     * @param personaId the persona ID
     */
    public void setPersonaId(String personaId) {
        this.personaId = personaId;
    }

    /**
     * Sets the user type for this role assignment.
     * 
     * @param userType the user type
     */
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    /**
     * Sets the role name for this role assignment.
     * 
     * @param role the role name
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Sets the identity ID for this role assignment (legacy method).
     * This is an alias for setUserId to maintain compatibility.
     * 
     * @param identityId the identity/user ID
     */
    public void setIdentityId(String identityId) {
        this.userId = identityId;
    }

    /**
     * Gets the identity ID for this role assignment (legacy method).
     * This is an alias for getUserId to maintain compatibility.
     * 
     * @return the identity/user ID
     */
    public String getIdentityId() {
        return this.userId;
    }
}
