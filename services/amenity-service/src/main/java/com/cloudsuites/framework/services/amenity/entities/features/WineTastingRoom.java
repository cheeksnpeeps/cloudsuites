package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "wine_tasting_room")
public class WineTastingRoom extends Amenity {

    @Column(name = "number_of_wines_available")
    private Integer numberOfWinesAvailable; // Number of wines available for tasting

    @Column(name = "allows_private_events")
    private Boolean allowsPrivateEvents; // If private events are allowed

    @Column(name = "seating_capacity")
    private Integer seatingCapacity; // Max seating capacity of the wine tasting room
}

