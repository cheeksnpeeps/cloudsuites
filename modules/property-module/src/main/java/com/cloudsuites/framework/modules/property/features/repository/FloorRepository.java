package com.cloudsuites.framework.modules.property.features.repository;

import com.cloudsuites.framework.services.property.features.entities.Floor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FloorRepository extends JpaRepository<Floor, String> {

    @EntityGraph(attributePaths = "units")
    Optional<Floor> findByBuilding_BuildingIdAndFloorId(String buildingId, String floorId);

}
