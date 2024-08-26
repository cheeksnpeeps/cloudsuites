package com.cloudsuites.framework.services.amenity.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

@Data
@Entity
@Table(name = "amenity")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Amenity {

    @Id
    @Column(name = "amenity_id", unique = true, nullable = false)
    private String amenityId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AmenityType type;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    // Common fields and methods
}
