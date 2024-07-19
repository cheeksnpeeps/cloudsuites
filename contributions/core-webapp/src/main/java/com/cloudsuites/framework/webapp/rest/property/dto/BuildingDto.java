package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.webapp.rest.user.dto.AddressDto;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import com.fasterxml.jackson.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
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

	@JsonView({Views.StaffView.class, Views.BuildingView.class, Views.ManagementCompanyView.class})
	@Schema(hidden = true)
	private Long buildingId;

	@JsonView({Views.StaffView.class, Views.BuildingView.class, Views.ManagementCompanyView.class})
	@NotBlank(message = "Building name is required")
	@Schema(description = "Name of the building", example = "Skyline Tower")
	private String name;

	@JsonView(Views.BuildingView.class)
	@JsonBackReference(value = "managementCompany")
	@NotNull(message = "Management company is required")
	@Schema(description = "Management company of the building")
	private ManagementCompanyDto managementCompany;

	@JsonView({Views.StaffView.class, Views.BuildingView.class})
	@Schema(description = "Address of the building")
	private AddressDto address;

	@Valid
	@JsonManagedReference(value = "building")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@JsonView(Views.BuildingView.class)
	private List<FloorDto> floors;

	@PositiveOrZero(message = "Total floors must be a non-negative number")
	@JsonView({Views.StaffView.class, Views.BuildingView.class, Views.ManagementCompanyView.class})
	private Integer totalFloors;

	@Positive(message = "Year built must be a positive number")
	private Integer yearBuilt;

	@Schema(hidden = true)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private IdentityDto createdBy;

	@Schema(hidden = true)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private IdentityDto lastModifiedBy;

	@Schema(hidden = true)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private LocalDateTime createdAt;

	@Schema(hidden = true)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private LocalDateTime lastModifiedAt;
}
