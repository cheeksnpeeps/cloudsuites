package com.cloudsuites.framework.services.amenity.service;

import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface AmenityBookingService {

    @Async
    CompletableFuture<AmenityBooking> asyncBookAmenity(String amenityId, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException;

    AmenityBooking bookAmenity(String amenityId, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException;

    void cancelBooking(String bookingId) throws BookingException;

    boolean isAvailable(String amenityId, LocalDateTime startTime, LocalDateTime endTime);
}
