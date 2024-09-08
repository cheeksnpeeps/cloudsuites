package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "massage_room")
public class MassageRoom extends Amenity {

    @Column(name = "has_licensed_therapist")
    private Boolean hasLicensedTherapist; // If a licensed therapist is available

    @Column(name = "has_sauna")
    private Boolean hasSauna; // If a sauna is available

    @Column(name = "max_capacity")
    private Integer maxCapacity; // Maximum capacity of the massage room
}

