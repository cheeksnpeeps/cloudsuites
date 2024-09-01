package com.cloudsuites.framework.modules.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.service.AmenityBookingCalendarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class AmenityBookingCalendarServiceImpl implements AmenityBookingCalendarService {

    private final AmenityBookingRepository bookingRepository;
    private final AmenityRepository amenityRepository;

    private static final Logger logger = LoggerFactory.getLogger(AmenityBookingCalendarServiceImpl.class);

    public AmenityBookingCalendarServiceImpl(AmenityBookingRepository bookingRepository, AmenityRepository amenityRepository) {
        this.bookingRepository = bookingRepository;
        this.amenityRepository = amenityRepository;
    }

    private static Set<LocalDateTime> getLocalDateTimes(List<AmenityBooking> bookings) {
        Set<LocalDateTime> bookedSlots = new HashSet<>();
        logger.debug("Calculating booked slots from {} bookings.", bookings.size());

        // Mark all booked slots
        for (AmenityBooking booking : bookings) {
            LocalDateTime slotTime = booking.getStartTime().truncatedTo(ChronoUnit.HOURS);
            while (!slotTime.isAfter(booking.getEndTime().minusMinutes(1))) { // Exclude end time itself
                bookedSlots.add(slotTime);
                slotTime = slotTime.plusHours(1);
            }
        }
        logger.debug("Found {} booked slots.", bookedSlots.size());
        return bookedSlots;
    }

    @Override
    public List<AmenityBooking> getBookingsForUser(String userId, AmenityType amenityType, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Fetching bookings for userId: {}, amenityType: {}, startDate: {}, endDate: {}",
                userId, amenityType, startDate, endDate);
        List<AmenityBooking> bookings = bookingRepository.findByUserIdAndFilters(userId, amenityType, startDate, endDate);
        logger.debug("Found {} bookings for userId: {}, amenityType: {}", bookings.size(), userId, amenityType);
        return bookings;
    }

    @Override
    public List<AmenityBooking> getBookingsForAmenity(String amenityId, LocalDateTime start, LocalDateTime end) {
        logger.info("Fetching bookings for amenityId: {}, start: {}, end: {}", amenityId, start, end);
        List<AmenityBooking> bookings = bookingRepository.findByAmenityIdAndTimeRange(amenityId, start, end);
        logger.debug("Found {} bookings for amenityId: {}", bookings.size(), amenityId);
        return bookings;
    }

    @Override
    public List<LocalDateTime> getAvailableSlotsForAmenity(String amenityId, LocalDateTime start, LocalDateTime end) {
        logger.info("Calculating available slots for amenityId: {}, start: {}, end: {}", amenityId, start, end);
        List<AmenityBooking> bookings = bookingRepository.findByAmenityIdAndTimeRange(amenityId, start, end);
        Amenity amenity = amenityRepository.findById(amenityId).orElseThrow(() -> {
            logger.error("Amenity with id {} not found.", amenityId);
            return new RuntimeException("Amenity not found");
        });

        List<LocalDateTime> availableSlots = calculateAvailableSlots(amenity, bookings, start, end);
        logger.debug("Found {} available slots for amenityId: {}", availableSlots.size(), amenityId);
        return availableSlots;
    }

    List<LocalDateTime> calculateAvailableSlots(Amenity amenity, List<AmenityBooking> bookings, LocalDateTime start, LocalDateTime end) {
        List<LocalDateTime> availableSlots = new ArrayList<>();
        logger.debug("Calculating available slots based on {} bookings.", bookings.size());

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

        logger.debug("Calculated {} available slots.", availableSlots.size());
        return availableSlots;
    }
}
