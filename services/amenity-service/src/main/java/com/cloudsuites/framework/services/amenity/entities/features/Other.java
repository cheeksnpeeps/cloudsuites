package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "other")
public class Other extends Amenity {

    @Column(name = "description")
    private String description; // Description of the custom amenity

    @Column(name = "special_instructions")
    private String specialInstructions; // Special instructions or notes for the amenity
}
