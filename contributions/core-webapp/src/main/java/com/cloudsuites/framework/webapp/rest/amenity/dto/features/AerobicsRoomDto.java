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

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("AEROBICS_ROOM")
public class AerobicsRoomDto extends AmenityDto {

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Type of floor in the aerobics room", example = "Hardwood")
    private String floorType;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the room has a built-in sound system", example = "true")
    private Boolean hasSoundSystem;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the room has mirror walls", example = "true")
    private Boolean mirrorWalls;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Maximum number of participants for a class", example = "20")
    private Integer maxClassCapacity;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the room has air conditioning", example = "true")
    private Boolean hasAC;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the room is equipped with workout equipment", example = "true")
    private Boolean hasEquipment;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Fee for booking the aerobics room", example = "50.00")
    private BigDecimal bookingFee;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Schedule for regular classes or sessions", example = "Monday: 9:00 AM - 10:00 AM")
    private String classSchedule;
}
