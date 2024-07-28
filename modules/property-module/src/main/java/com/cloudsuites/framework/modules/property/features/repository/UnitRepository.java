package com.cloudsuites.framework.modules.property.features.repository;

import com.cloudsuites.framework.services.property.features.entities.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, String> {

    Optional<List<Unit>> findAllByFloor_FloorId(String floorId);

    Optional<Unit> findByBuilding_BuildingIdAndUnitId(String buildingId, String unitId);
}