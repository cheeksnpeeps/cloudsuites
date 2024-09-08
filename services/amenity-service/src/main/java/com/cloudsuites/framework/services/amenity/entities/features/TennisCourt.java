package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "tennis_court")
public class TennisCourt extends Amenity {

    @Column(name = "court_surface")
    private String courtSurface; // Surface type of the tennis court (e.g., clay, hard, grass)

    @Column(name = "is_indoor")
    private Boolean isIndoor; // Indicates if the court is indoor

    @Column(name = "lighting")
    private Boolean lighting; // Indicates if the court has lighting for night play

    @Column(name = "net_height")
    private Double netHeight; // Height of the net in meters

    @Column(name = "has_spectator_seats")
    private Boolean hasSpectatorSeats; // Indicates if there are seats for spectators

    @Column(name = "has_benches")
    private Boolean hasBenches; // Indicates if there are benches for players to sit

    @Column(name = "booking_fee")
    private BigDecimal bookingFee; // Fee for booking the tennis court

    @Column(name = "court_size")
    private Double courtSize; // Size of the court in square meters

    @Column(name = "is_bookable")
    private Boolean isBookable; // Indicates if the court can be booked
}


