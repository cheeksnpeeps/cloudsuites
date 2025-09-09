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
@JsonTypeName("WINE_TASTING_ROOM")
public class WineTastingRoomDto extends AmenityDto {



    @JsonView(Views.AmenityView.class)
    @Schema(description = "Number of wines available for tasting", example = "50")
    private Integer numberOfWinesAvailable;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if private events are allowed", example = "true")
    private Boolean allowsPrivateEvents;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Max seating capacity of the wine tasting room", example = "20")
    private Integer seatingCapacity;
}
