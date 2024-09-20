package com.cloudsuites.framework.services.amenity.service;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingStatus;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface AmenityBookingService {


    @Transactional
    Mono<AmenityBooking> bookAmenity(Amenity amenity, String userId, LocalDateTime startTime, LocalDateTime endTime);

    @Transactional
    Mono<Void> cancelBooking(String bookingId, String tenantId);

    @Transactional
    Mono<Boolean> isAvailable(String amenityId, LocalDateTime startTime, LocalDateTime endTime);

    @Transactional
    Flux<AmenityBooking> getAllBookingsForAmenity(String amenityId);

    @Transactional
    Mono<AmenityBooking> getAmenityBooking(String bookingId);

    @Transactional
    Mono<AmenityBooking> updateBooking(AmenityBooking booking, LocalDateTime newStartTime, LocalDateTime newEndTime);

    @Transactional
    Mono<AmenityBooking> updateBookingStatus(String bookingId, BookingStatus status);
}