package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.webapp.rest.user.dto.OwnerDto;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnitDto {

	@Schema(hidden = true)
	@JsonView({Views.UnitView.class, Views.FloorView.class, Views.OwnerView.class, Views.TenantView.class})
	private String unitId;

	@Schema(description = "Building of the unit")
	@JsonView({Views.UnitView.class, Views.OwnerView.class})
	private BuildingDto building;

	@Schema(description = "Owner of the unit")
	@JsonView({Views.UnitView.class, Views.TenantView.class})
	private OwnerDto owner;

	@Schema(description = "Number of the unit", example = "101")
	@JsonView({Views.UnitView.class, Views.FloorView.class, Views.TenantView.class, Views.OwnerView.class})
	@NotNull(message = "Unit number must be provided")
	@Min(value = 1, message = "Unit number must be at least 1")
	@Max(value = 99999, message = "Unit number must be no more than 5 digits")
	private Integer unitNumber;

	@Schema(description = "Tenants of the unit")
	@JsonView({Views.UnitView.class, Views.OwnerView.class})
	private List<TenantDto> tenants;
}



