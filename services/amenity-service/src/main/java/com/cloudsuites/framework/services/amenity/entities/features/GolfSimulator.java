package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "golf_simulator")
public class GolfSimulator extends Amenity {

    @Column(name = "simulator_model")
    private String simulatorModel; // Brand or model of the golf simulator

    @Column(name = "provides_clubs")
    private Boolean providesClubs; // If golf clubs are provided

    @Column(name = "max_players")
    private Integer maxPlayers; // Maximum number of players at a time
}
