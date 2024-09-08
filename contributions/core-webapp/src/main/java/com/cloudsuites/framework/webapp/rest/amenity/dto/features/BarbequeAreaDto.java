package com.cloudsuites.framework.webapp.rest.amenity.dto.features;

import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("BARBEQUE_AREA") // This value should match the type in the base DTO
public class BarbequeAreaDto extends AmenityDto {

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Number of barbeque grills available", example = "3")
    private Integer numberOfGrills;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the barbeque area is covered", example = "true")
    private Boolean isCovered;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the area has seating arrangements", example = "true")
    private Boolean hasSeating;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the barbeque area has lighting", example = "true")
    private Boolean hasLighting;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if there is a water source available", example = "true")
    private Boolean hasWaterSource;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if there is a fire pit in the area", example = "true")
    private Boolean hasFirePit;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the area can be reserved for private use", example = "true")
    private Boolean hasReservationSystem;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Maximum number of people allowed in the barbeque area", example = "50")
    private Integer maxOccupancy;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Fee for renting the barbeque area", example = "100.00")
    private BigDecimal rentalFee;
}
