package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.webapp.rest.user.dto.AddressDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CompanyDto {

    @JsonView({Views.StaffView.class, Views.CompanyView.class, Views.BuildingView.class})
    @Schema(hidden = true)
    private String companyId;

    @JsonView({Views.StaffView.class, Views.CompanyView.class, Views.BuildingView.class})
    @NotBlank(message = "Name is required")
    @Schema(description = "Name of the management company", example = "Skyline Property Management")
    private String name;

    @JsonView({Views.StaffView.class, Views.CompanyView.class, Views.BuildingView.class})
    @URL(message = "Invalid website URL")
    @Schema(description = "Website of the management company", example = "https://www.skylinepropertymanagement.com")
    private String website;

    @JsonView({Views.StaffView.class, Views.CompanyView.class})
    @NotBlank(message = "Address is required")
    @Schema(description = "Address of the management company")
    private AddressDto address;
}

