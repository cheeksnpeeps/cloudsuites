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

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("PARTY_ROOM")
public class PartyRoomDto extends AmenityDto {

    @JsonView({Views.AmenityView.class, Views.BuildingView.class})
    @Schema(description = "Type of the amenity", example = "SWIMMING_POOL")
    @NotNull(message = "Type is mandatory")
    private AmenityType type;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Maximum number of people the room can accommodate", example = "100")
    private Integer capacity;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the room has an audio system", example = "true")
    private Boolean hasAudioSystem;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the room is equipped with a projector", example = "true")
    private Boolean hasProjector;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if there is a bar in the room", example = "false")
    private Boolean hasBar;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Fee for booking the room", example = "150.00")
    private BigDecimal bookingFee;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Additional features or amenities available in the room", example = "Decorations, furniture")
    private String amenities;
}
