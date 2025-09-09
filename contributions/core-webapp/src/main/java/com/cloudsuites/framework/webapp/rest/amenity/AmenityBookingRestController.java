package com.cloudsuites.framework.webapp.rest.amenity;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;
import com.cloudsuites.framework.services.amenity.service.AmenityBookingService;
import com.cloudsuites.framework.services.amenity.service.AmenityService;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Tags(@Tag(name = "Amenity Bookings", description = "Operations related to amenity bookings"))
public class AmenityBookingRestController {

    private static final Logger logger = LoggerFactory.getLogger(AmenityBookingRestController.class);

    private final AmenityBookingService bookingService;
    private final AmenityBookingMapper mapper;
    private final AmenityService amenityService;

    public AmenityBookingRestController(AmenityBookingService bookingService,
                                        AmenityBookingMapper mapper,
                                        AmenityService amenityService) {
        this.bookingService = bookingService;
        this.mapper = mapper;
        this.amenityService = amenityService;
    }

    public record CreateBookingRequest(
            @NotNull @FutureOrPresent OffsetDateTime startTime,
            @NotNull @Future OffsetDateTime endTime
    ) {}


    // ------------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER') or hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Book an Amenity", description = "Create a new booking for an amenity")
    @PostMapping("/amenities/{amenityId}/tenants/{tenantId}/bookings")
    @JsonView(Views.AmenityBooking.class)
    public ResponseEntity<AmenityBookingDto> bookAmenity(
            @PathVariable @NotNull String amenityId,
            @PathVariable @NotNull String tenantId,
            @Valid @RequestBody @Parameter(description = "Amenity booking details") CreateBookingRequest body) {

        logger.debug("Booking amenity {} for tenant {} from {} to {}", amenityId, tenantId, body.startTime(), body.endTime());

        Optional<Amenity> amenityOpt = amenityService.getAmenityById(amenityId);
        if (amenityOpt.isEmpty()) {
            logger.debug("Amenity not found with ID: {}", amenityId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Amenity amenity = amenityOpt.get();
        try {
            LocalDateTime start = body.startTime().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime end   = body.endTime().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();

            validateBookingTime(amenity, start, end);
            AmenityBooking booking = bookingService.bookAmenitySync(
                    amenity,
                    tenantId,
                    start,
                    end
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(booking));
        } catch (BookingException e) {
            logger.warn("Booking failed for amenity {}: {}", amenityId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Unexpected error creating booking for amenity {}: {}", amenityId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ------------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER') or hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Cancel an Amenity Booking", description = "Cancel an existing booking")
    @ApiResponse(responseCode = "204", description = "Booking cancelled successfully")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @DeleteMapping("/amenities/tenants/{tenantId}/bookings/{bookingId}")
    @JsonView(Views.AmenityBooking.class)
    public ResponseEntity<Void> cancelBooking(@PathVariable String bookingId, @PathVariable String tenantId) {
        logger.debug("Cancelling booking {} for tenant {}", bookingId, tenantId);
        try {
            bookingService.cancelBookingSync(bookingId, tenantId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Unexpected error cancelling booking {}: {}", bookingId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ------------------------------------------------------------------------
    // READ — list by amenity
    // ------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER') or hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Get all bookings for an Amenity", description = "Retrieve all bookings for a specific amenity")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Amenity not found")
    @GetMapping("/amenities/{amenityId}/bookings")
    @JsonView(Views.AmenityBooking.class)
    public ResponseEntity<List<AmenityBookingDto>> getAllBookingsForAmenity(@PathVariable String amenityId) {
        logger.debug("Retrieving all bookings for amenity {}", amenityId);
        try {
            var bookings = bookingService.getAllBookingsForAmenitySync(amenityId);
            var dtos = bookings.stream().map(mapper::convertToDTO).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Unexpected error listing bookings for amenity {}: {}", amenityId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ------------------------------------------------------------------------
    // READ — single booking
    // ------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER') or hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Get booking for an Amenity", description = "Retrieve a booking for a specific amenity")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @GetMapping("/amenities/bookings/{bookingId}")
    @JsonView(Views.AmenityBooking.class)
    public ResponseEntity<AmenityBookingDto> getBookingForAmenity(@PathVariable String bookingId) {
        logger.debug("Retrieving booking ID {}", bookingId);
        try {
            var booking = bookingService.getAmenityBookingSync(bookingId);
            return ResponseEntity.ok(mapper.convertToDTO(booking));
        } catch (BookingException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Unexpected error fetching booking {}: {}", bookingId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ------------------------------------------------------------------------
    // READ — availability
    // ------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER') or hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Check Amenity Availability", description = "Check if an amenity is available for a specific time range")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping("/amenities/{amenityId}/availability")
    @JsonView(Views.AmenityBooking.class)
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable String amenityId,
            @RequestParam @NotNull @FutureOrPresent OffsetDateTime startTime,
            @RequestParam @NotNull @Future OffsetDateTime endTime) {

        logger.debug("Checking availability for amenity {} from {} to {}", amenityId, startTime, endTime);

        if (!startTime.isBefore(endTime)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Service can accept OffsetDateTime or LocalDateTime; align with your impl
            boolean isAvailable = bookingService.isAvailableSync(
                    amenityId,
                    startTime.toLocalDateTime(),
                    endTime.toLocalDateTime()
            );
            return ResponseEntity.ok(isAvailable);
        } catch (Exception e) {
            logger.error("Error checking availability for amenity {}: {}", amenityId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ------------------------------------------------------------------------
    // UPDATE — window
    // ------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER') or hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Update an Amenity Booking", description = "Update an existing booking")
    @ApiResponse(responseCode = "200", description = "Booking updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @PutMapping("/amenities/tenants/{tenantId}/bookings/{bookingId}")
    @JsonView(Views.AmenityBooking.class)
    public ResponseEntity<AmenityBookingDto> updateBooking(
            @PathVariable String bookingId,
            @PathVariable String tenantId,
            @Valid @RequestBody AmenityBookingDto body) {

        logger.debug("Updating booking {} for tenant {} to {} → {}", bookingId, tenantId, body.getStartTime(), body.getEndTime());

        try {
            var existing = bookingService.getAmenityBookingSync(bookingId);
            
            LocalDateTime start = body.getStartTime().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime end   = body.getEndTime().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();

            validateBookingTime(existing.getAmenity(), start, end);

            var updated = bookingService.updateBookingSync(existing, start, end);
            return ResponseEntity.ok(mapper.convertToDTO(updated));
        } catch (BookingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Unexpected error updating booking {}: {}", bookingId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ------------------------------------------------------------------------
    // UPDATE — status
    // ------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER') or hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Update an Amenity Booking Status", description = "Update an existing booking status")
    @ApiResponse(responseCode = "200", description = "Booking status updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @PutMapping("/amenities/bookings/{bookingId}/status")
    @JsonView(Views.AmenityBookingStaff.class)
    public ResponseEntity<AmenityBookingDto> updateBookingStatus(
            @PathVariable String bookingId,
            @Valid @RequestBody AmenityBookingDto body) {

        logger.debug("Updating booking status {} -> {}", bookingId, body.getStatus());
        try {
            var updated = bookingService.updateBookingStatusSync(bookingId, body.getStatus());
            return ResponseEntity.ok(mapper.convertToDTO(updated));
        } catch (BookingException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Unexpected error updating booking status {}: {}", bookingId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ------------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------------
    private void validateBookingTime(Amenity amenity, LocalDateTime startTime, LocalDateTime endTime) throws BookingException {
        if (!startTime.isBefore(endTime)) {
            throw new BookingException("Start must be before end.");
        }
        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        Integer min = amenity.getMinBookingDuration();
        Integer max = amenity.getMaxBookingDuration();

        if (min != null && minutes < min) {
            throw new BookingException("Booking period is less than the minimum allowed.");
        }
        if (max != null && minutes > max) {
            throw new BookingException("Booking period is more than the maximum allowed.");
        }
    }
}
