package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "billiard_room")
public class BilliardRoom extends Amenity {

    @Column(name = "number_of_billiard_tables")
    private Integer numberOfBilliardTables; // Number of billiard tables available

    @Column(name = "provides_pool_cues")
    private Boolean providesPoolCues; // If pool cues are provided

    @Column(name = "allows_food_and_drinks")
    private Boolean allowsFoodAndDrinks; // If food and drinks are allowed
}

