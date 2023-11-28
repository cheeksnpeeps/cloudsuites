package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.UnitRepository;
import com.cloudsuites.framework.services.common.entities.property.Unit;
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
    public Unit getUnitById(Long unitId) {
        return unitRepository.findById(unitId).orElse(null);
    }

    @Override
    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }

    @Override
    public Unit saveUnit(Unit unit) {
        return unitRepository.save(unit);
    }

    @Override
    public void deleteUnitById(Long unitId) {
        unitRepository.deleteById(unitId);
    }
}
