package com.cloudsuites.framework.modules.property.features.module;

import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class UnitServiceImpl implements UnitService {

    private static final Logger logger = LoggerFactory.getLogger(UnitServiceImpl.class);
    private final TenantRepository tenantRepository;
    private final UnitRepository unitRepository;

    @Autowired
    public UnitServiceImpl(UnitRepository unitRepository, TenantRepository tenantRepository) {
        this.unitRepository = unitRepository;
        this.tenantRepository = tenantRepository;
    }

    @Override
    public Unit getUnitById(String buildingId, String unitId) throws NotFoundResponseException {
        logger.debug("Entering getUnitById with buildingId: {} and unitId: {}", buildingId, unitId);
        if (!unitRepository.existsById(unitId)) {
            logger.error("Unit not found for buildingId: {} and unitId: {}", buildingId, unitId);
            throw new NotFoundResponseException("Unit not found: " + unitId);
        }
        Unit unit = unitRepository.findByBuilding_BuildingIdAndUnitId(buildingId, unitId)
                .orElseThrow(() -> {
                    logger.error("Unit not found for buildingId: {} and unitId: {}", buildingId, unitId);
                    return new NotFoundResponseException("Unit not found: " + unitId);
                });

        logger.debug("Unit found: {}", unit.getUnitId());
        return unit;
    }

    @Override
    public List<Unit> getAllUnitsByFloor(String buildingId, String floorId) throws NotFoundResponseException {
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
    public List<Unit> getAllUnits(String buildingId) {
        logger.debug("Entering getAllUnits with buildingId: {}", buildingId);
        List<Unit> units = unitRepository.findAll();
        logger.debug("Units found: {}", units.size());
        return units;
    }

    @Override
    public Unit saveUnit(Unit unit) {
        Unit savedUnit = unitRepository.save(unit);
        logger.debug("Unit saved successfully: {}", savedUnit.getUnitId());
        return savedUnit;
    }

    @Override
    public List<Unit> saveAllUnits(List<Unit> units) {
        logger.debug("Entering saveUnit with units: {}", units.size());
        List<Unit> savedUnits = unitRepository.saveAll(units);
        logger.debug("Units saved successfully: {}", savedUnits.size());
        return savedUnits;
    }


    @Override
    public void deleteUnitById(String buildingId, String floorId, String unitId) {
        logger.debug("Entering deleteUnitById with buildingId: {} and unitId: {}", buildingId, unitId);
        unitRepository.deleteById(unitId);
        logger.debug("unit {} deleted successfully",unitId);
    }

    @Override
    public void deleteAllUnitInBatch(List<Unit> units) {
        logger.debug("Entering deleteAllUnitInBatch with units: {}", units.size());
        unitRepository.deleteAllInBatch(units);
        logger.debug("units {} deleted successfully", units.size());
    }

    @Override
    public void setOwnerForUnit(Tenant tenant) throws NotFoundResponseException {
        Unit unit = tenant.getUnit();
        List<Tenant> tenants = tenantRepository.findByBuilding_BuildingIdAndUnit_UnitId(unit.getBuilding().getBuildingId(), unit.getUnitId())
                .orElseThrow(() -> {
                    logger.error("Unit not found for buildingId: {} and unitId: {}", unit.getBuilding().getBuildingId(), unit.getUnitId());
                    return new NotFoundResponseException("Unit not found: " + unit.getUnitId());
                });
        // Update tenants isOwner flag to false except for the current tenant
        tenants.forEach(t -> {
            if (!t.getTenantId().equals(tenant.getTenantId())) {
                t.setIsOwner(false);
                tenantRepository.save(t);
                t.setIsPrimaryTenant(false);
            }
        });
    }
}
