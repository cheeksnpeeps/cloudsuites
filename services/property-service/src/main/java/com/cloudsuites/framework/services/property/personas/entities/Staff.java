package com.cloudsuites.framework.services.property.personas.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Company;
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

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "staff")
public class Staff {

    private static final Logger logger = LoggerFactory.getLogger(Staff.class);

    @Id
    @Column(name = "staff_id", unique = true, nullable = false)
    private String staffId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Identity identity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "building_id")
    private Building building;

    @Enumerated(EnumType.STRING)
    private StaffStatus status;

    @Enumerated(EnumType.STRING)
    private StaffRole role;

    public List<GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority staffAuthority = new SimpleGrantedAuthority(UserType.STAFF.name());
        SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(role.name());
        return List.of(staffAuthority, roleAuthority);
    }

    public UserRole getUserRole() {
        UserRole userRole = new UserRole();
        userRole.setIdentityId(this.getIdentity().getUserId());
        userRole.setPersonaId(this.staffId);
        userRole.setUserType(UserType.STAFF);
        userRole.setRole(Objects.requireNonNullElse(role, StaffRole.DEFAULT).name());
        return userRole;
    }

    @PrePersist
    public void onCreate() {
        this.staffId = IdGenerator.generateULID("ST-");
        logger.debug("Generated staffId: {}", this.staffId);
    }

    public void updateStaff(Staff staff) {
    }
}
