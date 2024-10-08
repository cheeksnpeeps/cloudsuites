package com.cloudsuites.framework.services.property.features.service;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UnitService {

    Unit getUnitById(String buildingId, String unitId) throws NotFoundResponseException;

    List<Unit> getAllUnits(String buildingId);

    Unit saveUnit(Unit unit);

    List<Unit> saveAllUnits(List<Unit> units);

    List<Unit> getAllUnitsByFloor(String buildingId, String floorId) throws NotFoundResponseException;

    void deleteUnitById(String buildingId, String unitId) throws NotFoundResponseException;

    void deleteAllUnitInBatch(List<Unit> units);

    void setOwnerForUnit(Tenant tenant) throws NotFoundResponseException;
}
