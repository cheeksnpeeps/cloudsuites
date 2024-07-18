package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildingDto {

	@JsonView({Views.BuildingView.class, Views.UnitView.class})
	private Long buildingId;

	@NotBlank(message = "Building name is required")
	@JsonView({Views.BuildingView.class, Views.UnitView.class})
	private String name;

	@JsonBackReference
	@NotNull(message = "Management company is required")
	@JsonView({Views.BuildingView.class})
	private ManagementCompanyDto managementCompany;

	@Valid
	@JsonView({Views.BuildingView.class})
	private AddressDto address;

	@Valid
	@JsonManagedReference
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@JsonView({Views.BuildingView.class})
	private List<FloorDto> floors;

	@PositiveOrZero(message = "Total floors must be a non-negative number")
	private Integer totalFloors;

	@Positive(message = "Year built must be a positive number")
	private Integer yearBuilt;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private IdentityDto createdBy;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private IdentityDto lastModifiedBy;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private LocalDateTime createdAt;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private LocalDateTime lastModifiedAt;
}
