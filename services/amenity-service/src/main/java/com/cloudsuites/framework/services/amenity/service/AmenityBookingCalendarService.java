package com.cloudsuites.framework.services.amenity.service;

import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface AmenityBookingCalendarService {

    // Retrieve bookings based on user and optional filters
    List<AmenityBooking> getBookingsForUser(String userId, String amenityType, LocalDateTime startDate, LocalDateTime endDate);

    // Retrieve all bookings for a specific amenity
    List<AmenityBooking> getBookingsForAmenity(String amenityId, LocalDateTime start, LocalDateTime end);

    // Retrieve all available slots for an amenity
    List<LocalDateTime> getAvailableSlotsForAmenity(String amenityId, LocalDateTime start, LocalDateTime end);
}
