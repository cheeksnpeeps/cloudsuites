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

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OwnerDto {

    @JsonView({Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    @Schema(hidden = true)
    private String ownerId;

    @JsonView({Views.OwnerView.class, Views.UnitView.class})
    @Schema(description = "Owner's identity details")
    private IdentityDto identity;

    @JsonView({Views.OwnerView.class})
    @Schema(hidden = true)
    private List<UnitDto> units;

    @JsonView({Views.OwnerView.class})
    @Schema(hidden = true)
    private List<BuildingDto> buildings;
}
