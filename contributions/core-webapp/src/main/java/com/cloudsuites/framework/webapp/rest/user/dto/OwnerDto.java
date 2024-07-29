package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.services.property.personas.entities.OwnerStatus;
import com.cloudsuites.framework.webapp.rest.property.dto.UnitDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OwnerDto {

    @JsonView({Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    @Schema(hidden = true)
    private String ownerId;

    @JsonView({Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    @Schema(description = "Owner's identity details")
    @Valid // Ensure that the Identity object is validated
    @NotNull(message = "Identity is required")
    private IdentityDto identity;

    @JsonView({Views.OwnerView.class})
    @Schema(hidden = true)
    private List<UnitDto> units;

    @Schema(description = "Owner is the Tenant of the same unit", example = "true")
    @JsonView(Views.OwnerView.class)
    private Boolean isPrimaryTenant;

    @JsonView(Views.OwnerView.class)
    @Schema(description = "Owner status", example = "ACTIVE")
    private OwnerStatus status;
}
