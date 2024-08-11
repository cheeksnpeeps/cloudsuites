package com.cloudsuites.framework.services.user.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "admin")
public class Admin {

    private static final Logger logger = LoggerFactory.getLogger(Admin.class);

    @Id
    @Column(name = "admin_id", unique = true, nullable = false)
    private String adminId;

    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Identity identity;

    @Enumerated(EnumType.STRING)
    private AdminStatus status;

    @Enumerated(EnumType.STRING)
    private AdminRole role;

    public List<GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority staffAuthority = new SimpleGrantedAuthority(UserType.STAFF.name());
        SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(role.name());
        return List.of(staffAuthority, roleAuthority);
    }

    public UserRole getUserRole() {
        UserRole userRole = new UserRole();
        userRole.setIdentityId(this.getIdentity().getUserId());
        userRole.setPersonaId(this.adminId);
        userRole.setUserType(UserType.ADMIN);
        userRole.setRole(Objects.requireNonNullElse(role, AdminRole.USER).name());
        return userRole;
    }

    @PrePersist
    public void onCreate() {
        this.adminId = IdGenerator.generateULID("ADM-");
        logger.debug("Generated adminId: {}", this.adminId);
    }

    public void updateAdmin(Admin admin) {
    }
}
