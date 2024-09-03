package com.cloudsuites.framework.webapp.rest.amenity.dto;

import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;

import java.time.LocalDateTime;
import java.util.List;

public class AmenityBookingCalendarDto {

    private String amenityId;
    private String amenityName;
    private String amenityType;
    private List<AmenityBooking> bookedSlots;
    private List<LocalDateTime> availableSlots;
}
