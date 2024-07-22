package com.cloudsuites.framework.services.property.personas.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Company;
import com.cloudsuites.framework.services.user.entities.Identity;
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "building_id")
    private Building building;

    @Column(name = "staff_role")
    private StaffRole staffRole;

    public List<GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(UserType.STAFF.name()));
    }

    @PrePersist
    public void onCreate() {
        this.staffId = IdGenerator.generateULID("ST-");
        logger.debug("Generated staffId: {}", this.staffId);
    }
}
