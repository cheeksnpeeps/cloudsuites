package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.webapp.rest.user.dto.ContactInfoDTO;
import com.cloudsuites.framework.webapp.rest.user.dto.UserDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PropertyManagementCompanyDTO {

    private Long companyId;

    private String name;

    private String website;

    private List<BuildingDTO> buildings;

    private AddressDTO addressDTO;

    private ContactInfoDTO contactInfo;

    private UserDTO createdBy;

    private UserDTO lastModifiedBy;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;
}
