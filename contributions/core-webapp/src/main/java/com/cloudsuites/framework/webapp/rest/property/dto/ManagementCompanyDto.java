package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.webapp.rest.user.dto.AddressDto;
import com.cloudsuites.framework.webapp.rest.user.dto.StaffDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ManagementCompanyDto {

    @JsonView({Views.StaffView.class, Views.ManagementCompanyView.class})
    @Schema(hidden = true)
    private Long managementCompanyId;

    @JsonView({Views.StaffView.class, Views.ManagementCompanyView.class})
    @NotBlank(message = "Name is required")
    @Schema(description = "Name of the management company", example = "Skyline Property Management")
    private String name;

    @JsonView({Views.StaffView.class, Views.ManagementCompanyView.class})
    @URL(message = "Invalid website URL")
    @Schema(description = "Website of the management company", example = "https://www.skylinepropertymanagement.com")
    private String website;

    @JsonView(Views.ManagementCompanyView.class)
    @JsonManagedReference(value = "managementCompany")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(hidden = true)
    private List<BuildingDto> buildings;

    @JsonView({Views.StaffView.class, Views.ManagementCompanyView.class})
    @NotBlank(message = "Address is required")
    @Schema(description = "Address of the management company")
    private AddressDto address;

    @JsonView(Views.ManagementCompanyView.class)
    @Schema(hidden = true)
    private List<StaffDto> staffs;
}

