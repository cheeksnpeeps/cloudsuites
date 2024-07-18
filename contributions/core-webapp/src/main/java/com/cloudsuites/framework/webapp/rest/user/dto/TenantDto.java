package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.webapp.rest.property.dto.BuildingDto;
import com.cloudsuites.framework.webapp.rest.property.dto.UnitDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantDto{

    @JsonView(Views.TenantView.class)
    private Long buildingId;

    @JsonView(Views.TenantView.class)
    private Long tenantId;

    @JsonView(Views.TenantView.class)
    private IdentityDto identity;

    @JsonView(Views.TenantView.class)
    private UnitDto unit;

    private BuildingDto building;

}
