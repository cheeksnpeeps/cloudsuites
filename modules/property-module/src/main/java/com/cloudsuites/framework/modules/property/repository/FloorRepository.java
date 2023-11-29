package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.entities.property.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {

    public List<Floor> findByBuildingId(Long buildingId);

    public Floor findByBuildingIdAndFloorId(Long buildingId, Long floorId);

    public Floor findByBuildingIdAndFloorNumber(Long buildingId, Long floorNumber);

}
