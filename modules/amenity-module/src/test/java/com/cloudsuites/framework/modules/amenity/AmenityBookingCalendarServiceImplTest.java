package com.cloudsuites.framework.modules.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.features.SwimmingPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AmenityBookingCalendarServiceImplTest {


    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private AmenityBookingRepository bookingRepository;

    @InjectMocks
    private AmenityBookingCalendarServiceImpl amenityBookingCalendarService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBookingsForUser() {
        // Arrange
        String userId = "user1";
        AmenityBooking booking = new AmenityBooking();
        booking.setUserId(userId);
        LocalDateTime startDate = LocalDateTime.of(2024, 8, 1, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 8, 1, 11, 0);
        booking.setStartTime(startDate);
        booking.setEndTime(endDate);

        when(bookingRepository.findByUserIdAndFilters(userId, AmenityType.BARBECUE_AREA.name(), startDate, endDate)).thenReturn(List.of(booking));
        // Act
        List<AmenityBooking> bookings = amenityBookingCalendarService.getBookingsForUser(userId, AmenityType.BARBECUE_AREA.name(), startDate, endDate);
        // Assert
        assertEquals(1, bookings.size());
        assertEquals(userId, bookings.get(0).getUserId());
        verify(bookingRepository, Mockito.times(1)).findByUserIdAndFilters(userId, AmenityType.BARBECUE_AREA.name(), startDate, endDate);
    }

    @Test
    void testCalculateAvailableSlots() {
        // Amenity is open from 8 to 23
        Amenity amenity = new SwimmingPool();
        amenity.setOpenTime(LocalTime.of(8, 0));
        amenity.setCloseTime(LocalTime.of(23, 0));

        // Amenity is booked from 10 to 11 and 11 to 23
        AmenityBooking booking = new AmenityBooking();
        booking.setStartTime(LocalDateTime.of(2024, 8, 1, 10, 0));
        booking.setEndTime(LocalDateTime.of(2024, 8, 1, 11, 0));

        AmenityBooking booking2 = new AmenityBooking();
        booking2.setStartTime(LocalDateTime.of(2024, 8, 1, 11, 0));
        booking2.setEndTime(LocalDateTime.of(2024, 8, 1, 23, 0));

        List<AmenityBooking> bookings = List.of(booking, booking2);

        // Available slots are from 8 to 23
        LocalDateTime start = LocalDateTime.of(2024, 8, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 1, 23, 0);

        // Act
        List<LocalDateTime> availableSlots = amenityBookingCalendarService.calculateAvailableSlots(amenity, bookings, start, end);

        // Assert that the available slots are from 8 to 10 on the same day
        assertEquals(2, availableSlots.size());  // 9 slots in total (9 AM - 5 PM) minus 1 booked (10 AM - 11 AM)
        assertEquals(LocalDateTime.of(2024, 8, 1, 8, 0), availableSlots.get(0));
        assertEquals(LocalDateTime.of(2024, 8, 1, 9, 0), availableSlots.get(1));  // First available slot after the booking
    }
}