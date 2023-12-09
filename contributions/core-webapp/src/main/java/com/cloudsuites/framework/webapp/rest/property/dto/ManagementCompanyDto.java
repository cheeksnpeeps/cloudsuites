package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.webapp.rest.user.dto.ContactInfoDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
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
@JsonView(Views.ManagementCompanyView.class)
public class ManagementCompanyDto {

    private Long managementCompanyId;

    @NotBlank(message = "Name is required")
    private String name;

    @URL(message = "Invalid website URL")
    private String website;

    @Valid
    @JsonManagedReference
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<BuildingDto> buildings;

    @Valid
    private AddressDto addressDTO;

    @Valid
    private ContactInfoDto contactInfo;
}

