package com.cloudsuites.framework.services.property.personas.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.services.user.entities.UserRole;
import com.cloudsuites.framework.services.user.entities.UserType;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@Table(name = "owner")
public class Owner {

    private static final Logger logger = LoggerFactory.getLogger(Owner.class);

    @Id
    @Column(name = "owner_id", unique = true, nullable = false)
    private String ownerId;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Unit> units;

    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Identity identity;

    @Column(name = "is_primary_tenant")
    private Boolean isPrimaryTenant;

    @Enumerated(EnumType.STRING)
    private OwnerStatus status;

    public Owner() {
        this.isPrimaryTenant = false;
        this.status = OwnerStatus.INACTIVE;// Default value
    }

    public List<GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(UserType.OWNER.name()));
    }

    public UserRole getUserRole() {
        UserRole userRole = new UserRole();
        userRole.setIdentityId(this.getIdentity().getUserId());
        userRole.setPersonaId(this.ownerId);
        userRole.setUserType(UserType.OWNER);
        userRole.setRole(UserType.OWNER.name());
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

}
