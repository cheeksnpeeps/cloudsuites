package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.services.user.entities.Gender;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonView({Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
public class IdentityDto {

    private Long userId;

    private String username;

    private Gender gender;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

//    private IdentityDto createdBy;

//    private IdentityDto lastModifiedBy;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;
}
