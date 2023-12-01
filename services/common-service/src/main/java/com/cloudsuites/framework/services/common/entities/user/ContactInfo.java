package com.cloudsuites.framework.services.common.entities.user;

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

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;



}

