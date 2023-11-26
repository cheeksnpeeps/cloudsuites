package com.cloudsuites.framework.services.common.entities.property;

import jakarta.persistence.*;
import lombok.Data;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_info_id")
    private ContactInfo contactInfo;

    // Other attributes and relationships can be added based on your requirements

    // Constructors, getters, and setters

    // Additional methods if needed
}
