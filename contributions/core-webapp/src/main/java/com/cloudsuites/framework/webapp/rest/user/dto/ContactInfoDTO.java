package com.cloudsuites.framework.webapp.rest.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactInfoDTO {

    private Long contactInfoId;

    private String phoneNumber;

    private String email;

}

