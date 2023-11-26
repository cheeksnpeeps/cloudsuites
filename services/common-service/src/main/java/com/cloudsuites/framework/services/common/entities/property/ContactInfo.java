package com.cloudsuites.framework.services.common.entities.property;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "contact_info")
public class ContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_info_id")
    private Long contactInfoId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    // Constructors, getters, and setters

    // Additional methods if needed
}

