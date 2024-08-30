package com.cloudsuites.framework.services.amenity.entities;

import com.cloudsuites.framework.services.amenity.entities.booking.BookingLimitPeriod;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

@Data
@Entity
@Table(name = "amenity")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Amenity {

    @Id
    @Column(name = "amenity_id", unique = true, nullable = false)
    private String amenityId; // Unique identifier for the amenity

    @Column(name = "image_url")
    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "amenity_image_gallery", joinColumns = @JoinColumn(name = "amenity_id"))
    @Column(name = "image_url")
    private Set<String> imageGallery;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "name", unique = true, nullable = false)
    private String name; // Name of the amenity

    @Column(name = "description")
    private String description; // Description of the amenity

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AmenityType type; // Type of the amenity (e.g., Swimming Pool, Gym)

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // Indicates if the amenity is currently active

    @Column(name = "open_time")
    private LocalTime openTime; // Opening time of the amenity

    @Column(name = "close_time")
    private LocalTime closeTime; // Closing time of the amenity

    @Column(name = "location", nullable = false)
    private String location; // Location or address of the amenity

    @Column(name = "capacity")
    private Integer capacity; // Maximum capacity for the amenity

    @Column(name = "is_booking_required", nullable = false)
    private Boolean isBookingRequired = false; // Indicates if booking is required for the amenity

    @Column(name = "is_paid_service", nullable = false)
    private Boolean isPaidService = false; // Indicates if the amenity requires a fee to use

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate; // Hourly rate for using the amenity

    @Column(name = "rules")
    private String rules; // Rules and regulations for the amenity

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_status", nullable = false)
    private MaintenanceStatus maintenanceStatus = MaintenanceStatus.OPERATIONAL; // Current maintenance status of the amenity

    @Column(name = "advance_booking_period")
    private Integer advanceBookingPeriod; // Number of days in advance the amenity can be booked

    @Column(name = "booking_duration_limit")
    private Integer bookingDurationLimit; // Maximum duration for which the amenity can be booked

    @Column(name = "minimum_booking_duration")
    private Integer minimumBookingDuration; // Minimum duration for which the amenity can be booked

    @Column(name = "max_bookings_per_tenant")
    private Integer maxBookingsPerTenant;  // Maximum number of bookings a tenant can make for this amenity

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_limit_period")
    private BookingLimitPeriod bookingLimitPeriod; // The period for which the booking limit applies (e.g., DAILY, WEEKLY, MONTHLY)

}
