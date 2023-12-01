package com.cloudsuites.framework.services.entities.property;

import com.cloudsuites.framework.services.common.entities.Address;
import com.cloudsuites.framework.services.common.entities.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "managementCompany", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Building> buildings = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @JoinColumn(name = "created_by")
    @OneToOne(cascade = CascadeType.ALL)
    private User createdBy;

    @JoinColumn(name = "last_modified_by")
    @OneToOne(cascade = CascadeType.ALL)
    private User lastModifiedBy;

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
