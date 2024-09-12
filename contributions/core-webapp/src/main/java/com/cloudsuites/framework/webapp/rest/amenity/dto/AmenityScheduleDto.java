package com.cloudsuites.framework.webapp.rest.amenity.dto;

import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmenityScheduleDto {

    @Schema(description = "The amenity details")
    @JsonView(Views.BookingCalendarView.class)
    private AmenityDto amenity;

    @Schema(description = "List of booked slots for the amenity")
    @JsonView(Views.BookingCalendarView.class)
    private List<AmenityBookingDto> bookedSlots;
}
