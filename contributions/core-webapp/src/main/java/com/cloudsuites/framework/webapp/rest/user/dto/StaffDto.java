package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.services.property.personas.entities.StaffRole;
import com.cloudsuites.framework.webapp.rest.property.dto.BuildingDto;
import com.cloudsuites.framework.webapp.rest.property.dto.ManagementCompanyDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
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
    private IdentityDto identity;

    @JsonView(Views.StaffView.class)
    @Schema(description = "Role of the staff", example = "BUILDING_SECURITY")
    private StaffRole staffRole;

    @JsonView(Views.StaffView.class)
    @Schema(hidden = true)
    private ManagementCompanyDto managementCompany;

    @JsonView(Views.StaffView.class)
    @Schema(hidden = true)
    private BuildingDto building;
}

