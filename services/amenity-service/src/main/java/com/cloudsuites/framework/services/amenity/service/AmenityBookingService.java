package com.cloudsuites.framework.services.amenity.service;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;

import java.time.LocalDateTime;

public interface AmenityBookingService {

    AmenityBooking bookAmenity(String amenityId, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException;

    void cancelBooking(String bookingId) throws BookingException;

    boolean isAvailable(Amenity amenity, LocalDateTime startTime, LocalDateTime endTime);
}
