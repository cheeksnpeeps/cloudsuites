package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.AmenityBuilding;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AmenityBuildingRepository extends JpaRepository<AmenityBuilding, String> {

    @Transactional
    List<AmenityBuilding> findByBuildingId(String buildingId);

    @Transactional
    List<AmenityBuilding> findByAmenityId(String amenityId);

    @Transactional
    void deleteByAmenityIdAndBuildingIdIn(String amenityId, Set<String> toRemove);

    @Transactional
    boolean existsByAmenityIdAndBuildingId(String amenityId, String buildingId);

    @Transactional
    void deleteByAmenityId(String amenityId);

    @Transactional
    List<AmenityBuilding> findByAmenityIdAndBuildingId(String amenityId, String buildingId);
}
