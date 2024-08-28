package com.cloudsuites.framework.modules.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.MaintenanceStatus;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingLimitPeriod;
import com.cloudsuites.framework.services.amenity.service.AmenityBookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class AmenityBookingServiceImpl implements AmenityBookingService {

    private final AmenityRepository amenityRepository;
    private final AmenityBookingRepository bookingRepository;
    private static final Logger logger = LoggerFactory.getLogger(AmenityBookingServiceImpl.class);

    public AmenityBookingServiceImpl(AmenityRepository amenityRepository, AmenityBookingRepository bookingRepository) {
        this.amenityRepository = amenityRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Async
    public CompletableFuture<AmenityBooking> asyncBookAmenity(String amenityId, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException {
        return CompletableFuture.completedFuture(bookAmenity(amenityId, userId, startTime, endTime));
    }

    @Override
    public AmenityBooking bookAmenity(String amenityId, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException {
        logger.debug("Booking amenity: {}", amenityId);

        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> {
                    logger.debug("Amenity not found.");
                    return new BookingException("Amenity not found.");
                });

        if (!isAvailable(amenity.getAmenityId(), startTime, endTime)) {
            logger.debug("Amenity is not available during the requested time.");
            throw new BookingException("Amenity is not available during the requested time.");
        }

        // Check tenant booking limit
        if (amenity.getMaxBookingsPerTenant() != null) {
            LocalDateTime periodStart = calculateBookingLimitStartTime(amenity.getBookingLimitPeriod());
            int currentBookingCount = bookingRepository.countBookingsForUser(userId, amenityId, periodStart, endTime);

            if (currentBookingCount >= amenity.getMaxBookingsPerTenant()) {
                logger.debug("Booking limit reached for this amenity.");
                throw new BookingException("Booking limit reached for this amenity.");
            }
        }

        AmenityBooking booking = new AmenityBooking();
        booking.setAmenity(amenity);
        booking.setUserId(userId);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        logger.debug("Booking created: {}", booking);

        return bookingRepository.save(booking);
    }

    @Override
    public void cancelBooking(String bookingId) throws BookingException {
        logger.debug("Cancelling booking: {}", bookingId);

        AmenityBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.debug("Booking not found.");
                    return new BookingException("Booking not found.");
                });

        bookingRepository.delete(booking);
        logger.debug("Booking cancelled: {}", bookingId);
    }

    @Override
    public boolean isAvailable(String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Checking amenity availability: {}", amenityId);
        Optional<Amenity> amenityOpt = amenityRepository.findById(amenityId);
        if (amenityOpt.isEmpty()) {
            logger.debug("Amenity not found.");
            throw new BookingException("Amenity not found.");
        }

        Amenity amenity = amenityOpt.get();
        // Fetch overlapping bookings using the optimized query
        List<AmenityBooking> overlappingBookings = bookingRepository.findOverlappingBookings(
                amenity.getAmenityId(), startTime, endTime
        );

        if (!overlappingBookings.isEmpty()) {
            logger.debug("Amenity is not available during the requested time.");
            return false; // Not available if there is an overlap
        }

        // Check additional constraints, if any
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
