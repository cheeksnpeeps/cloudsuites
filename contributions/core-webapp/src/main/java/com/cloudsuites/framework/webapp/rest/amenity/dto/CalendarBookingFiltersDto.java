package com.cloudsuites.framework.webapp.rest.amenity.dto;

import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingStatus;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarBookingFiltersDto {

    @JsonView(Views.BookingCalendarView.class)
    @Schema(description = "List of amenity types", example = "[\"SWIMMING_POOL\", \"ELEVATOR\"]")
    List<AmenityType> byAmenityTypes;

    @JsonView(Views.BookingCalendarView.class)
    @Schema(description = "List of booking statuses", example = "[\"PENDING\", \"APPROVED\"]")
    List<BookingStatus> byBookingStatus;

    @Schema(description = "List of amenity ids", example = "[\"AMN-01J79E2Z8MQXSAVM1AMCFK2ADZ\", \"AMN-01J77YPYDY1CDAR0CSAJZ6GY9W\"]")
    @JsonView(Views.BookingCalendarView.class)
    List<String> byAmenityIds;

    @Schema(description = "List of tenant ids", example = "[\"TENANT-01J79E2Z8MQXSAVM1AMCFK2ADZ\", \"TENANT-01J77YPYDY1CDAR0CSAJZ6GY9W\"]")
    @JsonView(Views.BookingCalendarView.class)
    List<String> byTenantIds;
    
    @Schema(description = "Start date of the calendar", example = "2021-08-01T00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    @JsonView(Views.BookingCalendarView.class)
    private LocalDateTime startDate;

    @Schema(description = "End date of the calendar", example = "2021-08-31T23:59:59")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    @JsonView(Views.BookingCalendarView.class)
    private LocalDateTime endDate;
    
    @JsonView(Views.BookingCalendarView.class)
    @Schema(hidden = true)
    private Map<AmenityType, List<String>> amenities;
}

