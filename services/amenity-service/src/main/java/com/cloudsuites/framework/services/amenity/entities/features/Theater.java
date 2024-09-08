package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "theater")
public class Theater extends Amenity {

    @Column(name = "number_of_seats")
    private Integer numberOfSeats; // Number of seats in the theater

    @Column(name = "has_3d_projection")
    private Boolean has3dProjection; // Indicates if the theater has 3D projection

    @Column(name = "has_surround_sound")
    private Boolean hasSurroundSound; // Indicates if the theater has surround sound
}
