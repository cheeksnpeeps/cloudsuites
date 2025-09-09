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
@JsonTypeName("TENNIS_COURT")
public class TennisCourtDto extends AmenityDto {



    @JsonView(Views.AmenityView.class)
    @Schema(description = "Surface type of the tennis court (e.g., clay, hard, grass)", example = "Clay")
    private String courtSurface;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the court is indoor", example = "false")
    private Boolean isIndoor;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the court has lighting for night play", example = "true")
    private Boolean lighting;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Height of the net in meters", example = "1.07")
    private Double netHeight;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if there are seats for spectators", example = "true")
    private Boolean hasSpectatorSeats;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if there are benches for players to sit", example = "true")
    private Boolean hasBenches;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Fee for booking the tennis court", example = "30.00")
    private BigDecimal bookingFee;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Size of the court in square meters", example = "650.0")
    private Double courtSize;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the court can be booked", example = "true")
    private Boolean isBookable;
}

