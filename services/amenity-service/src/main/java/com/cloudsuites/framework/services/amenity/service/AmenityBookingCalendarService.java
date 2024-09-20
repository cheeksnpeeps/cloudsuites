package com.cloudsuites.framework.services.amenity.service;

import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface AmenityBookingCalendarService {

    Flux<AmenityBooking> getBookingsForUser(List<String> userIds, List<String> amenityIds, List<BookingStatus> bookingStatuses, LocalDateTime startDate, LocalDateTime endDate);

    @Transactional(readOnly = true)
    Flux<AmenityBooking> getBookingsForAmenity(List<String> amenityIds, LocalDateTime startDate, LocalDateTime endDate);

    Flux<LocalDateTime> getAvailableSlotsForAmenity(String amenityId, LocalDateTime start, LocalDateTime end);
}
