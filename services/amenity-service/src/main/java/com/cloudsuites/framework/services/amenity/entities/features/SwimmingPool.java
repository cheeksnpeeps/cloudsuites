package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "swimming_pool")
public class SwimmingPool extends Amenity {

    @Column(name = "has_lifeguard")
    private Boolean hasLifeguard; // Whether a lifeguard is present
}

