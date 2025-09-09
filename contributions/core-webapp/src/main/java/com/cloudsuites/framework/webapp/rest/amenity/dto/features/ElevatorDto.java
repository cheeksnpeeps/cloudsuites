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
@JsonTypeName("ELEVATOR")
public class ElevatorDto extends AmenityDto {



    @JsonView(Views.AmenityView.class)
    @Schema(description = "Maximum weight capacity of the elevator", example = "1000")
    private Integer weightCapacity;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the elevator is wheelchair accessible", example = "true")
    private Boolean isWheelchairAccessible;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Number of floors serviced by the elevator", example = "10")
    private Integer floorsServiced;
}

