package com.cloudsuites.framework.modules.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.DailyAvailability;
import com.cloudsuites.framework.services.amenity.entities.MaintenanceStatus;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingLimitPeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
public class AmenityBookingValidator {

    private static final Logger logger = LoggerFactory.getLogger(AmenityBookingValidator.class);
    private final AmenityBookingRepository bookingRepository;

    public AmenityBookingValidator(AmenityBookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void validateBookingConstraints(Amenity amenity, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException {
        if (!isAvailable(amenity, startTime, endTime)) {
            throw new BookingException("Amenity is not available during the requested time.");
        }

        if (!isWithinDailyAvailability(amenity, startTime, endTime)) {
            throw new BookingException("Booking time is outside of amenity's operating hours.");
        }

        checkBookingRequirements(amenity, startTime, endTime);
        checkBookingLimits(amenity, userId, startTime, endTime);
    }

    private void checkBookingRequirements(Amenity amenity, LocalDateTime startTime, LocalDateTime endTime) throws BookingException {
        if (!amenity.getIsBookingRequired()) {
            throw new BookingException("Booking is not required for this amenity.");
        }

        if (amenity.getAdvanceBookingPeriod() != null
                && startTime.isAfter(LocalDateTime.now().plusDays(amenity.getAdvanceBookingPeriod()))) {
            throw new BookingException("Booking is too far in advance.");
        }

        long bookingDurationMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
        if ((amenity.getMinBookingDuration() != null && bookingDurationMinutes < amenity.getMinBookingDuration())
                || (amenity.getBookingDurationLimit() != null && bookingDurationMinutes > amenity.getBookingDurationLimit())) {
            throw new BookingException("Booking duration is outside the allowed range.");
        }
    }

    private void checkBookingLimits(Amenity amenity, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException {
        if (amenity.getMaxBookingsPerTenant() != null) {
            LocalDateTime periodStart = calculateBookingLimitStartTime(amenity.getBookingLimitPeriod());
            int currentBookingCount = bookingRepository.countBookingsForUser(userId, amenity.getAmenityId(), periodStart, endTime);

            if (currentBookingCount >= amenity.getMaxBookingsPerTenant()) {
                throw new BookingException("Booking limit reached for this amenity.");
            }
        }

        if (amenity.getMaxBookingOverlap() != null) {
            int overlappingCount = bookingRepository.countOverlappingBookings(amenity.getAmenityId(), startTime, endTime);
            if (overlappingCount >= amenity.getMaxBookingOverlap()) {
                throw new BookingException("Maximum booking overlap reached.");
            }
        }
    }

    private boolean isWithinDailyAvailability(Amenity amenity, LocalDateTime startTime, LocalDateTime endTime) {
        List<DailyAvailability> dailyAvailabilities = amenity.getDailyAvailabilities();
        DayOfWeek dayOfWeek = startTime.getDayOfWeek();

        Optional<DailyAvailability> availabilityOpt = dailyAvailabilities.stream()
                .filter(a -> a.getDayOfWeek().equals(dayOfWeek))
                .findFirst();

        if (availabilityOpt.isEmpty()) {
            logger.debug("No availability defined for {}", dayOfWeek);
            return false;
        }

        DailyAvailability availability = availabilityOpt.get();
        LocalTime startLocalTime = startTime.toLocalTime();
        LocalTime endLocalTime = endTime.toLocalTime();

        if (startLocalTime.isBefore(availability.getOpenTime()) || endLocalTime.isAfter(availability.getCloseTime())) {
            logger.debug("Booking time from {} to {} is outside of operating hours on {}", startLocalTime, endLocalTime, dayOfWeek);
            return false;
        }

        return true;
    }

    private LocalDateTime calculateBookingLimitStartTime(BookingLimitPeriod period) {
        LocalDateTime now = LocalDateTime.now();
        switch (period) {
            case DAILY:
                return now.truncatedTo(ChronoUnit.DAYS);
            case WEEKLY:
                return now.minusDays(now.getDayOfWeek().getValue() - 1).truncatedTo(ChronoUnit.DAYS);
            case MONTHLY:
                return now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
            default:
                throw new IllegalArgumentException("Unsupported booking limit period: " + period);
        }
    }

    public boolean isAvailable(Amenity amenity, LocalDateTime startTime, LocalDateTime endTime) {
        List<AmenityBooking> overlappingBookings = bookingRepository.findOverlappingBookings(
                amenity.getAmenityId(), startTime, endTime
        );

        if (!overlappingBookings.isEmpty()) {
            logger.debug("Amenity is not available due to overlapping bookings.");
            return false;
        }

        if (amenity.getMaintenanceStatus() != MaintenanceStatus.OPERATIONAL) {
            logger.debug("Amenity is under maintenance.");
            return false;
        }

        return true;
    }
}