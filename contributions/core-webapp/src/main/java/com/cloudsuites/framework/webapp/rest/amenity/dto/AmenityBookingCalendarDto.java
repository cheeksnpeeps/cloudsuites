package com.cloudsuites.framework.webapp.rest.amenity.dto;

import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmenityBookingCalendarDto {

    @Schema(description = "Amenity ID")
    @JsonView({Views.AmenityBooking.class})
    private String amenityId;

    @JsonView({Views.AmenityBooking.class})
    @Schema(description = "Booked slots")
    private List<AmenityBookingDto> bookedSlots;

    @JsonView({Views.AmenityBooking.class})
    @Schema(description = "Available slots")
    private List<LocalDateTime> availableSlots;
}