package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.entities.Unit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UnitService {

    Unit getUnitById(String buildingId, Long unitId) throws NotFoundResponseException;

    List<Unit> getAllUnits(String buildingId);

    Unit saveUnit(String buildingId, Long floorId, Unit unit);

    List<Unit> saveAllUnits(List<Unit> units);

    void deleteUnitById(String buildingId, Long unitId, Long id);

    List<Unit> getAllUnitsByFloor(String buildingId, Long floorId) throws NotFoundResponseException;

    void deleteAllUnitInBatch(List<Unit> units);
}
