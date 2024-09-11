package com.cloudsuites.framework.services.amenity.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "daily_availability")
public class DailyAvailability {

    @Id
    @Column(name = "day_id", unique = true, nullable = false)
    private String dayId; // Unique identifier for the daily availability

    @ManyToOne
    @JoinColumn(name = "amenity_id", nullable = false)
    private Amenity amenity; // Reference back to the amenity

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek; // Enum representing the day of the week

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime; // Opening time for the day

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime; // Closing time for the day

    public DailyAvailability(Amenity amenity, DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime) {
        this.amenity = amenity;
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    @PrePersist
    public void onCreate() {
        this.dayId = IdGenerator.generateULID("DAY-");
    }
}
