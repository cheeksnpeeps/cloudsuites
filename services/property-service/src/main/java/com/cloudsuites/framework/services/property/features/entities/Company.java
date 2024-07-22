package com.cloudsuites.framework.services.property.features.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.cloudsuites.framework.services.user.entities.Address;
import com.cloudsuites.framework.services.user.entities.Identity;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "management_company")
public class Company {

    private static final Logger logger = LoggerFactory.getLogger(Company.class);

    @Id
    @Column(name = "management_company_id", unique = true, nullable = false)
    private String companyId;

    @Column(name = "name")
    private String name;

    @Column(name = "website")
    private String website;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @JoinColumn(name = "created_by")
    @OneToOne(cascade = CascadeType.ALL)
    private Identity createdBy;

    @JoinColumn(name = "last_modified_by")
    @OneToOne(cascade = CascadeType.ALL)
    private Identity lastModifiedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.companyId == null) {
            this.companyId = IdGenerator.generateULID("MC-");
            logger.debug("Generated companyId: {}", this.companyId);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedAt = LocalDateTime.now();
    }

}
