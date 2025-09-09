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
@JsonTypeName("MASSAGE_ROOM")
public class MassageRoomDto extends AmenityDto {



    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if a licensed therapist is available", example = "true")
    private Boolean hasLicensedTherapist;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if a sauna is available in the massage room", example = "true")
    private Boolean hasSauna;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Maximum capacity of the massage room", example = "5")
    private Integer maxCapacity;
}
