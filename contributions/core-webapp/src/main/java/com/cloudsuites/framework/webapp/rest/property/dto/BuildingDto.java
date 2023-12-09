package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.webapp.rest.user.dto.UserDto;
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
@JsonView(Views.BuildingView.class)
public class BuildingDto {

	private Long buildingId;

	@NotBlank(message = "Building name is required")
	private String name;

	@JsonBackReference
	@NotNull(message = "Management company is required")
	private ManagementCompanyDto managementCompany;

	@Valid
	private AddressDto address;

	@Valid
	@JsonManagedReference
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<FloorDto> floors;

	@PositiveOrZero(message = "Total floors must be a non-negative number")
	private Integer totalFloors;

	@Positive(message = "Year built must be a positive number")
	private Integer yearBuilt;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private UserDto createdBy;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private UserDto lastModifiedBy;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private LocalDateTime createdAt;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private LocalDateTime lastModifiedAt;
}
