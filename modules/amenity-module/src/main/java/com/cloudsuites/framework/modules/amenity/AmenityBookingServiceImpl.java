package com.cloudsuites.framework.modules.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;
import com.cloudsuites.framework.services.amenity.service.AmenityBookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class AmenityBookingServiceImpl implements AmenityBookingService {

    private final AmenityRepository amenityRepository;
    private final AmenityBookingRepository bookingRepository;
    private final AmenityBookingValidator bookingValidator;
    private static final Logger logger = LoggerFactory.getLogger(AmenityBookingServiceImpl.class);

    public AmenityBookingServiceImpl(AmenityRepository amenityRepository, AmenityBookingRepository bookingRepository, AmenityBookingValidator bookingValidator) {
        this.amenityRepository = amenityRepository;
        this.bookingRepository = bookingRepository;
        this.bookingValidator = bookingValidator;
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<AmenityBooking> asyncBookAmenity(Amenity amenity, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException {
        return CompletableFuture.supplyAsync(() -> {
            logger.debug("Booking amenity: {}", amenity.getAmenityId());

            // Lock the amenity to handle concurrency issues
            Amenity lockedAmenity = amenityRepository.lockAmenityForBooking(amenity.getAmenityId());

            // Validate booking constraints
            bookingValidator.validateBookingConstraints(lockedAmenity, userId, startTime, endTime);

            // Create and save the booking
            AmenityBooking booking = new AmenityBooking();
            booking.setAmenity(lockedAmenity);
            booking.setUserId(userId);
            booking.setStartTime(startTime);
            booking.setEndTime(endTime);

            // Save the booking
            AmenityBooking savedBooking = bookingRepository.save(booking);
            logger.debug("Async booking completed: {}", savedBooking);

            return savedBooking;
        });
    }

    @Override
    public AmenityBooking bookAmenity(Amenity amenity, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException {
        return asyncBookAmenity(amenity, userId, startTime, endTime).join();
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
        return bookingValidator.isAvailable(amenity, startTime, endTime);
    }

    @Override
    public List<AmenityBooking> getAllBookingsForAmenity(String amenityId) {
        logger.debug("Retrieving all bookings for amenity: {}", amenityId);
        return bookingRepository.findByAmenity_AmenityId(amenityId);
    }

    @Override
    public AmenityBooking getAmenityBooking(String bookingId) {
        logger.debug("Retrieving booking: {}", bookingId);
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.debug("Booking not found.");
                    return new BookingException("Booking not found.");
                });
    }

    @Override
    @Transactional
    public AmenityBooking updateBooking(String bookingId, LocalDateTime newStartTime, LocalDateTime newEndTime) throws BookingException {
        logger.debug("Updating booking: {}", bookingId);

        // Fetch existing booking
        AmenityBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.debug("Booking not found.");
                    return new BookingException("Booking not found.");
                });

        // Lock the amenity to handle concurrency issues
        Amenity amenity = booking.getAmenity();
        Amenity lockedAmenity = amenityRepository.lockAmenityForBooking(amenity.getAmenityId());

        // Validate booking constraints for the updated times
        bookingValidator.validateBookingConstraints(lockedAmenity, booking.getUserId(), newStartTime, newEndTime);

        // Update booking details
        booking.setStartTime(newStartTime);
        booking.setEndTime(newEndTime);

        // Save the updated booking
        AmenityBooking updatedBooking = bookingRepository.save(booking);
        logger.debug("Booking updated successfully: {}", updatedBooking);

        return updatedBooking;
    }
}
