package com.cloudsuites.framework.services.amenity.entities.booking;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "amenity_booking")
public class AmenityBooking {

    @Id
    @Column(name = "booking_id")
    private String bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amenity_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    // If flyway is disabled, you can use the following line instead of the above line
    // @JoinColumn(name = "amenity_id", nullable = false)
    private Amenity amenity;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.bookingId = IdGenerator.generateULID("BK-");
    }

    @Version
    private Long version; // For optimistic locking


    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
