package com.cloudsuites.framework.webapp.rest.amenity;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    public Mono<ResponseEntity<AmenityBookingDto>> bookAmenity(
            @PathVariable @NotBlank String amenityId,
            @PathVariable @NotBlank String tenantId,
            @RequestBody @Parameter(description = "Amenity booking details") AmenityBookingDto amenityBookingDto) {

        logger.debug("Booking amenity {} for user {} from {} to {}", amenityId, amenityBookingDto.getUserId(),
                amenityBookingDto.getStartTime(), amenityBookingDto.getEndTime());

        return Mono.fromCallable(() -> amenityService.getAmenityById(amenityId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(amenity -> {
                    validateBookingTime(amenity.get(), amenityBookingDto.getStartTime(), amenityBookingDto.getEndTime());
                    logger.debug("Booking constraints validated for amenity {}", amenityId);
                    return amenitybookingService.bookAmenity(amenity.get(), tenantId, amenityBookingDto.getStartTime(), amenityBookingDto.getEndTime())
                            .map(booking -> ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(booking)));
                })
                .onErrorResume(BookingException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
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
    public Mono<ResponseEntity<Void>> cancelBooking(@PathVariable String bookingId, @PathVariable String tenantId) {
        logger.debug("Cancelling booking {}", bookingId);
        return Mono.fromCallable(() -> amenitybookingService.cancelBooking(bookingId, tenantId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(booking -> ResponseEntity.noContent().<Void>build())
                .onErrorResume(NotFoundResponseException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }


    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Get all bookings for an Amenity", description = "Retrieve all bookings for a specific amenity")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Amenity not found")
    @GetMapping("/amenities/{amenityId}/bookings")
    @JsonView(Views.AmenityBooking.class)
    public Flux<ResponseEntity<AmenityBookingDto>> getAllBookingsForAmenity(@PathVariable String amenityId) {
        logger.debug("Retrieving all bookings for amenity {}", amenityId);
        return Flux.fromStream(() -> amenitybookingService.getAllBookingsForAmenity(amenityId).toStream())
                .subscribeOn(Schedulers.boundedElastic())
                .map(booking -> ResponseEntity.ok(mapper.convertToDTO(booking)))
                .onErrorResume(NotFoundResponseException.class, e -> Flux.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(summary = "Get booking for an Amenity", description = "Retrieve a booking for a specific amenity")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Amenity not found")
    @GetMapping("/amenities/bookings/{bookingId}")
    @JsonView(Views.AmenityBooking.class)
    public Mono<ResponseEntity<AmenityBookingDto>> getBookingForAmenity(
            @PathVariable String bookingId) {
        logger.debug("Retrieving booking ID {}", bookingId);
        return amenitybookingService.getAmenityBooking(bookingId)
                .map(booking -> ResponseEntity.ok(mapper.convertToDTO(booking)))
                .onErrorResume(BookingException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(summary = "Check Amenity Availability", description = "Check if an amenity is available for a specific time range")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @GetMapping("/amenities/{amenityId}/availability")
    @JsonView(Views.AmenityBooking.class)
    public Mono<ResponseEntity<Boolean>> checkAvailability(
            @PathVariable String amenityId,
            @RequestParam @NotBlank @FutureOrPresent LocalDateTime startTime,
            @RequestParam @NotBlank @Future LocalDateTime endTime) {
        logger.debug("Checking availability for amenity {} from {} to {}", amenityId, startTime, endTime);
        return amenitybookingService.isAvailable(amenityId, startTime, endTime)
                .map(isAvailable -> ResponseEntity.ok(isAvailable))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(summary = "Update an Amenity Booking", description = "Update an existing booking")
    @ApiResponse(responseCode = "200", description = "Booking updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @PutMapping("/amenities/tenants/{tenantId}/bookings/{bookingId}")
    @JsonView(Views.AmenityBooking.class)
    public Mono<ResponseEntity<AmenityBookingDto>> updateBooking(
            @PathVariable String bookingId,
            @PathVariable String tenantId,
            @RequestBody @Parameter(description = "Amenity booking details") AmenityBookingDto amenityBookingDto) {

        logger.debug("Updating booking {} for user {} from {} to {}", bookingId, tenantId,
                amenityBookingDto.getStartTime(), amenityBookingDto.getEndTime());
        return amenitybookingService.getAmenityBooking(bookingId)
                .flatMap(booking -> {
                    validateBookingTime(booking.getAmenity(), amenityBookingDto.getStartTime(), amenityBookingDto.getEndTime());
                    logger.debug("Booking constraints validated for booking {}", bookingId);
                    return amenitybookingService.updateBooking(booking, amenityBookingDto.getStartTime(), amenityBookingDto.getEndTime())
                            .map(updatedBooking -> ResponseEntity.ok(mapper.convertToDTO(updatedBooking)));
                })
                .onErrorResume(BookingException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Update an Amenity Booking Status", description = "Update an existing booking status")
    @ApiResponse(responseCode = "200", description = "Booking status updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @PutMapping("/amenities/bookings/{bookingId}/status")
    @JsonView(Views.AmenityBookingStaff.class)
    public Mono<ResponseEntity<AmenityBookingDto>> updateBookingStatus(
            @PathVariable String bookingId,
            @RequestBody @Parameter(description = "Amenity booking status") AmenityBookingDto amenityBookingDto) {

        logger.debug("Updating booking status {}", bookingId);
        return amenitybookingService.getAmenityBooking(bookingId)
                .flatMap(booking -> {
                    booking.setStatus(amenityBookingDto.getStatus());
                    return amenitybookingService.updateBookingStatus(booking.getBookingId(), amenityBookingDto.getStatus())
                            .map(updatedBooking -> ResponseEntity.ok(mapper.convertToDTO(updatedBooking)));
                })
                .onErrorResume(BookingException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }
}
