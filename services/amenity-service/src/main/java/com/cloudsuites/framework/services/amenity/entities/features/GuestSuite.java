package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "guest_suite")
public class GuestSuite extends Amenity {

    @Column(name = "number_of_bedrooms")
    private Integer numberOfBedrooms; // Number of bedrooms in the guest suite

    @Column(name = "has_kitchen")
    private Boolean hasKitchen; // If a kitchen is available

    @Column(name = "nightly_rental_fee")
    private BigDecimal nightlyRentalFee; // Nightly rental fee for the guest suite
}

