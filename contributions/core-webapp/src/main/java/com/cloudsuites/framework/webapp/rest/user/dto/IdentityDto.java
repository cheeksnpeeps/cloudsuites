package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.services.user.entities.Gender;
import com.cloudsuites.framework.services.user.entities.UserType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class IdentityDto {

    private Long userId;

    private String username;

    private Gender gender;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private UserType userType;

//    private IdentityDto createdBy;

//    private IdentityDto lastModifiedBy;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;
}
