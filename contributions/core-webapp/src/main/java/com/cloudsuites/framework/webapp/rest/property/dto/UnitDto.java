package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.services.property.entities.Tenant;
import com.fasterxml.jackson.annotation.*;
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

	@Valid
	@JsonIgnore
	private BuildingDto building;

	@Valid
	@JsonBackReference
	private FloorDto floor;

	@NotBlank(message = "Unit number is required")
	private String unitNumber;

	@JsonBackReference(value = "tenant-unit")
	private List<Tenant> tenants;
}



