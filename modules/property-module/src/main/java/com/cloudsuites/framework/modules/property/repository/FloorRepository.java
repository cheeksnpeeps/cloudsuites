package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.entities.property.Floor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {

    @EntityGraph(attributePaths = "units")
    public Optional<Floor> findByBuilding_BuildingIdAndFloorId(Long buildingId, Long floorId);

}
