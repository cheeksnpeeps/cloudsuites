package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.webapp.rest.user.dto.ContactInfoDTO;
import com.cloudsuites.framework.webapp.rest.user.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagementCompanyDTO {

    private Long managementCompanyId;

    private String name;

    private String website;

    private List<BuildingDTO> buildings;

    private AddressDTO addressDTO;

    private ContactInfoDTO contactInfo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UserDTO createdBy;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UserDTO lastModifiedBy;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime lastModifiedAt;
}
