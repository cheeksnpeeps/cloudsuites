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
}

