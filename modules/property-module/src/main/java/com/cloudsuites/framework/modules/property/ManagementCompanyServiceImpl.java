package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.repository.ManagementCompanyRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.entities.property.Building;
import com.cloudsuites.framework.services.entities.property.Floor;
import com.cloudsuites.framework.services.entities.property.ManagementCompany;
import com.cloudsuites.framework.services.entities.property.Unit;
import com.cloudsuites.framework.services.property.ManagementCompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ManagementCompanyServiceImpl implements ManagementCompanyService {

    private final ManagementCompanyRepository managementCompanyRepository;

    private static final Logger logger = LoggerFactory.getLogger(FloorServiceImpl.class);

    @Autowired
    public ManagementCompanyServiceImpl(ManagementCompanyRepository managementCompanyRepository, BuildingRepository buildingRepository) {
        this.managementCompanyRepository = managementCompanyRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public ManagementCompany getManagementCompanyById(Long managementCompanyId) throws NotFoundResponseException {
        logger.debug("Entering getManagementCompanyById with managementCompanyId: {}", managementCompanyId);

        ManagementCompany managementCompany = managementCompanyRepository.findById(managementCompanyId)
                .orElseThrow(() -> {
                    logger.error("Management Company not found for ID: {}", managementCompanyId);
                    return new NotFoundResponseException("Management Company not found: " + managementCompanyId);
                });

        logger.debug("Management Company found: {}", managementCompany.getName());
        return managementCompany;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ManagementCompany> getAllManagementCompanies() {
        List<ManagementCompany> managementCompanies = managementCompanyRepository.findAll();
        logger.debug("Retrieved {} managementCompanies", managementCompanies.size());
        return managementCompanies;
    }

    @Transactional
    @Override
    public void deleteManagementCompanyById(Long managementCompanyId) {
        logger.debug("Entering deleteManagementCompanyById with managementCompanyId: {}", managementCompanyId);
        managementCompanyRepository.deleteById(managementCompanyId);
        logger.debug("Management Company deleted: {}", managementCompanyId);
    }

    @Transactional
    @Override
    public ManagementCompany saveManagementCompany(ManagementCompany managementCompany) {
        logger.debug("Entering saveManagementCompany with managementCompany: {}", managementCompany.getName());
        ManagementCompany savedCompany = managementCompanyRepository.save(managementCompany);
        setManagementCompanyInBuildings(savedCompany.getBuildings(), savedCompany);
        logger.debug("Created Management Company: {}", savedCompany.getName());
        return savedCompany;
    }

    private void setManagementCompanyInBuildings(List<Building> buildings, ManagementCompany managementCompany) {
        if (buildings != null) {
            buildings.forEach(building -> {
                building.setManagementCompany(managementCompany);
                setBuildingInFloors(building.getFloors(), building);
                setBuildingInUnits(building.getUnits(), building);
            });
            managementCompanyRepository.save(managementCompany);
        }
    }

    private void setBuildingInFloors(List<Floor> floors, Building building) {
        if (floors != null) {
            floors.forEach(floor -> {
                floor.setBuilding(building);
                setFloorInUnits(floor.getUnits(), floor);
            });
        }
    }

    private void setBuildingInUnits(List<Unit> units, Building building) {
        if (units != null) {
            units.forEach(unit -> unit.setBuilding(building));
        }
    }

    private void setFloorInUnits(List<Unit> units, Floor floor) {
        if (units != null) {
            units.forEach(unit -> unit.setFloor(floor));
        }
    }



}

