package com.cloudsuites.framework.modules.property;

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
    public Unit getUnitById(Long buildingId, Long unitId) {
        return unitRepository.findById(unitId).orElse(null);
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

    @Override
    public List<Unit> getAllUnitsByFloorNumber(Long buildingId, Integer floorNumber) {
        return unitRepository.findAllByFloor_FloorNumber(floorNumber);
    }

}
