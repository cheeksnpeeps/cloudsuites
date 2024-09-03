package com.cloudsuites.framework.modules.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.MaintenanceStatus;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingLimitPeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class AmenityBookingValidator {

    private static final Logger logger = LoggerFactory.getLogger(AmenityBookingValidator.class);
    private final AmenityBookingRepository bookingRepository;

    public AmenityBookingValidator(AmenityBookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void validateBookingConstraints(Amenity amenity, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException {
        if (!isAvailable(amenity, startTime, endTime)) {
            logger.debug("Amenity is not available during the requested time.");
            throw new BookingException("Amenity is not available during the requested time.");
        }

        // Check if booking is required
        if (!amenity.getIsBookingRequired()) {
            logger.debug("Booking is not required for this amenity.");
            throw new BookingException("Booking is not required for this amenity.");
        }

        // Check amenity opening and closing times
        LocalTime startLocalTime = startTime.toLocalTime();
        LocalTime endLocalTime = endTime.toLocalTime();
        if (startLocalTime.isBefore(amenity.getOpenTime()) || endLocalTime.isAfter(amenity.getCloseTime())) {
            logger.debug("Booking time is outside of amenity's operating hours.");
            throw new BookingException("Booking time is outside of amenity's operating hours.");
        }

        // Check advance booking period
        if (amenity.getAdvanceBookingPeriod() != null && startTime.isAfter(LocalDateTime.now().plusDays(amenity.getAdvanceBookingPeriod()))) {
            logger.debug("Booking is too far in advance.");
            throw new BookingException("Booking is too far in advance.");
        }

        // Check booking duration limits
        long bookingDurationMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
        if ((amenity.getMinimumBookingDuration() != null && bookingDurationMinutes < amenity.getMinimumBookingDuration())
                || (amenity.getBookingDurationLimit() != null && bookingDurationMinutes > amenity.getBookingDurationLimit())) {
            logger.debug("Booking duration is outside the allowed range.");
            throw new BookingException("Booking duration is outside the allowed range.");
        }

        // Check tenant booking limit
        if (amenity.getMaxBookingsPerTenant() != null) {
            LocalDateTime periodStart = calculateBookingLimitStartTime(amenity.getBookingLimitPeriod());
            int currentBookingCount = bookingRepository.countBookingsForUser(userId, amenity.getAmenityId(), periodStart, endTime);

            if (currentBookingCount >= amenity.getMaxBookingsPerTenant()) {
                logger.debug("Booking limit reached for this amenity.");
                throw new BookingException("Booking limit reached for this amenity.");
            }
        }

        // Check capacity
        if (amenity.getCapacity() != null) {
            int currentOccupancy = bookingRepository.countOccupancyDuringTimeRange(amenity.getAmenityId(), startTime, endTime);
            if (currentOccupancy >= amenity.getCapacity()) {
                logger.debug("Amenity capacity reached.");
                throw new BookingException("Amenity capacity reached.");
            }
        }
    }

    public boolean isAvailable(Amenity amenity, LocalDateTime startTime, LocalDateTime endTime) {
        // Fetch overlapping bookings using the optimized query
        List<AmenityBooking> overlappingBookings = bookingRepository.findOverlappingBookings(
                amenity.getAmenityId(), startTime, endTime
        );

        if (!overlappingBookings.isEmpty()) {
            logger.debug("Amenity is not available during the requested time.");
            return false; // Not available if there is an overlap
        }

        // Check if the amenity is under maintenance
        if (amenity.getMaintenanceStatus() != MaintenanceStatus.OPERATIONAL) {
            logger.debug("Amenity is under maintenance.");
            return false; // Not available if under maintenance
        }

        logger.debug("Amenity is available.");
        return true;
    }

    private LocalDateTime calculateBookingLimitStartTime(BookingLimitPeriod period) {
        LocalDateTime now = LocalDateTime.now();
        return switch (period) {
            case DAILY -> now.truncatedTo(ChronoUnit.DAYS);
            case WEEKLY -> now.minusDays(now.getDayOfWeek().getValue() - 1).truncatedTo(ChronoUnit.DAYS);
            case MONTHLY -> now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
            default -> {
                logger.error("Unsupported booking limit period: {}", period);
                throw new IllegalArgumentException("Unsupported booking limit period: " + period);
            }
        };
    }
}

