package com.cloudsuites.framework.services.property.entities;

import com.cloudsuites.framework.services.user.entities.Address;
import com.cloudsuites.framework.services.user.entities.Identity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "management_company")
public class ManagementCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "management_company_id")
    private Long managementCompanyId;

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
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedAt = LocalDateTime.now();
    }

}
