package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tennis_court")
public class TennisCourt extends Amenity {

    @Column(name = "max_reservation_per_hour")
    private Integer maxReservationPerHour; // Maximum number of reservations per hour
}

