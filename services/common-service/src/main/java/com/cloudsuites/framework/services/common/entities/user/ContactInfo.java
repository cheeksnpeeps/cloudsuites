package com.cloudsuites.framework.services.common.entities.user;

import com.cloudsuites.framework.services.common.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

}

