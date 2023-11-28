package com.cloudsuites.framework.services.common.entities.property;

import com.cloudsuites.framework.services.common.entities.Address;
import com.cloudsuites.framework.services.common.entities.user.ContactInfo;
import com.cloudsuites.framework.services.common.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "property_management_company")
public class PropertyManagementCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "name")
    private String name;

    @Column(name = "website")
    private String website;

    @OneToMany(mappedBy = "propertyManagementCompany", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Building> buildings;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_info_id")
    private ContactInfo contactInfo;

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
