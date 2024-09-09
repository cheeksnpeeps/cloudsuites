package com.cloudsuites.framework.services.amenity.service;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AmenityBookingService {

    @Async
    CompletableFuture<AmenityBooking> asyncBookAmenity(Amenity amenity, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException;

    @Transactional
    AmenityBooking bookAmenity(Amenity amenity, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException;

    @Transactional
    void cancelBooking(String bookingId) throws BookingException;

    @Transactional
    boolean isAvailable(String amenityId, LocalDateTime startTime, LocalDateTime endTime);

    @Transactional
    List<AmenityBooking> getAllBookingsForAmenity(String amenityId);

    @Transactional
    AmenityBooking getAmenityBooking(String bookingId);

    @Transactional
    AmenityBooking updateBooking(String bookingId, LocalDateTime newStartTime, LocalDateTime newEndTime) throws BookingException;

}