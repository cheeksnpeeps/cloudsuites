package com.cloudsuites.framework.modules.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.modules.amenity.repository.CustomBookingCalendarRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.DailyAvailability;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingStatus;
import com.cloudsuites.framework.services.amenity.entities.features.SwimmingPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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

    @Mock
    private CustomBookingCalendarRepository customBookingCalendarRepository;

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

        when(customBookingCalendarRepository.findByUserIdAndFilters(List.of(userId), null, null, startDate, endDate))
                .thenReturn(List.of(booking));

        List<AmenityBooking> bookings = amenityBookingCalendarService.getBookingsForUser(List.of(userId), null, List.of(BookingStatus.PENDING), startDate, endDate);

        assertEquals(1, bookings.size());
        assertEquals(userId, bookings.get(0).getUserId());
        verify(customBookingCalendarRepository, Mockito.times(1)).findByUserIdAndFilters(List.of(userId), null, null, startDate, endDate);
    }

    @Test
    void testCalculateAvailableSlots() {
        // Arrange
        Amenity amenity = new SwimmingPool();

        DailyAvailability mondayAvailability = new DailyAvailability();
        mondayAvailability.setDayOfWeek(DayOfWeek.THURSDAY);
        mondayAvailability.setOpenTime(LocalTime.of(8, 0));
        mondayAvailability.setCloseTime(LocalTime.of(23, 0));

        amenity.setDailyAvailabilities(List.of(mondayAvailability));

        // Amenity is booked from 10 to 11 and 11 to 23
        AmenityBooking booking = new AmenityBooking();
        booking.setStartTime(LocalDateTime.of(2024, 8, 1, 10, 0));
        booking.setEndTime(LocalDateTime.of(2024, 8, 1, 11, 0));

        AmenityBooking booking2 = new AmenityBooking();
        booking2.setStartTime(LocalDateTime.of(2024, 8, 1, 11, 0));
        booking2.setEndTime(LocalDateTime.of(2024, 8, 1, 23, 0));

        List<AmenityBooking> bookings = List.of(booking, booking2);

        // Mock repository calls
        when(amenityRepository.findById("amenityId")).thenReturn(Optional.of(amenity));
        when(customBookingCalendarRepository.findByAmenityIdAndTimeRange("amenityId", LocalDateTime.of(2024, 8, 1, 8, 0), LocalDateTime.of(2024, 8, 1, 23, 0)))
                .thenReturn(bookings);

        // Available slots are from 8 to 10 on the same day
        LocalDateTime start = LocalDateTime.of(2024, 8, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 1, 23, 0);

        // Act
        List<LocalDateTime> availableSlots = amenityBookingCalendarService.getAvailableSlotsForAmenity("amenityId", start, end);

        // Assert
        assertEquals(2, availableSlots.size());  // Slots are 8 AM, 9 AM
        assertEquals(LocalDateTime.of(2024, 8, 1, 8, 0), availableSlots.get(0));
        assertEquals(LocalDateTime.of(2024, 8, 1, 9, 0), availableSlots.get(1));  // First available slot after the booking
    }

    @Test
    void testCalculateAvailableSlots_NoAvailability() {
        // Arrange
        Amenity amenity = new SwimmingPool();

        DailyAvailability mondayAvailability = new DailyAvailability();
        mondayAvailability.setDayOfWeek(DayOfWeek.THURSDAY);
        mondayAvailability.setOpenTime(LocalTime.of(8, 0));
        mondayAvailability.setCloseTime(LocalTime.of(23, 0));

        amenity.setDailyAvailabilities(List.of(mondayAvailability));

        // Amenity is booked all day
        AmenityBooking booking = new AmenityBooking();
        booking.setStartTime(LocalDateTime.of(2024, 8, 1, 8, 0));
        booking.setEndTime(LocalDateTime.of(2024, 8, 1, 23, 0));

        List<AmenityBooking> bookings = List.of(booking);

        // Mock repository calls
        when(amenityRepository.findById("amenityId")).thenReturn(Optional.of(amenity));
        when(customBookingCalendarRepository.findByAmenityIdAndTimeRange("amenityId", LocalDateTime.of(2024, 8, 1, 8, 0), LocalDateTime.of(2024, 8, 1, 23, 0)))
                .thenReturn(bookings);

        // Available slots should be empty
        LocalDateTime start = LocalDateTime.of(2024, 8, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 1, 23, 0);

        // Act
        List<LocalDateTime> availableSlots = amenityBookingCalendarService.getAvailableSlotsForAmenity("amenityId", start, end);

        // Assert
        assertEquals(0, availableSlots.size());
    }
}
