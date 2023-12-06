package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.webapp.rest.user.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class BuildingDto {

	private Long buildingId;

	@NotBlank(message = "Building name is required")
	private String name;

	@JsonIgnoreProperties("buildings")
	@NotNull(message = "Management company is required")
	private ManagementCompanyDto managementCompany;

	@Valid
	private AddressDto address;

	@Valid
	private List<FloorDto> floors;

	@PositiveOrZero(message = "Total floors must be a non-negative number")
	private Integer totalFloors;

	@Positive(message = "Year built must be a positive number")
	private Integer yearBuilt;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private UserDTO createdBy;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private UserDTO lastModifiedBy;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private LocalDateTime createdAt;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private LocalDateTime lastModifiedAt;
}
