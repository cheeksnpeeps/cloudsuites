package com.cloudsuites.framework.services.user.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@Entity
@Table(name = "address")
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private static final Logger logger = LoggerFactory.getLogger(Address.class);

    @Id
    @Column(name = "address_id", unique = true, nullable = false)
    private String addressId;

    @Column(name = "apt_number")
    private String aptNumber;

    @Column(name = "street_number")
    private String streetNumber;

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "province")
    private String province;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    private String country;

    @PrePersist
    protected void onCreate() {
        this.addressId = IdGenerator.generateULID("ADR-");
        logger.debug("Generated addressId: {}", this.addressId);
    }

}