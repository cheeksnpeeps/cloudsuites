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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("BILLIARD_ROOM")
public class BilliardRoomDto extends AmenityDto {

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Number of billiard tables available", example = "3")
    private Integer numberOfBilliardTables;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if pool cues are provided", example = "true")
    private Boolean providesPoolCues;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if food and drinks are allowed", example = "false")
    private Boolean allowsFoodAndDrinks;
}

