package com.cloudsuites.framework.modules.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.service.AmenityBookingCalendarService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AmenityBookingCalendarServiceImpl implements AmenityBookingCalendarService {

    private final AmenityBookingRepository bookingRepository;
    private final AmenityRepository amenityRepository;

    public AmenityBookingCalendarServiceImpl(AmenityBookingRepository bookingRepository, AmenityRepository amenityRepository) {
        this.bookingRepository = bookingRepository;
        this.amenityRepository = amenityRepository;
    }

    private static Set<LocalDateTime> getLocalDateTimes(List<AmenityBooking> bookings) {
        Set<LocalDateTime> bookedSlots = new HashSet<>();

        // Mark all booked slots
        for (AmenityBooking booking : bookings) {
            LocalDateTime slotTime = booking.getStartTime().truncatedTo(ChronoUnit.HOURS);
            while (!slotTime.isAfter(booking.getEndTime().minusMinutes(1))) { // Exclude end time itself
                bookedSlots.add(slotTime);
                slotTime = slotTime.plusHours(1);
            }
        }
        return bookedSlots;
    }

    @Override
    public List<AmenityBooking> getBookingsForUser(String userId, String amenityType, LocalDateTime startDate, LocalDateTime endDate) {
        // Fetch bookings from repository based on user and filters
        return bookingRepository.findByUserIdAndFilters(userId, amenityType, startDate, endDate);
    }

    @Override
    public List<AmenityBooking> getBookingsForAmenity(String amenityId, LocalDateTime start, LocalDateTime end) {
        // Fetch bookings from repository based on amenityId and time range
        return bookingRepository.findByAmenityIdAndTimeRange(amenityId, start, end);
    }

    @Override
    public List<LocalDateTime> getAvailableSlotsForAmenity(String amenityId, LocalDateTime start, LocalDateTime end) {
        // Fetch all bookings and calculate available slots
        List<AmenityBooking> bookings = bookingRepository.findByAmenityIdAndTimeRange(amenityId, start, end);
        // Logic to determine available slots based on existing bookings
        Amenity amenity = amenityRepository.findById(amenityId).orElseThrow();
        return calculateAvailableSlots(amenity, bookings, start, end);
    }

    private List<LocalDateTime> calculateAvailableSlots(Amenity amenity, List<AmenityBooking> bookings, LocalDateTime start, LocalDateTime end) {
        List<LocalDateTime> availableSlots = new ArrayList<>();

        // Get opening and closing hours from the amenity entity
        LocalTime openingHour = amenity.getOpenTime();
        LocalTime closingHour = amenity.getCloseTime();

        // Create a set to hold all booked slots for quick lookup
        Set<LocalDateTime> bookedSlots = getLocalDateTimes(bookings);

        // Generate all possible hourly slots within the start and end time
        LocalDateTime slotStartTime = start.truncatedTo(ChronoUnit.HOURS);
        while (!slotStartTime.isAfter(end.minusHours(1))) {
            LocalTime localTime = slotStartTime.toLocalTime();

            // Check if the slot is within the amenity's opening hours and not booked
            if (!localTime.isBefore(openingHour) && !localTime.isAfter(closingHour.minusHours(1))
                    && !bookedSlots.contains(slotStartTime)) {
                availableSlots.add(slotStartTime);
            }

            slotStartTime = slotStartTime.plusHours(1);
        }

        return availableSlots;
    }

}

