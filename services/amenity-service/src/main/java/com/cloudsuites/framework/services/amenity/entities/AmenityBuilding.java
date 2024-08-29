package com.cloudsuites.framework.services.amenity.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "amenity_building")
public class AmenityBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amenity_id")
    private String amenityId;

    @Column(name = "building_id")
    private String buildingId;

    public AmenityBuilding(String amenityId, String buildingId) {
        this.amenityId = amenityId;
        this.buildingId = buildingId;
    }

    public AmenityBuilding() {

    }
}
