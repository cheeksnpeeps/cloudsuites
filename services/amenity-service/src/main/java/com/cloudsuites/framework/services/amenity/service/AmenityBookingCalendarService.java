package com.cloudsuites.framework.services.amenity.service;

import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface AmenityBookingCalendarService {

    @Transactional(readOnly = true)
    List<AmenityBooking> getBookingsForUser(String userId, AmenityType amenityType, LocalDateTime startDate, LocalDateTime endDate);

    @Transactional(readOnly = true)
    List<AmenityBooking> getBookingsForAmenity(String amenityId, LocalDateTime start, LocalDateTime end);

    @Transactional(readOnly = true)
    List<LocalDateTime> getAvailableSlotsForAmenity(String amenityId, LocalDateTime start, LocalDateTime end);
}
