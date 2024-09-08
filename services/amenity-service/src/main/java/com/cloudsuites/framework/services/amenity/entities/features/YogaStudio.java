package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "yoga_studio")
public class YogaStudio extends Amenity {

    @Column(name = "provides_yoga_mats")
    private Boolean providesYogaMats; // If yoga mats are provided

    @Column(name = "max_participants")
    private Integer maxParticipants; // Maximum number of participants allowed

    @Column(name = "available_classes")
    private String availableClasses; // List of yoga classes available
}
