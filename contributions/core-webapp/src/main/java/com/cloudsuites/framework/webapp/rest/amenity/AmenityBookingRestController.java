package com.cloudsuites.framework.webapp.rest.amenity;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;
import com.cloudsuites.framework.services.amenity.service.AmenityBookingService;
import com.cloudsuites.framework.services.amenity.service.AmenityService;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
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
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/")
@Tags(value = {@Tag(name = "Amenity Bookings", description = "Operations related to amenity bookings")})
public class AmenityBookingRestController {

    private static final Logger logger = LoggerFactory.getLogger(AmenityBookingRestController.class);

    private final AmenityBookingService amenitybookingService;
    private final AmenityBookingMapper mapper;
    private final AmenityService amenityService;

    @Autowired
    public AmenityBookingRestController(AmenityBookingService amenitybookingService,
                                        AmenityBookingMapper mapper, AmenityService amenityService) {
        this.amenitybookingService = amenitybookingService;
        this.mapper = mapper;
        this.amenityService = amenityService;
    }

    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(summary = "Book an Amenity", description = "Create a new booking for an amenity")
    @ApiResponse(responseCode = "201", description = "Booking created successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Amenity not found")
    @PostMapping("/amenities/{amenityId}/tenants/{tenantId}/bookings")
    @JsonView(Views.AmenityBooking.class)
    public ResponseEntity<AmenityBookingDto> bookAmenity(
            @PathVariable @NotBlank String amenityId,
            @PathVariable @NotBlank String tenantId,
            @RequestBody @Parameter(description = "Amenity booking details") AmenityBookingDto amenityBookingDto) {

        logger.debug("Booking amenity {} for user {} from {} to {}", amenityId, amenityBookingDto.getUserId(),
                amenityBookingDto.getStartTime(), amenityBookingDto.getEndTime());
        Optional<Amenity> amenityOptional = amenityService.getAmenityById(amenityId);
        if (amenityOptional.isEmpty()) {
            logger.error("Amenity {} not found", amenityId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Amenity amenity = amenityOptional.get();
        validateBookingTime(amenity, amenityBookingDto.getStartTime(), amenityBookingDto.getEndTime());
        logger.debug("Booking constraints validated for amenity {}", amenityId);
        AmenityBooking amenityBooking = amenitybookingService.bookAmenity(amenity, tenantId, amenityBookingDto.getStartTime(), amenityBookingDto.getEndTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(amenityBooking));
    }

    private void validateBookingTime(Amenity amenity, LocalDateTime startTime, LocalDateTime endTime) {
        if (amenity.getMinBookingDuration() != null && ChronoUnit.MINUTES.between(startTime, endTime) < amenity.getMinBookingDuration()) {
            throw new BookingException("Booking period is less than the minimum allowed.");
        }
        if (amenity.getMaxBookingDuration() != null && ChronoUnit.MINUTES.between(startTime, endTime) > amenity.getMaxBookingDuration()) {
            throw new BookingException("Booking period is more than the maximum allowed.");
        }
    }

    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(summary = "Cancel an Amenity Booking", description = "Cancel an existing booking")
    @ApiResponse(responseCode = "204", description = "Booking cancelled successfully")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @DeleteMapping("/amenities/tenants/{tenantId}/bookings/{bookingId}")
    @JsonView(Views.AmenityBooking.class)
    public ResponseEntity<Void> cancelBooking(@PathVariable String bookingId, @PathVariable String tenantId) throws NotFoundResponseException {
        logger.debug("Cancelling booking {}", bookingId);
        amenitybookingService.cancelBooking(bookingId, tenantId);
        logger.debug("Booking {} cancelled successfully for {}", bookingId, tenantId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Get all bookings for an Amenity", description = "Retrieve all bookings for a specific amenity")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Amenity not found")
    @GetMapping("/amenities/{amenityId}/bookings")
    @JsonView(Views.AmenityBooking.class)
    public ResponseEntity<List<AmenityBookingDto>> getAllBookingsForAmenity(@PathVariable String amenityId) {
        logger.debug("Retrieving all bookings for amenity {}", amenityId);
        List<AmenityBooking> bookings = amenitybookingService.getAllBookingsForAmenity(amenityId);
        logger.debug("Found {} bookings for amenity {}", bookings.size(), amenityId);
        return ResponseEntity.ok(mapper.convertToDTOList(bookings));
    }

    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(summary = "Get booking for an Amenity", description = "Retrieve a bookings for a specific amenity")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Amenity not found")
    @GetMapping("/amenities/bookings/{bookingId}")
    @JsonView(Views.AmenityBooking.class)
    public ResponseEntity<AmenityBookingDto> getBookingForAmenity(
            @PathVariable String bookingId) {
        logger.debug("Retrieving booking ID {}", bookingId);
        try {
            AmenityBooking amenityBooking = amenitybookingService.getAmenityBooking(bookingId);
            logger.debug("Found booking {} - start time {} ", bookingId, amenityBooking.getStartTime());
            return ResponseEntity.ok(mapper.convertToDTO(amenityBooking));
        } catch (BookingException e) {
            logger.error("Error retrieving booking {}: {}", bookingId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(summary = "Check Amenity Availability", description = "Check if an amenity is available for a specific time range")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @GetMapping("/amenities/{amenityId}/availability")
    @JsonView(Views.AmenityBooking.class)
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable String amenityId,
            @RequestParam @NotBlank @FutureOrPresent LocalDateTime startTime,
            @RequestParam @NotBlank @Future LocalDateTime endTime) {
        logger.debug("Checking availability for amenity {} from {} to {}", amenityId, startTime, endTime);
        boolean isAvailable = amenitybookingService.isAvailable(amenityId, startTime, endTime);
        logger.debug("Amenity {} is available: {}", amenityId, isAvailable);
        return ResponseEntity.ok(isAvailable);
    }

    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(summary = "Update an Amenity Booking", description = "Update an existing booking")
    @ApiResponse(responseCode = "200", description = "Booking updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @PutMapping("/amenities/tenants/{tenantId}/bookings/{bookingId}")
    @JsonView(Views.AmenityBooking.class)
    public ResponseEntity<AmenityBookingDto> updateBooking(
            @PathVariable String bookingId,
            @PathVariable String tenantId,
            @RequestBody @Parameter(description = "Amenity booking details") AmenityBookingDto amenityBookingDto) {

        logger.debug("Updating booking {} for user {} from {} to {}", bookingId, tenantId,
                amenityBookingDto.getStartTime(), amenityBookingDto.getEndTime());
        AmenityBooking booking = amenitybookingService.getAmenityBooking(bookingId);
        if (booking == null) {
            logger.error("Booking {} not found", bookingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        validateBookingTime(booking.getAmenity(), amenityBookingDto.getStartTime(), amenityBookingDto.getEndTime());
        logger.debug("Booking constraints validated for booking {}", bookingId);
        AmenityBooking updatedBooking = amenitybookingService.updateBooking(booking, amenityBookingDto.getStartTime(), amenityBookingDto.getEndTime());
        return ResponseEntity.ok(mapper.convertToDTO(updatedBooking));
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Update an Amenity Booking Status", description = "Update an existing booking status")
    @ApiResponse(responseCode = "200", description = "Booking status updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @PutMapping("/amenities/tenants/{tenantId}/bookings/{bookingId}/status")
    @JsonView(Views.AmenityBooking.class)
    public ResponseEntity<AmenityBookingDto> updateBookingStatus(
            @PathVariable String bookingId,
            @PathVariable String tenantId,
            @RequestBody @Parameter(description = "Amenity booking status") AmenityBookingDto amenityBookingDto) {

        logger.debug("Updating booking status {} for user {}", bookingId, tenantId);
        AmenityBooking booking = amenitybookingService.getAmenityBooking(bookingId);
        if (booking == null) {
            logger.error("Booking {} not found", bookingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        booking.setStatus(amenityBookingDto.getStatus());
        AmenityBooking updatedBooking = amenitybookingService.updateBookingStatus(booking.getBookingId(), amenityBookingDto.getStatus());
        return ResponseEntity.ok(mapper.convertToDTO(updatedBooking));
    }


}

