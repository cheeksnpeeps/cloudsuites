package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "bowling_alley")
public class BowlingAlley extends Amenity {

    @Column(name = "number_of_lanes")
    private Integer numberOfLanes; // Number of lanes available

    @Column(name = "provides_shoes")
    private Boolean providesShoes; // If bowling shoes are provided

    @Column(name = "allows_food_and_drinks")
    private Boolean allowsFoodAndDrinks; // If food and drinks are allowed
}
