package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "aerobics_room")
public class AerobicsRoom extends Amenity {

    @Column(name = "floor_type")
    private String floorType; // Type of floor, such as hardwood, rubber, etc.

    @Column(name = "has_sound_system")
    private Boolean hasSoundSystem; // Indicates if the room has a built-in sound system

    @Column(name = "mirror_walls")
    private Boolean mirrorWalls; // Indicates if the room has mirror walls

    @Column(name = "max_class_capacity")
    private Integer maxClassCapacity; // Maximum number of participants for a class

    @Column(name = "has_ac")
    private Boolean hasAC; // Indicates if the room has air conditioning

    @Column(name = "has_equipment")
    private Boolean hasEquipment; // Indicates if the room is equipped with workout equipment

    @Column(name = "booking_fee")
    private BigDecimal bookingFee; // Fee for booking the aerobics room

    @Column(name = "class_schedule")
    private String classSchedule; // Schedule for regular classes or sessions
}
