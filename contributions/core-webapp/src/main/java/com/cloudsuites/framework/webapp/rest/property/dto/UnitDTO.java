package com.cloudsuites.framework.webapp.rest.property.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitDTO {

	private Long unitId;

	private BuildingDTO buildingDTO;

	private FloorDTO floorDTO;

	private String unitNumber;

	private Double squareFootage;

	// Constructors, getters, and setters

	// Additional methods if needed
}


