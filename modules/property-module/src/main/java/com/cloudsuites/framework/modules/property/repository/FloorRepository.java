package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.entities.property.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {

    public List<Floor> findByBuilding_BuildingId(Long buildingId);

    public Floor findByBuilding_BuildingIdAndFloorId(Long buildingId, Long floorId);

    public Floor findByBuilding_BuildingIdAndFloorNumber(Long buildingId, Long floorNumber);

}
