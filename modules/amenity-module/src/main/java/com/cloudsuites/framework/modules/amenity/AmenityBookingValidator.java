package com.cloudsuites.framework.modules.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.CustomBookingCalendarRepositoryImpl;
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
    private final CustomBookingCalendarRepositoryImpl customBookingCalendarRepository;

    public AmenityBookingValidator(AmenityBookingRepository bookingRepository, CustomBookingCalendarRepositoryImpl customBookingCalendarRepository) {
        this.bookingRepository = bookingRepository;
        this.customBookingCalendarRepository = customBookingCalendarRepository;
    }

    public void validateBookingConstraints(Amenity amenity, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException {
        logger.debug("Validating booking constraints for Amenity ID: {} from {} to {}", amenity.getAmenityId(), startTime, endTime);
        if (startTime.toLocalDate().equals(endTime.toLocalDate())) {
            // Single-day booking
            logger.debug("Single-day booking detected.");
            if (!isAvailable(amenity, startTime, endTime)) {
                throw new BookingException("Amenity is not available during the requested time.");
            }

            if (!isWithinDailyAvailability(amenity, startTime, endTime)) {
                throw new BookingException("Booking time is outside of amenity's operating hours.");
            }

            checkBookingRequirements(amenity, startTime, endTime);
            checkBookingLimits(amenity, userId, startTime, endTime);
        } else {
            // Multi-day booking
            logger.debug("Multi-day booking detected.");
            validateMultiDayBooking(amenity, userId, startTime, endTime);
        }
    }

    private void validateMultiDayBooking(Amenity amenity, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException {
        logger.debug("Validating multi-day booking from {} to {}", startTime, endTime);

        LocalDateTime currentStartTime = startTime;
        LocalDateTime currentEndTime;

        while (!currentStartTime.toLocalDate().isAfter(endTime.toLocalDate())) {
            // Define end of the current day or endTime, whichever is earlier
            currentEndTime = currentStartTime.toLocalDate().atTime(LocalTime.MAX).isAfter(endTime) ? endTime : currentStartTime.toLocalDate().atTime(LocalTime.MAX);

            logger.debug("Validating day from {} to {}", currentStartTime, currentEndTime);

            // Validate each day
            if (!isAvailable(amenity, currentStartTime, currentEndTime)) {
                throw new BookingException("Amenity is not available during the requested time.");
            }

            if (!isWithinDailyAvailability(amenity, currentStartTime, currentEndTime)) {
                throw new BookingException("Booking time is outside of amenity's operating hours.");
            }

            currentStartTime = currentStartTime.toLocalDate().plusDays(1).atStartOfDay(); // Move to next day
        }

        // Check overall booking constraints
        checkBookingRequirements(amenity, startTime, endTime);
        checkBookingLimits(amenity, userId, startTime, endTime);
    }

    private void checkBookingRequirements(Amenity amenity, LocalDateTime startTime, LocalDateTime endTime) throws BookingException {
        logger.debug("Checking booking requirements for Amenity ID: {} from {} to {}", amenity.getAmenityId(), startTime, endTime);

        if (Boolean.FALSE.equals(amenity.getIsBookingRequired())) {
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
        logger.debug("Checking booking limits for Amenity ID: {} for user ID: {} from {} to {}", amenity.getAmenityId(), userId, startTime, endTime);

        if (amenity.getMaxBookingsPerTenant() != null) {
            LocalDateTime periodStart = calculateBookingLimitStartTime(amenity.getBookingLimitPeriod());
            int currentBookingCount = customBookingCalendarRepository.countBookingsForUser(userId, amenity.getAmenityId(), periodStart, endTime);

            if (currentBookingCount >= amenity.getMaxBookingsPerTenant()) {
                throw new BookingException("Booking limit reached for this amenity.");
            }
        }

        if (amenity.getMaxBookingOverlap() != null) {
            int overlappingCount = customBookingCalendarRepository.countOverlappingBookings(amenity.getAmenityId(), startTime, endTime);
            if (overlappingCount >= amenity.getMaxBookingOverlap()) {
                throw new BookingException("Maximum booking overlap reached.");
            }
        }
    }

    private boolean isWithinDailyAvailability(Amenity amenity, LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Checking daily availability for Amenity ID: {} on {}", amenity.getAmenityId(), startTime.toLocalDate());

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
            logger.debug("Booking time from {} to {} is outside of operating hours on {} - Open {} CLose {}", startLocalTime, endLocalTime, dayOfWeek, availability.getOpenTime(), availability.getCloseTime());
            return false;
        }

        return true;
    }

    private LocalDateTime calculateBookingLimitStartTime(BookingLimitPeriod period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime;
        switch (period) {
            case DAILY:
                startTime = now.truncatedTo(ChronoUnit.DAYS);
                logger.debug("Calculated DAILY booking limit start time: {}", startTime);
                break;
            case WEEKLY:
                startTime = now.minusDays(now.getDayOfWeek().getValue() - 1).truncatedTo(ChronoUnit.DAYS);
                logger.debug("Calculated WEEKLY booking limit start time: {}", startTime);
                break;
            case MONTHLY:
                startTime = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
                logger.debug("Calculated MONTHLY booking limit start time: {}", startTime);
                break;
            default:
                throw new IllegalArgumentException("Unsupported booking limit period: " + period);
        }
        return startTime;
    }

    public boolean isAvailable(Amenity amenity, LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Checking availability for Amenity ID: {} from {} to {}", amenity.getAmenityId(), startTime, endTime);

        List<AmenityBooking> overlappingBookings = customBookingCalendarRepository.findOverlappingBookings(
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
