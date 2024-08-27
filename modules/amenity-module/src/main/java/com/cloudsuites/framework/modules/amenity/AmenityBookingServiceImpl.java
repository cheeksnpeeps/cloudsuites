package com.cloudsuites.framework.modules.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;
import com.cloudsuites.framework.services.amenity.service.AmenityBookingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AmenityBookingServiceImpl implements AmenityBookingService {

    private final AmenityRepository amenityRepository;

    private final AmenityBookingRepository bookingRepository;

    AmenityBookingServiceImpl(AmenityRepository amenityRepository, AmenityBookingRepository bookingRepository) {
        this.amenityRepository = amenityRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public AmenityBooking bookAmenity(String amenityId, String userId, LocalDateTime startTime, LocalDateTime endTime) throws BookingException {
        Optional<Amenity> amenityOpt = amenityRepository.findById(amenityId);
        if (!amenityOpt.isPresent()) {
            throw new BookingException("Amenity not found.");
        }

        Amenity amenity = amenityOpt.get();
        if (!isAvailable(amenity, startTime, endTime)) {
            throw new BookingException("Amenity is not available during the requested time.");
        }

        AmenityBooking booking = new AmenityBooking();
        booking.setAmenity(amenity);
        booking.setUserId(userId);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);

        return bookingRepository.save(booking);
    }

    @Override
    public void cancelBooking(String bookingId) throws BookingException {
        Optional<AmenityBooking> bookingOpt = bookingRepository.findById(bookingId);
        if (!bookingOpt.isPresent()) {
            throw new BookingException("Booking not found.");
        }

        bookingRepository.delete(bookingOpt.get());
    }

    @Override
    public boolean isAvailable(Amenity amenity, LocalDateTime startTime, LocalDateTime endTime) {
        // Check existing bookings and other constraints
        // For simplicity, assuming all amenities are available
        return true;
    }
}
