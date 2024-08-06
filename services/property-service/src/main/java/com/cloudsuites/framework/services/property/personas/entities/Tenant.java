package com.cloudsuites.framework.services.property.personas.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.cloudsuites.framework.services.property.features.entities.Building;
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

    public List<GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(UserType.TENANT.name()));
    }

    public UserRole getUserRole() {
        UserRole userRole = new UserRole();
        userRole.setIdentityId(this.getIdentity().getUserId());
        userRole.setPersonaId(this.tenantId);
        userRole.setUserType(UserType.TENANT);
        userRole.setRole(UserType.TENANT.name());
        return userRole;
    }

    public Tenant() {
        this.isOwner = false; // Default value
        this.isPrimaryTenant = false;
        this.status = TenantStatus.INACTIVE;// Default value
    }

    @PrePersist
    public void onCreate() {
        this.tenantId = IdGenerator.generateULID("TN-");
        logger.debug("Generated tenantId: {}", this.tenantId);
    }
}
