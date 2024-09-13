package com.cloudsuites.framework.webapp.rest.amenity.dto;

import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonView(Views.BookingCalendarView.class)
public class AmenityBookingCalendarDto {

    @Schema(hidden = true)
    @JsonView(Views.BookingCalendarView.class)
    private AmenityScheduleDto amenitySchedule;

    @JsonView(Views.BookingCalendarView.class)
    @Schema(description = "Filters for the calendar", example = "{\"startDate\":\"2021-08-01T00:00:00\",\"endDate\":\"2021-08-31T23:59:59\"," +
            "\"byAmenityTypes\":[\"SWIMMING_POOL\",\"EVEVATOR\"]," +
            "\"byBookingStatus\":[\"PENDING\",\"APPROVED\"]," +
            "\"byAmenityIds\":[\"AMN-01J79E2Z8MQXSAVM1AMCFK2ADZ\",\"AMN-01J77YPYDY1CDAR0CSAJZ6GY9W\"]}")
    private CalendarBookingFiltersDto filters;
}