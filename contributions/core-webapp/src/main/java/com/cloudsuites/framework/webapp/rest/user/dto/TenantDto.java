package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.webapp.rest.property.dto.BuildingDto;
import com.cloudsuites.framework.webapp.rest.property.dto.UnitDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantDto{

    @Schema(hidden = true)
    private String buildingId;

    @Schema(hidden = true)
    @JsonView({Views.TenantView.class, Views.UnitView.class, Views.OwnerView.class})
    private String tenantId;

    @Schema(description = "Identity of the tenant")
    @JsonView({Views.TenantView.class, Views.UnitView.class, Views.OwnerView.class})
    private IdentityDto identity;

    @Schema(hidden = true)
    @JsonView(Views.TenantView.class)
    private UnitDto unit;

    @JsonView(Views.TenantView.class)
    @Schema(hidden = true)
    private BuildingDto building;

    @Schema(description = "Tenant is Owner of the same unit", example = "true")
    @JsonView({Views.TenantView.class, Views.UnitView.class, Views.OwnerView.class})
    private Boolean isOwner;

    @Schema(description = "Tenant is Primary Tenant (Existing tenants will be cleared)", example = "true")
    @JsonView({Views.TenantView.class, Views.UnitView.class, Views.OwnerView.class})
    private Boolean isPrimaryTenant;

    @Schema(description = "Tenant status", example = "ACTIVE")
    @JsonView({Views.TenantView.class, Views.UnitView.class, Views.OwnerView.class})
    private TenantStatus status;

    public TenantDto() {
        this.isPrimaryTenant = false; // Default value
        this.isOwner = false; // Default value
        this.status = TenantStatus.INACTIVE;// Default value
    }
}
