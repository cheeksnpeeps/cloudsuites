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
@JsonTypeName("THEATER")
public class TheaterDto extends AmenityDto {

    @JsonView({Views.AmenityView.class, Views.BuildingView.class})
    @Schema(description = "Type of the amenity", example = "SWIMMING_POOL")
    @NotNull(message = "Type is mandatory")
    private AmenityType type;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Number of seats in the theater", example = "50")
    private Integer numberOfSeats;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the theater has 3D projection", example = "true")
    private Boolean has3dProjection;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the theater has surround sound", example = "true")
    private Boolean hasSurroundSound;
}
