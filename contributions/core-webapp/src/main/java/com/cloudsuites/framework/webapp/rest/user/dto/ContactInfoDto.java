package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonView(Views.ContactInfoView.class)
public class ContactInfoDto {

    private Long contactInfoId;

    private String phoneNumber;

    private String email;

}

