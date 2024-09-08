package com.cloudsuites.framework.webapp.rest.property.dto;

import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityDto;
import com.cloudsuites.framework.webapp.rest.user.dto.AddressDto;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BuildingDto {

	@JsonView({Views.StaffView.class, Views.TenantView.class, Views.FloorView.class, Views.BuildingView.class, Views.CompanyView.class, Views.OwnerView.class})
	@Schema(hidden = true)
	private String buildingId;

	@JsonView({Views.StaffView.class, Views.TenantView.class, Views.FloorView.class, Views.BuildingView.class, Views.CompanyView.class, Views.OwnerView.class})
	@Size(min = 3, max = 20, message = "Building must be between 3 and 100 characters long")
	@Schema(description = "Name of the building", example = "Skyline Tower")
	private String name;

	@JsonView(Views.BuildingView.class)
	@Schema(description = "Management company of the building")
	private CompanyDto company;

	@JsonView({Views.StaffView.class, Views.BuildingView.class})
	@Schema(description = "Address of the building")
	private AddressDto address;

	@PositiveOrZero(message = "Total floors must be a non-negative number")
	@JsonView({Views.StaffView.class, Views.BuildingView.class, Views.CompanyView.class})
	@Schema(description = "Total number of floors in the building", example = "10")
	private Integer totalFloors;

	@Positive(message = "Year built must be a positive number")
	@JsonView(Views.BuildingView.class)
	@Schema(description = "Year the building was built", example = "1990")
	private Integer yearBuilt;

	@Schema(hidden = true)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@JsonView(Views.BuildingView.class)
	private IdentityDto createdBy;

	@Schema(hidden = true)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@JsonView(Views.BuildingView.class)
	private IdentityDto lastModifiedBy;

	@Schema(hidden = true)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@JsonView(Views.BuildingView.class)
	private LocalDateTime createdAt;

	@Schema(hidden = true)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@JsonView(Views.BuildingView.class)
	private LocalDateTime lastModifiedAt;

	@JsonView(Views.BuildingView.class)
    @Schema(hidden = true)
	private List<AmenityDto> amenities;

}
