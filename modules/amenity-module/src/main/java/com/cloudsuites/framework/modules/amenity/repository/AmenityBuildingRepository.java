package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.AmenityBuilding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface AmenityBuildingRepository extends JpaRepository<AmenityBuilding, String> {

    List<AmenityBuilding> findByBuildingId(String buildingId);

    List<AmenityBuilding> findByAmenityId(String amenityId);

    void deleteByAmenityIdAndBuildingIdIn(String amenityId, Set<String> toRemove);

    boolean existsByAmenityIdAndBuildingId(String amenityId, String buildingId);

    void deleteByAmenityId(String amenityId);

    List<AmenityBuilding> findByAmenityIdAndBuildingId(String amenityId, String buildingId);
}
