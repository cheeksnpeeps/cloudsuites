package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "elevator")
public class Elevator extends Amenity {

    @Column(name = "weight_capacity")
    private Integer weightCapacity; // Maximum weight capacity of the elevator

    @Column(name = "is_wheelchair_accessible")
    private Boolean isWheelchairAccessible; // If the elevator is wheelchair accessible

    @Column(name = "floors_serviced")
    private Integer floorsServiced; // Number of floors serviced by the elevator
}
