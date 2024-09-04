package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.services.property.features.entities.LeaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LeaseDto {

    @Schema(hidden = true)
    @JsonView({Views.UnitView.class, Views.OwnerView.class, Views.TenantView.class})
    private String leaseId;

    @Schema(description = "Tenant ID of the lease")
    @JsonView({Views.UnitView.class, Views.OwnerView.class, Views.TenantView.class})
    private String tenantId;

    @Schema(description = "Owner ID of the lease")
    @JsonView({Views.UnitView.class, Views.OwnerView.class, Views.TenantView.class})
    private String ownerId;

    @Schema(description = "Unit ID of the lease")
    @JsonView({Views.UnitView.class, Views.OwnerView.class, Views.TenantView.class})
    private String unitId;

    @Schema(description = "Start date of the lease")
    @JsonView({Views.UnitView.class, Views.OwnerView.class, Views.TenantView.class})
    private LocalDate startDate;

    @Schema(description = "End date of the lease")
    @JsonView({Views.UnitView.class, Views.OwnerView.class, Views.TenantView.class})
    private LocalDate endDate;

    @Schema(hidden = true)
    @JsonView({Views.UnitView.class, Views.OwnerView.class, Views.TenantView.class})
    private LocalDate originalStartDate;

    @Schema(hidden = true)
    @JsonView({Views.UnitView.class, Views.OwnerView.class, Views.TenantView.class})
    private LocalDate originalEndDate;

    @Schema(description = "Rental amount of the lease")
    @JsonView({Views.UnitView.class, Views.OwnerView.class, Views.TenantView.class})
    private Double rentalAmount;

    @Schema(description = "Renewal count of the lease")
    @JsonView({Views.UnitView.class, Views.OwnerView.class, Views.TenantView.class})
    private int renewalCount;

    @Schema(description = "Status of the lease")
    @JsonView({Views.UnitView.class, Views.OwnerView.class, Views.TenantView.class})
    private LeaseStatus status;
}
