package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.services.property.features.entities.Lease;
import com.cloudsuites.framework.services.property.personas.entities.TenantRole;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.webapp.rest.property.dto.BuildingDto;
import com.cloudsuites.framework.webapp.rest.property.dto.UnitDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantDto{

    @Schema(hidden = true)
    private String buildingId;

    @Schema(hidden = true)
    @JsonView({Views.RoleView.class, Views.TenantView.class, Views.UnitView.class, Views.OwnerView.class})
    private String tenantId;

    @Schema(description = "Identity of the tenant")
    @JsonView({Views.RoleView.class, Views.TenantView.class, Views.UnitView.class, Views.OwnerView.class})
    @Valid // Ensure that the Identity object is validated
    @NotNull(message = "Identity is required")
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

    @Schema(hidden = true)
    @JsonView(Views.TenantView.class)
    private OwnerDto owner;

    @Schema(description = "Tenant status", example = "ACTIVE")
    @JsonView({Views.RoleView.class, Views.TenantView.class, Views.UnitView.class, Views.OwnerView.class})
    private TenantStatus status;

    @Schema(description = "Tenant role", example = "DEFAULT")
    @JsonView({Views.RoleView.class, Views.TenantView.class, Views.UnitView.class, Views.OwnerView.class})
    private TenantRole role;

    @Schema(description = "The lease associated with the unit")
    @JsonView({Views.RoleView.class, Views.TenantView.class, Views.UnitView.class, Views.OwnerView.class})
    private Lease lease;

    public TenantDto() {
        this.isPrimaryTenant = false; // Default value
        this.isOwner = false; // Default value
    }
}
