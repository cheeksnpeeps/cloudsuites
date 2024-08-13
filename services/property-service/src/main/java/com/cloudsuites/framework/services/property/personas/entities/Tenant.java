package com.cloudsuites.framework.services.property.personas.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Lease;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.services.user.entities.UserRole;
import com.cloudsuites.framework.services.user.entities.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "tenant")
public class Tenant {

    private static final Logger logger = LoggerFactory.getLogger(Tenant.class);

    @Id
    @Column(name = "tenant_id", unique = true, nullable = false)
    private String tenantId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Identity identity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "building_id")
    private Building building;

    @Column(name = "is_owner")
    private Boolean isOwner;

    @Column(name = "is_primary_tenant")
    private Boolean isPrimaryTenant;

    @Enumerated(EnumType.STRING)
    private TenantStatus status;

    @Enumerated(EnumType.STRING)
    private TenantRole role;

    @OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "lease_id")
    private Lease lease;

    public List<GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(UserType.TENANT.name()));
    }

    public UserRole getUserRole() {
        UserRole userRole = new UserRole();
        userRole.setIdentityId(this.getIdentity().getUserId());
        userRole.setPersonaId(this.tenantId);
        userRole.setUserType(UserType.TENANT);
        userRole.setRole(Objects.requireNonNullElse(role, TenantRole.DEFAULT).name());
        return userRole;
    }

    public Tenant() {
        this.isOwner = false; // Default value
        this.isPrimaryTenant = false;
    }

    @PrePersist
    public void onCreate() {
        this.tenantId = IdGenerator.generateULID("TN-");
        logger.debug("Generated tenantId: {}", this.tenantId);
    }

    public void updateTenant(Tenant tenant) {
        if (!this.isPrimaryTenant.equals(tenant.isPrimaryTenant)) {
            logger.debug("Updating primary tenant status from {} to {}", this.isPrimaryTenant, tenant.isPrimaryTenant);
            this.isPrimaryTenant = tenant.isPrimaryTenant;
        }
        if (!this.isOwner.equals(tenant.isOwner)) {
            logger.debug("Updating owner status from {} to {}", this.isOwner, tenant.isOwner);
            this.isOwner = tenant.isOwner;
        }
    }
}
