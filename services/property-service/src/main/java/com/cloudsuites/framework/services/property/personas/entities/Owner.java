package com.cloudsuites.framework.services.property.personas.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.services.user.entities.UserRole;
import com.cloudsuites.framework.services.user.entities.UserType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "owner")
public class Owner {

    private static final Logger logger = LoggerFactory.getLogger(Owner.class);

    @Id
    @Column(name = "owner_id", unique = true, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String ownerId;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Unit> units;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Identity identity;

    @Column(name = "is_primary_tenant")
    @ToString.Include
    private Boolean isPrimaryTenant;

    @Enumerated(EnumType.STRING)
    @ToString.Include
    private OwnerStatus status;

    @Enumerated(EnumType.STRING)
    @ToString.Include
    private OwnerRole role;

    public Owner() {
        this.isPrimaryTenant = false;
        this.status = OwnerStatus.INACTIVE;// Default value
    }

    public List<GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(UserType.OWNER.name()));
    }

    public UserRole getUserRole() {
        UserRole userRole = new UserRole();
        userRole.setIdentityId(this.identity.getUserId());
        userRole.setPersonaId(this.ownerId);
        userRole.setUserType(UserType.OWNER);
        userRole.setRole(Objects.requireNonNullElse(role, OwnerRole.DEFAULT).name());
        return userRole;
    }

    @PrePersist
    protected void onCreate() {
        this.ownerId = IdGenerator.generateULID("OW-");
        logger.debug("Generated ownerId: {}", this.ownerId);
    }

    public void addUnit(Unit unit) {
        if (this.units == null) {
            logger.debug("Initializing units list");
            this.units = new ArrayList<>();
        }
        this.units.add(unit);
        unit.setOwner(this);
    }

	public void updateOwner(Owner owner) {
		if (this.isPrimaryTenant != owner.isPrimaryTenant) {
			logger.debug("Updating isPrimaryTenant from {} to {}", this.isPrimaryTenant, owner.isPrimaryTenant);
			this.isPrimaryTenant = owner.isPrimaryTenant;
		}
	}
}