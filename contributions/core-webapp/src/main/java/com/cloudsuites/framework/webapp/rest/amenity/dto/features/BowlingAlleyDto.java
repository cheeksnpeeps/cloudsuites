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
@JsonTypeName("BOWLING_ALLEY")
public class BowlingAlleyDto extends AmenityDto {

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Number of lanes available", example = "4")
    private Integer numberOfLanes;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if bowling shoes are provided", example = "true")
    private Boolean providesShoes;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if food and drinks are allowed", example = "true")
    private Boolean allowsFoodAndDrinks;
}
