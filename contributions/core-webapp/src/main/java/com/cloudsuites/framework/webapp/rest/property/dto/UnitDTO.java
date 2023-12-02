package com.cloudsuites.framework.webapp.rest.property.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class UnitDto {

	private Long unitId;

	@Valid
	private BuildingDto building;

	@Valid
	private FloorDto floor;

	@NotBlank(message = "Unit number is required")
	private String unitNumber;
}



