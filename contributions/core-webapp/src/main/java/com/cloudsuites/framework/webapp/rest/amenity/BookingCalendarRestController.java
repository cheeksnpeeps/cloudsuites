package com.cloudsuites.framework.webapp.rest.amenity;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingStatus;
import com.cloudsuites.framework.services.amenity.service.AmenityBookingCalendarService;
import com.cloudsuites.framework.services.amenity.service.AmenityService;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.service.TenantService;
import com.cloudsuites.framework.webapp.rest.amenity.dto.*;
import com.cloudsuites.framework.webapp.rest.amenity.mapper.AmenityBookingMapper;
import com.cloudsuites.framework.webapp.rest.amenity.mapper.AmenityMapper;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/buildings/{buildingId}")
@Tags(value = {@Tag(name = "AmenityBookingCalendar", description = "Operations related amenity booking calendar")})
public class BookingCalendarRestController {

    private static final Logger logger = LoggerFactory.getLogger(BookingCalendarRestController.class);
    private final AmenityBookingCalendarService amenityBookingCalendarService;
    private final AmenityBookingMapper mapper;
    private final AmenityService amenityService;
    private final AmenityMapper amenityMapper;
    private final TenantService tenantService;

    @Autowired
    public BookingCalendarRestController(AmenityBookingCalendarService amenityBookingCalendarService, AmenityBookingMapper mapper, AmenityService amenityService, AmenityMapper amenityMapper, TenantService tenantService) {
        this.amenityBookingCalendarService = amenityBookingCalendarService;
        this.mapper = mapper;
        this.amenityService = amenityService;
        this.amenityMapper = amenityMapper;
        this.tenantService = tenantService;
    }

