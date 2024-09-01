package com.cloudsuites.framework.services.amenity.service;

import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public interface AmenityBookingService {

    @Async
    CompletableFuture<AmenityBooking> asyncBookAmenity(String amenityId, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException;

    @Transactional
    AmenityBooking bookAmenity(String amenityId, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException;

    @Transactional
    void cancelBooking(String bookingId) throws BookingException;

    @Transactional
    boolean isAvailable(String amenityId, LocalDateTime startTime, LocalDateTime endTime);
}
