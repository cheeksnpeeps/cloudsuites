package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.webapp.rest.property.dto.BuildingDto;
import com.cloudsuites.framework.webapp.rest.property.dto.UnitDto;
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
public class TenantDto{

    @Schema(hidden = true)
    private Long buildingId;

    @Schema(hidden = true)
    @JsonView(Views.TenantView.class)
    private Long tenantId;

    @Schema(description = "Identity of the tenant")
    @JsonView(Views.TenantView.class)
    private IdentityDto identity;

    @Schema(hidden = true)
    @JsonView(Views.TenantView.class)
    private UnitDto unit;

    @JsonView(Views.TenantView.class)
    @Schema(hidden = true)
    private BuildingDto building;

}
