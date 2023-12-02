package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.modules.property.repository.UnitRepository;
import com.cloudsuites.framework.services.entities.property.Unit;
import com.cloudsuites.framework.services.property.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Component
@Transactional
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    @Autowired
    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public Unit getUnitById(Long buildingId, Long unitId) throws NotFoundResponseException {
        return unitRepository.findById(unitId).orElseThrow(() -> new NotFoundResponseException("Unit not found: "+unitId));
    }

    @Override
    public List<Unit> getAllUnitsByFloor(Long buildingId, Long floorId) throws NotFoundResponseException {
        return unitRepository.findAllByFloor_FloorId(floorId).orElseThrow(() -> new NotFoundResponseException("Unit not found for Floor: "+floorId));
    }

    @Override
    public List<Unit> getAllUnits(Long buildingId) {return unitRepository.findAll();}

    @Override
    public Unit saveUnit(Long buildingId, Long floorId, Unit unit) {
        return unitRepository.save(unit);
    }

    @Override
    public void deleteUnitById(Long buildingId, Long unitId) {
        unitRepository.deleteById(unitId);
    }

}
