package com.cloudsuites.framework.services.amenity.service;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface AmenityBookingService {

    @Transactional
    AmenityBooking bookAmenity(Amenity amenity, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException;

    @Transactional
    void cancelBooking(String bookingId, String tenantId) throws BookingException, NotFoundResponseException;

    @Transactional
    boolean isAvailable(String amenityId, LocalDateTime startTime, LocalDateTime endTime);

    @Transactional
    List<AmenityBooking> getAllBookingsForAmenity(String amenityId);

    @Transactional
    AmenityBooking getAmenityBooking(String bookingId);

    @Transactional
    AmenityBooking updateBooking(String bookingId, LocalDateTime newStartTime, LocalDateTime newEndTime) throws BookingException;

}