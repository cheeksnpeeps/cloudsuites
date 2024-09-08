package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "library")
public class Library extends Amenity {

    @Column(name = "number_of_books")
    private Integer numberOfBooks; // Number of books available in the library

    @Column(name = "has_computers")
    private Boolean hasComputers; // If computers are available for public use

    @Column(name = "seating_capacity")
    private Integer seatingCapacity; // Maximum seating capacity of the library
}

