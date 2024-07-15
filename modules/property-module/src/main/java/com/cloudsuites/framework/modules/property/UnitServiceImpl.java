package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.UnitRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.UnitService;
import com.cloudsuites.framework.services.property.entities.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    private static final Logger logger = LoggerFactory.getLogger(UnitServiceImpl.class);

    @Autowired
    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public Unit getUnitById(Long buildingId, Long unitId) throws NotFoundResponseException {
        logger.debug("Entering getUnitById with buildingId: {} and unitId: {}", buildingId, unitId);

        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> {
                    logger.error("Unit not found for buildingId: {} and unitId: {}", buildingId, unitId);
                    return new NotFoundResponseException("Unit not found: " + unitId);
                });

        logger.debug("Unit found: {}", unit.getUnitNumber());
        return unit;
    }

    @Override
    public List<Unit> getAllUnitsByFloor(Long buildingId, Long floorId) throws NotFoundResponseException {
        logger.debug("Entering getAllUnitsByFloor with buildingId: {} and floorId: {}", buildingId, floorId);

        List<Unit> units = unitRepository.findAllByFloor_FloorId(floorId)
                .orElseThrow(() -> {
                    logger.error("Units not found for buildingId: {} and floorId: {}", buildingId, floorId);
                    return new NotFoundResponseException("Unit not found for Floor: " + floorId);
                });

        logger.debug("Units found: {}", units.size());
        return units;
    }

    @Override
    public List<Unit> getAllUnits(Long buildingId) {
        logger.debug("Entering getAllUnits with buildingId: {}", buildingId);
        List<Unit> units = unitRepository.findAll();
        logger.debug("Units found: {}", units.size());
        return units;
    }

    @Override
    public Unit saveUnit(Long buildingId, Long floorId, Unit unit) {
        logger.debug("Entering saveUnit with buildingId: {}, floorId: {}", buildingId, floorId);
        Unit savedUnit = unitRepository.save(unit);
        logger.debug("Unit saved successfully: {}", savedUnit.getUnitNumber());
        return savedUnit;
    }

    @Override
    public void deleteUnitById(Long buildingId, Long unitId) {
        logger.debug("Entering deleteUnitById with buildingId: {} and unitId: {}", buildingId, unitId);
        unitRepository.deleteById(unitId);
        logger.debug("unit {} deleted successfully",unitId);
    }
}
