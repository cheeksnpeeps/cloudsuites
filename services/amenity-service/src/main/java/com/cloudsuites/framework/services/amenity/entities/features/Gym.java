package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "gym")
public class Gym extends Amenity {

    @Column(name = "number_of_machines")
    private Integer numberOfMachines; // Number of gym machines available

    @Column(name = "has_personal_trainers")
    private Boolean hasPersonalTrainers; // Indicates if personal trainers are available

    @Column(name = "has_fitness_classes")
    private Boolean hasFitnessClasses; // Indicates if fitness classes are offered

    @Column(name = "has_changing_rooms")
    private Boolean hasChangingRooms; // Indicates if changing rooms are available

    @Column(name = "has_showers")
    private Boolean hasShowers; // Indicates if showers are available

    @Column(name = "has_lockers")
    private Boolean hasLockers; // Indicates if lockers are available

    @Column(name = "has_towels")
    private Boolean hasTowels; // Indicates if towels are provided

    @Column(name = "has_water_fountains")
    private Boolean hasWaterFountains; // Indicates if water fountains are available

    @Column(name = "has_wifi")
    private Boolean hasWifi; // Indicates if wifi is available

    @Column(name = "has_parking")
    private Boolean hasParking; // Indicates if parking is available

}

