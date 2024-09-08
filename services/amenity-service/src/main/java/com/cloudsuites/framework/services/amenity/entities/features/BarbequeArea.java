package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "barbeque_area")
public class BarbequeArea extends Amenity {

    @Column(name = "number_of_grills")
    private Integer numberOfGrills; // Number of barbeque grills available

    @Column(name = "is_covered")
    private Boolean isCovered; // Indicates if the barbeque area is covered (e.g., has a roof)

    @Column(name = "has_seating")
    private Boolean hasSeating; // Indicates if the area has seating arrangements

    @Column(name = "has_lighting")
    private Boolean hasLighting; // Indicates if the barbeque area has outdoor lighting

    @Column(name = "has_water_source")
    private Boolean hasWaterSource; // Indicates if there is a water source available (e.g., sink, faucet)

    @Column(name = "has_fire_pit")
    private Boolean hasFirePit; // Indicates if there is a fire pit in the barbeque area

    @Column(name = "has_reservation_system")
    private Boolean hasReservationSystem; // Indicates if the area can be reserved for private events

    @Column(name = "max_occupancy")
    private Integer maxOccupancy; // Maximum number of people the area can accommodate

    @Column(name = "rental_fee")
    private BigDecimal rentalFee; // Fee for renting the barbeque area
}

