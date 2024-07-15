package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.entities.Unit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UnitService {

    Unit getUnitById(Long buildingId, Long unitId) throws NotFoundResponseException;

    List<Unit> getAllUnits(Long buildingId);

    Unit saveUnit(Long buildingId, Long floorId, Unit unit);

    void deleteUnitById(Long buildingId, Long unitId);
    
    List<Unit> getAllUnitsByFloor(Long buildingId, Long floorId) throws NotFoundResponseException;
}
