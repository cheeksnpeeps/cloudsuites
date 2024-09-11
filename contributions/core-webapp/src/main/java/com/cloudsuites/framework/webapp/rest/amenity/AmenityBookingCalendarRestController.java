package com.cloudsuites.framework.webapp.rest.amenity;

import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.service.AmenityBookingCalendarService;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityBookingCalendarDto;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityBookingDto;
import com.cloudsuites.framework.webapp.rest.amenity.mapper.AmenityBookingMapper;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/amenities/{amenityId}")
@Tags(value = {@Tag(name = "AmenityBookingCalendar", description = "Operations related to the amenity booking calendar")})
public class AmenityBookingCalendarRestController {

    private static final Logger logger = LoggerFactory.getLogger(AmenityBookingCalendarRestController.class);
    private final AmenityBookingCalendarService amenityBookingCalendarService;
    private final AmenityBookingMapper mapper;

    @Autowired
    public AmenityBookingCalendarRestController(AmenityBookingCalendarService amenityBookingCalendarService, AmenityBookingMapper mapper) {
        this.amenityBookingCalendarService = amenityBookingCalendarService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(summary = "Get Booking Calendar for a Tenant", description = "Get a list of all bookings for a specific amenity and tenant within a given time range")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Not Found")
    @JsonView(Views.AmenityBooking.class)
    @GetMapping("/tenants/{tenantId}/bookings/calendar")
    public ResponseEntity<AmenityBookingCalendarDto> getBookingCalendar(
            @PathVariable String tenantId,
            @PathVariable String amenityId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startTime") @Parameter(description = "Start time of the range") LocalDateTime startTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endTime") @Parameter(description = "End time of the range") LocalDateTime endTime) throws InvalidOperationException {

        if (startTime.isAfter(endTime)) {
            throw new InvalidOperationException("Start time cannot be after end time");
        }
        logger.debug("Getting booking calendar for amenity: {} between {} and {}", amenityId, startTime, endTime);
        // Fetch booked and available slots
        List<AmenityBooking> bookedSlots = amenityBookingCalendarService.getBookingsForUser(tenantId, null, startTime, endTime);
        List<AmenityBookingDto> bookedSlotsDtos = mapper.convertToDTOList(bookedSlots);
        return getAmenityBookingCalendar(amenityId, startTime, endTime, bookedSlotsDtos);
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Get Booking Calendar", description = "Get a list of all bookings for a specific amenity within a given time range")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Not Found")
    @JsonView(Views.AmenityBooking.class)
    @GetMapping("/bookings/calendar")
    public ResponseEntity<AmenityBookingCalendarDto> getBookingCalendarByAmenity(
            @PathVariable String amenityId,
            @RequestParam("startTime") @Parameter(description = "Start time of the range") LocalDateTime startTime,
            @RequestParam("endTime") @Parameter(description = "End time of the range") LocalDateTime endTime) throws InvalidOperationException {
        if (startTime.isAfter(endTime)) {
            throw new InvalidOperationException("Start time cannot be after end time");
        }
        logger.debug("Getting booking calendar for amenity: {} between {} and {}", amenityId, startTime, endTime);
        // Fetch booked and available slots
        List<AmenityBooking> bookedSlots = amenityBookingCalendarService.getBookingsForAmenity(amenityId, null, startTime, endTime);
        List<AmenityBookingDto> bookedSlotsDtos = mapper.convertToDTOList(bookedSlots);
        return getAmenityBookingCalendar(amenityId, startTime, endTime, bookedSlotsDtos);
    }

    private ResponseEntity<AmenityBookingCalendarDto> getAmenityBookingCalendar(String amenityId, LocalDateTime startTime, LocalDateTime endTime, List<AmenityBookingDto> bookedSlots) {
        List<LocalDateTime> availableSlots = amenityBookingCalendarService.getAvailableSlotsForAmenity(amenityId, startTime, endTime);

        AmenityBookingCalendarDto calendarDto = new AmenityBookingCalendarDto();
        calendarDto.setAmenityId(amenityId);
        calendarDto.setBookedSlots(bookedSlots);
        calendarDto.setAvailableSlots(availableSlots);

        logger.debug("Found {} booked slots and {} available slots for amenity: {}", bookedSlots.size(), availableSlots.size(), amenityId);

        return ResponseEntity.ok().body(calendarDto);
    }
}
