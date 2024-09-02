package com.cloudsuites.framework.webapp.rest.amenity;

import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;
import com.cloudsuites.framework.services.amenity.service.AmenityBookingService;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityBookingDto;
import com.cloudsuites.framework.webapp.rest.amenityBooking.mapper.AmenityBookingMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/")
@Tags(value = {@Tag(name = "Amenity Bookings", description = "Operations related to amenity bookings")})
public class AmenityBookingRestController {

    private static final Logger logger = LoggerFactory.getLogger(AmenityBookingRestController.class);

    private final AmenityBookingService amenitybookingService;
    private final AmenityBookingMapper mapper;

    @Autowired
    public AmenityBookingRestController(AmenityBookingService amenitybookingService,
                                        AmenityBookingMapper mapper) {
        this.amenitybookingService = amenitybookingService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(summary = "Book an Amenity", description = "Create a new booking for an amenity")
    @ApiResponse(responseCode = "201", description = "Booking created successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Amenity not found")
    @PostMapping("/amenities/{amenityId}/bookings")
    public CompletableFuture<ResponseEntity<AmenityBookingDto>> bookAmenity(
            @PathVariable @NotBlank String amenityId,
            @Valid @RequestBody @Parameter(description = "Amenity booking details") AmenityBookingDto amenityBookingDto) {
        logger.debug("Booking amenity {} for user {} from {} to {}", amenityId, amenityBookingDto.getUserId(),
                amenityBookingDto.getStartTime(), amenityBookingDto.getEndTime());
        return amenitybookingService.asyncBookAmenity(amenityId,
                        amenityBookingDto.getUserId(), amenityBookingDto.getStartTime(), amenityBookingDto.getEndTime())
                .thenApply(booking -> ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(booking)))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof BookingException) {
                        logger.error("Booking failed: {}", ex.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(summary = "Cancel an Amenity Booking", description = "Cancel an existing booking")
    @ApiResponse(responseCode = "204", description = "Booking cancelled successfully")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @DeleteMapping("/amenities/bookings/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable String bookingId) {
        logger.debug("Cancelling booking {}", bookingId);
        try {
            amenitybookingService.cancelBooking(bookingId);
            logger.debug("Booking {} cancelled successfully", bookingId);
            return ResponseEntity.noContent().build();
        } catch (BookingException e) {
            logger.error("Booking cancellation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(summary = "Get all bookings for an Amenity", description = "Retrieve all bookings for a specific amenity")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Amenity not found")
    @GetMapping("/amenities/{amenityId}/bookings")
    public ResponseEntity<List<AmenityBookingDto>> getAllBookingsForAmenity(@PathVariable String amenityId) {
        logger.debug("Retrieving all bookings for amenity {}", amenityId);
        try {
            List<AmenityBooking> bookings = amenitybookingService.getAllBookingsForAmenity(amenityId);
            logger.debug("Found {} bookings for amenity {}", bookings.size(), amenityId);
            return ResponseEntity.ok(mapper.convertToDTOList(bookings));
        } catch (BookingException e) {
            logger.error("Error retrieving bookings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(summary = "Check Amenity Availability", description = "Check if an amenity is available for a specific time range")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @GetMapping("/amenities/{amenityId}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable String amenityId,
            @RequestParam @NotBlank @FutureOrPresent LocalDateTime startTime,
            @RequestParam @NotBlank @Future LocalDateTime endTime) {
        logger.debug("Checking availability for amenity {} from {} to {}", amenityId, startTime, endTime);
        boolean isAvailable = amenitybookingService.isAvailable(amenityId, startTime, endTime);
        logger.debug("Amenity {} is available: {}", amenityId, isAvailable);
        return ResponseEntity.ok(isAvailable);
    }
}

