package com.cloudsuites.framework.webapp.rest.amenity.dto.features;

import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("GYM")
public class GymDto extends AmenityDto {



    @JsonView(Views.AmenityView.class)
    @Schema(description = "Number of gym machines available", example = "30")
    private Integer numberOfMachines;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if personal trainers are available", example = "true")
    private Boolean hasPersonalTrainers;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if fitness classes are offered", example = "true")
    private Boolean hasFitnessClasses;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if changing rooms are available", example = "true")
    private Boolean hasChangingRooms;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if showers are available", example = "true")
    private Boolean hasShowers;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if lockers are available", example = "true")
    private Boolean hasLockers;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if towels are provided", example = "true")
    private Boolean hasTowels;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if water fountains are available", example = "true")
    private Boolean hasWaterFountains;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if wifi is available", example = "true")
    private Boolean hasWifi;

}
