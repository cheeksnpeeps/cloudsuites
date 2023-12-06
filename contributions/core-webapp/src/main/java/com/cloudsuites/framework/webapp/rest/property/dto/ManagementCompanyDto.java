package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.webapp.rest.user.dto.ContactInfoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class ManagementCompanyDto {

    private Long managementCompanyId;

    @NotBlank(message = "Name is required")
    private String name;

    @URL(message = "Invalid website URL")
    private String website;

    @Valid
    private List<BuildingDto> buildings;

    @Valid
    private AddressDto addressDTO;

    @Valid
    private ContactInfoDTO contactInfo;
}

