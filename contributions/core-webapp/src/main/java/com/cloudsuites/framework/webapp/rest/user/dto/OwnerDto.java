package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.webapp.rest.property.dto.BuildingDto;
import com.cloudsuites.framework.webapp.rest.property.dto.UnitDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
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
    private Long ownerId;

    @JsonView({Views.OwnerView.class, Views.UnitView.class})
    private IdentityDto identity;

    @JsonView({Views.OwnerView.class})
    private List<UnitDto> units;

    @JsonView({Views.OwnerView.class})
    private List<BuildingDto> buildings;
}