    @PreAuthorize("hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(
            summary = "Get Booking Calendar for a Tenant",
            description = "Get a list of all bookings amenities for a tenant within a given time range"
    )
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Not Found")
    @JsonView(Views.BookingCalendarView.class)
    @PostMapping("/tenants/{tenantId}/bookings/calendar")
    public ResponseEntity<AmenityBookingCalendarDto> getTenantBookingCalendar(
            @PathVariable String tenantId,
            @PathVariable String buildingId,
            @RequestBody @Parameter(description = "Calendar filters") CalendarBookingFiltersDto calendarBookingFiltersDto
    ) throws InvalidOperationException, NotFoundResponseException {

        logger.debug("Received request to fetch tenant booking calendar for tenantId: {}, buildingId: {}", tenantId, buildingId);
        validateFilters(calendarBookingFiltersDto);

        logger.debug("Fetching amenities for buildingId: {} using filters: {}", buildingId, calendarBookingFiltersDto);
        List<Amenity> amenities = getAmenities(buildingId, calendarBookingFiltersDto);
        List<String> amenityIds = amenities.stream().map(Amenity::getAmenityId).toList();

        logger.debug("Fetching tenant information for tenantId: {}", tenantId);
        Tenant tenant = tenantService.getTenantById(tenantId);

        logger.debug("Fetching bookings for tenantId: {} for amenityIds: {} between {} and {}", tenantId, amenityIds, calendarBookingFiltersDto.getStartDate(), calendarBookingFiltersDto.getEndDate());
        List<AmenityBooking> bookedSlots = amenityBookingCalendarService.getBookingsForUser(
                        null, amenityIds, List.of(BookingStatus.REQUESTED), calendarBookingFiltersDto.getStartDate(), calendarBookingFiltersDto.getEndDate())
                .collectList().block();

        logger.debug("Building response DTO for tenant booking calendar");
        AmenityBookingCalendarDto calendarDto = buildCalendarDto(tenant, amenities, bookedSlots, calendarBookingFiltersDto);

        logger.debug("Successfully fetched tenant booking calendar for tenantId: {}", tenantId);
        return ResponseEntity.ok().body(calendarDto);
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @Operation(
            summary = "Get Booking Calendar for a Staff",
            description = "Get a list of all bookings amenities for a staff  within a given time range"
    )
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Not Found")
    @JsonView(Views.BookingCalendarView.class)
    @PostMapping("/staff/{staffId}/bookings/calendar")
    public ResponseEntity<AmenityBookingCalendarDto> getStaffBookingCalendar(
            @PathVariable String staffId,
            @PathVariable String buildingId,
            @RequestBody @Parameter(description = "Calendar filters") CalendarBookingFiltersDto calendarBookingFiltersDto
    ) throws InvalidOperationException {

        logger.debug("Received request to fetch staff booking calendar for staffId: {}, buildingId: {}", staffId, buildingId);
        validateStaffFilters(calendarBookingFiltersDto);

        logger.debug("Fetching amenities for buildingId: {} using filters: {}", buildingId, calendarBookingFiltersDto);
        List<Amenity> amenities = getAmenities(buildingId, calendarBookingFiltersDto);
        List<String> amenityIds = amenities.stream().map(Amenity::getAmenityId).toList();
        List<BookingStatus> bookingStatuses = calendarBookingFiltersDto.getByBookingStatus();
        List<String> tenantIds = calendarBookingFiltersDto.getByTenantIds();

        logger.debug("Fetching bookings for staffId: {} with amenityIds: {}, bookingStatuses: {}, tenantIds: {}", staffId, amenityIds, bookingStatuses, tenantIds);
        List<AmenityBooking> bookedSlots = amenityBookingCalendarService.getBookingsForUser(
                        tenantIds, amenityIds, bookingStatuses, calendarBookingFiltersDto.getStartDate(), calendarBookingFiltersDto.getEndDate())
                .collectList().block();

        logger.debug("Building response DTO for staff booking calendar");
        AmenityBookingCalendarDto calendarDto = buildCalendarDto(null, amenities, bookedSlots, calendarBookingFiltersDto);

        logger.debug("Successfully fetched staff booking calendar for staffId: {}", staffId);
        return ResponseEntity.ok().body(calendarDto);
    }

    private void validateStaffFilters(CalendarBookingFiltersDto calendarBookingFiltersDto
    ) throws InvalidOperationException {
        if (calendarBookingFiltersDto == null) {
            logger.debug("Invalid request: Calendar filters are null");
            throw new InvalidOperationException("Calendar filters cannot be null");
        }
        if (calendarBookingFiltersDto.getByBookingStatus() == null) {
            logger.debug("No booking status provided, defaulting to REQUESTED");
            calendarBookingFiltersDto.setByBookingStatus(List.of(BookingStatus.REQUESTED));
        }
    }

    private void validateFilters(
            CalendarBookingFiltersDto calendarBookingFiltersDto
    ) throws InvalidOperationException {

        if (calendarBookingFiltersDto == null) {
            logger.debug("Invalid request: Calendar filters are null");
            throw new InvalidOperationException("Calendar filters cannot be null");
        }

        LocalDateTime startDate = calendarBookingFiltersDto.getStartDate();
        LocalDateTime endDate = calendarBookingFiltersDto.getEndDate();

        if (startDate == null || endDate == null) {
            logger.debug("Invalid request: Start date or end date is null");
            throw new InvalidOperationException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            logger.debug("Invalid request: Start date {} is after end date {}", startDate, endDate);
            throw new InvalidOperationException("Start time cannot be after end time");
        }
    }

    private List<Amenity> getAmenities(String buildingId, CalendarBookingFiltersDto filters) {
        if (filters.getByAmenityIds() != null) {
            logger.debug("Fetching amenities by ids: {}", filters.getByAmenityIds());
            return amenityService.getAmenitiesByIds(filters.getByAmenityIds());
        } else if (filters.getByAmenityTypes() != null) {
            logger.debug("Fetching amenities by types for buildingId: {}, amenity types: {}", buildingId, filters.getByAmenityTypes());
            return amenityService.getAmenitiesByBuildingAndTypes(buildingId, filters.getByAmenityTypes());
        }
        logger.debug("Fetching all amenities for buildingId: {}", buildingId);
        return amenityService.getAmenitiesByBuildingId(buildingId);
    }

    private AmenityBookingCalendarDto buildCalendarDto(
            Tenant tenant, List<Amenity> amenities,
            List<AmenityBooking> bookedSlots,
            CalendarBookingFiltersDto filters
    ) {

        logger.debug("Converting booked slots and amenities to DTOs");
        List<AmenityBookingDto> bookedSlotsDtos = mapper.convertToDTOList(bookedSlots);
        List<AmenityDto> amenitiesDtos = amenityMapper.convertToDTOList(amenities);

        if (tenant != null) {
            logger.debug("Marking bookings as current user for tenantId: {}", tenant.getTenantId());
            bookedSlotsDtos.forEach(booking -> {
                if (booking.getUserId().equals(tenant.getTenantId())) {
                    booking.setIsCurrentUser(true);
                }
            });
        }

        AmenityScheduleDto amenityScheduleDto = new AmenityScheduleDto();
        amenityScheduleDto.setBookedSlots(bookedSlotsDtos);
        amenityScheduleDto.setAmenities(amenitiesDtos);

        AmenityBookingCalendarDto calendarDto = new AmenityBookingCalendarDto();
        calendarDto.setAmenitySchedule(amenityScheduleDto);
        calendarDto.setFilters(filters);

        logger.debug("Successfully built AmenityBookingCalendarDto");
        return calendarDto;
    }
}
