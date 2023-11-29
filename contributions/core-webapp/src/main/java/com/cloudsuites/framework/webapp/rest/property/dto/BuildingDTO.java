package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.webapp.rest.user.dto.UserDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class BuildingDTO {

	private Long buildingId;

	private String name;

	private PropertyManagementCompanyDTO propertyManagementCompany;

	private AddressDTO addressDTO;

	private List<FloorDTO> floorDTOS;

	private Integer totalFloors;

	private Integer yearBuilt;

	private UserDTO createdBy;

	private UserDTO lastModifiedBy;

	private LocalDateTime createdAt;

	private LocalDateTime lastModifiedAt;

}
