package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.entities.property.Unit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UnitService {

    Unit getUnitById(Long buildingId, Long unitId);

    List<Unit> getAllUnits(Long buildingId);

    Unit saveUnit(Long buildingId, Long floorId, Unit unit);

    public void deleteUnitById(Long buildingId, Long unitId);

    List<Unit> getAllUnitsByFloorNumber(Long buildingId, Integer floorNumber);
}
