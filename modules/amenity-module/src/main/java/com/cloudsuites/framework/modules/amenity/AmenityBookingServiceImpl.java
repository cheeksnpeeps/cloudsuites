package com.cloudsuites.framework.modules.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.modules.amenity.repository.CustomBookingCalendarRepositoryImpl;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingStatus;
import com.cloudsuites.framework.services.amenity.service.AmenityBookingService;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AmenityBookingServiceImpl implements AmenityBookingService {

    private final AmenityRepository amenityRepository;
    private final AmenityBookingRepository bookingRepository;
    private final AmenityBookingValidator bookingValidator;
    // Self injection

    private static final Logger logger = LoggerFactory.getLogger(AmenityBookingServiceImpl.class);
    private final CustomBookingCalendarRepositoryImpl customBookingCalendarRepository;

    public AmenityBookingServiceImpl(AmenityRepository amenityRepository, AmenityBookingRepository bookingRepository, AmenityBookingValidator bookingValidator, CustomBookingCalendarRepositoryImpl customBookingCalendarRepository) {
        this.amenityRepository = amenityRepository;
        this.bookingRepository = bookingRepository;
        this.bookingValidator = bookingValidator;
        this.customBookingCalendarRepository = customBookingCalendarRepository;
    }

    @Override
    public AmenityBooking bookAmenity(Amenity amenity, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException {
        logger.debug("Attempting to lock amenity with ID: {}", amenity.getAmenityId());
        // Lock the amenity to handle concurrency issues
        Amenity lockedAmenity = amenityRepository.lockAmenityForBooking(amenity.getAmenityId());
        if (lockedAmenity == null) {
            logger.error("No amenity found for ID: {}", amenity.getAmenityId());
            throw new BookingException("Amenity not found for locking.");
        }
        logger.debug("Amenity with ID: {} locked for booking.", lockedAmenity.getAmenityId());
        logger.debug("Attempting to book amenity with ID: {} for user: {} from {} to {}", amenity.getAmenityId(), userId, startTime, endTime);
        // Validate booking constraints
        bookingValidator.validateBookingConstraints(amenity, userId, startTime, endTime);
        logger.debug("Booking constraints validated for amenity with ID: {}.", amenity.getAmenityId());

        // Create and save the booking
        AmenityBooking booking = new AmenityBooking();
        booking.setAmenity(amenity);
        booking.setUserId(userId);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);

        // Save the booking
        AmenityBooking savedBooking = bookingRepository.save(booking);
        logger.debug("Booking with ID: {} saved successfully.", savedBooking.getBookingId());
        return savedBooking;
    }

    @Override
    public void cancelBooking(String bookingId, String tenantId) throws BookingException, NotFoundResponseException {
        logger.debug("Attempting to cancel booking with ID: {} for tenant: {}", bookingId, tenantId);

        AmenityBooking booking = bookingRepository.findByBookingIdAndUserId(bookingId, tenantId)
                .orElseThrow(() -> {
                    logger.debug("Booking with ID: {} not found for tenant: {}", bookingId, tenantId);
                    return new NotFoundResponseException("Booking " + bookingId + " not found for tenant " + tenantId);
                });

        bookingRepository.delete(booking);
        logger.debug("Booking with ID: {} cancelled successfully.", bookingId);
    }

    @Override
    public boolean isAvailable(String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Checking availability for amenity with ID: {} from {} to {}", amenityId, startTime, endTime);

        Optional<Amenity> amenityOpt = amenityRepository.findById(amenityId);
        if (amenityOpt.isEmpty()) {
            logger.debug("Amenity with ID: {} not found.", amenityId);
            throw new BookingException("Amenity not found.");
        }

        Amenity amenity = amenityOpt.get();
        boolean available = bookingValidator.isAvailable(amenity, startTime, endTime);
        logger.debug("Amenity with ID: {} availability check result: {}", amenityId, available);
        return available;
    }

    @Override
    public List<AmenityBooking> getAllBookingsForAmenity(String amenityId) {
        logger.debug("Retrieving all bookings for amenity with ID: {}", amenityId);
        List<AmenityBooking> bookings = customBookingCalendarRepository.findByAmenity_AmenityId(amenityId);
        logger.debug("Found {} bookings for amenity with ID: {}", bookings.size(), amenityId);
        return bookings;
    }

    @Override
    public AmenityBooking getAmenityBooking(String bookingId) {
        logger.debug("Retrieving booking with ID: {}", bookingId);
        AmenityBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.debug("Booking with ID: {} not found.", bookingId);
                    return new BookingException("Booking not found.");
                });
        logger.debug("Retrieved booking: {}", booking);
        return booking;
    }

    @Override
    @Transactional
    public AmenityBooking updateBooking(AmenityBooking booking, LocalDateTime newStartTime, LocalDateTime newEndTime) throws BookingException {
        logger.debug("Attempting to update booking with ID: {} to new times from {} to {}", booking.getBookingId(), newStartTime, newEndTime);

        // Lock the amenity to handle concurrency issues
        Amenity amenity = booking.getAmenity();
        Amenity lockedAmenity = amenityRepository.lockAmenityForBooking(amenity.getAmenityId());
        logger.debug("Amenity with ID: {} locked for booking update.", lockedAmenity.getAmenityId());

        // Validate booking constraints for the updated times
        bookingValidator.validateBookingConstraints(lockedAmenity, booking.getUserId(), newStartTime, newEndTime);
        logger.debug("Booking constraints validated for updated times.");

        // Update booking details
        booking.setStartTime(newStartTime);
        booking.setEndTime(newEndTime);

        // Save the updated booking
        AmenityBooking updatedBooking = bookingRepository.save(booking);
        logger.debug("Booking updated successfully: {}", updatedBooking);

        return updatedBooking;
    }

    @Override
    public AmenityBooking updateBookingStatus(String bookingId, BookingStatus status) {
        logger.debug("Approving booking with ID: {}", bookingId);
        AmenityBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.debug("Booking with ID: {} not found.", bookingId);
                    return new BookingException("Booking not found.");
                });
        booking.setStatus(status);
        AmenityBooking savedBooking = bookingRepository.save(booking);
        logger.debug("Booking with ID: {} approved successfully.", bookingId);
        return savedBooking;
    }
}
