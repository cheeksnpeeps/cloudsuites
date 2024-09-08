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
@JsonTypeName("OTHER")
public class OtherDto extends AmenityDto {

    @JsonView({Views.AmenityView.class, Views.BuildingView.class})
    @Schema(description = "Type of the amenity", example = "SWIMMING_POOL")
    @NotNull(message = "Type is mandatory")
    private AmenityType type;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Description of the amenity", example = "Custom-built rock climbing wall")
    private String description;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Special instructions or notes for the amenity", example = "Available only with prior booking")
    private String specialInstructions;
}
