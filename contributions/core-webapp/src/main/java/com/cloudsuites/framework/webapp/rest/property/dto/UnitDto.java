package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.services.property.entities.Tenant;
import com.cloudsuites.framework.webapp.rest.user.dto.OwnerDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonView(Views.UnitView.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnitDto {

	private Long unitId;

	@JsonView({Views.UnitView.class})
	private BuildingDto building;

	@JsonView({Views.UnitView.class, Views.TenantView.class})
	private OwnerDto owner;

	@Valid
	@JsonBackReference(value = "floor")
	@JsonView({Views.UnitView.class})
	private FloorDto floor;

	@JsonView({Views.UnitView.class, Views.TenantView.class, Views.OwnerView.class})
	@NotBlank(message = "Unit number is required")
	private String unitNumber;

	@JsonBackReference(value = "tenant-unit")
	@JsonView({Views.UnitView.class, Views.OwnerView.class})
	private List<Tenant> tenants;
}



