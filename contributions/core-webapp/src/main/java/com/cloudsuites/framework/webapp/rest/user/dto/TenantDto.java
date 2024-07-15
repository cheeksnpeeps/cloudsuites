package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.services.property.entities.Building;
import com.cloudsuites.framework.services.property.entities.Unit;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantDto{

    private Long buildingId;

    private Long tenantId;

    @JsonManagedReference(value = "tenant-identity")
    private IdentityDto identity;

    @JsonManagedReference(value = "tenant-unit")
    private Unit unit;

    @JsonManagedReference(value = "tenant-building")
    private Building building;

}
