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
@JsonTypeName("YOGA_STUDIO")
public class YogaStudioDto extends AmenityDto {



    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if yoga mats are provided", example = "true")
    private Boolean providesYogaMats;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Maximum number of participants allowed", example = "15")
    private Integer maxParticipants;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "List of yoga classes available", example = "Hatha, Vinyasa, Power Yoga")
    private String availableClasses;
}

