package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.AmenityBuilding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityBuildingRepository extends JpaRepository<AmenityBuilding, String> {
}
