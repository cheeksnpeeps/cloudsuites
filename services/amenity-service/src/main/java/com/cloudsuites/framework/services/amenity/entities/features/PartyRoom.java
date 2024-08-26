package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "party_room")
public class PartyRoom extends Amenity {

    @Column(name = "is_paid_service")
    private Boolean isPaidService; // Whether booking the room requires payment

    @Column(name = "hourly_rate")
    private Double hourlyRate; // Rate if it’s a paid service
}
