package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.services.property.personas.entities.StaffRole;
import com.cloudsuites.framework.webapp.rest.property.dto.BuildingDto;
import com.cloudsuites.framework.webapp.rest.property.dto.CompanyDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffDto {

    @JsonView(Views.StaffView.class)
    @Schema(hidden = true)
    private String staffId;

    @JsonView({Views.StaffView.class})
    @Schema(description = "Owner's identity details")
    @Valid // Ensure that the Identity object is validated
    @NotNull(message = "Identity is required")
    private IdentityDto identity;

    @JsonView(Views.StaffView.class)
    @Schema(description = "Role of the staff", example = "BUILDING_SECURITY")
    @Valid // Ensure that the Identity object is validated
    @NotNull(message = "Staff role is required")
    private StaffRole staffRole;

    @JsonView(Views.StaffView.class)
    @Schema(hidden = true)
    private CompanyDto company;

    @JsonView(Views.StaffView.class)
    @Schema(hidden = true)
    private BuildingDto building;
}

