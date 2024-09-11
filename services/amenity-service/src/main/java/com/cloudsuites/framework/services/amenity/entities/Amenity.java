package com.cloudsuites.framework.services.amenity.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingLimitPeriod;
import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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

    @OneToMany(mappedBy = "amenity", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DailyAvailability> dailyAvailabilities; // List of availabilities for each day of the week

    @Column(name = "location")
    private String location; // Location or address of the amenity

    @Column(name = "capacity")
    private Integer capacity = 1; // Maximum capacity of the amenity

    @Column(name = "is_booking_required", nullable = false)
    private Boolean isBookingRequired = true; // Indicates if booking is required for the amenity

    @Column(name = "is_paid_service", nullable = false)
    private Boolean isPaidService = false; // Indicates if the amenity requires a fee to use

    @Column(name = "rules")
    private String rules; // Rules and regulations for the amenity

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_status", nullable = false)
    private MaintenanceStatus maintenanceStatus = MaintenanceStatus.OPERATIONAL; // Current maintenance status of the amenity

    @Column(name = "advance_booking_period")
    private Integer advanceBookingPeriod = 10080; // Minimum advance booking period required for the amenity

    @Column(name = "booking_duration_limit")
    private Integer bookingDurationLimit = 360; // Maximum duration for which the amenity can be booked

    @Column(name = "max_booking_overlap")
    private Integer maxBookingOverlap = 2; // Maximum number of bookings that can overlap

    @Column(name = "min_booking_duration")
    private Integer minBookingDuration = 60; // Minimum duration for which the amenity can be booked

    @Column(name = "maximum_booking_duration")
    private Integer maxBookingDuration = 60; // Minimum duration for which the amenity can be booked

    @Column(name = "max_bookings_per_tenant")
    private Integer maxBookingsPerTenant = 1; // Maximum number of bookings allowed per tenant

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_limit_period")
    private BookingLimitPeriod bookingLimitPeriod = BookingLimitPeriod.DAILY; // Period for which the booking limit is enforced

    protected Amenity() {
    }

    @PrePersist
    public void onCreate() {
        this.amenityId = IdGenerator.generateULID("AMN-");
        initializeDailyAvailability();
    }

    private void initializeDailyAvailability() {
        if (dailyAvailabilities == null) {
            dailyAvailabilities = new ArrayList<>();
            dailyAvailabilities.add(new DailyAvailability(this, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)));
            dailyAvailabilities.add(new DailyAvailability(this, DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)));
            dailyAvailabilities.add(new DailyAvailability(this, DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)));
            dailyAvailabilities.add(new DailyAvailability(this, DayOfWeek.THURSDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)));
            dailyAvailabilities.add(new DailyAvailability(this, DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)));
            dailyAvailabilities.add(new DailyAvailability(this, DayOfWeek.SATURDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)));
            dailyAvailabilities.add(new DailyAvailability(this, DayOfWeek.SUNDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)));
        } else {
            dailyAvailabilities.forEach(dailyAvailability -> dailyAvailability.setAmenity(this));
        }

    }
}
