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
@JsonTypeName("GAMES_ROOM")
public class GamesRoomDto extends AmenityDto {

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Number of video game consoles available", example = "4")
    private Integer numberOfGameConsoles;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if board games are available", example = "true")
    private Boolean hasBoardGames;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Maximum capacity of the games room", example = "20")
    private Integer maxCapacity;
}
