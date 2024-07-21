package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.BuildingRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.BuildingService;
import com.cloudsuites.framework.services.property.entities.Building;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BuildingServiceImpl implements BuildingService {

    private static final Logger logger = LoggerFactory.getLogger(BuildingServiceImpl.class);

    private final BuildingRepository buildingRepository;

    @Autowired
    public BuildingServiceImpl(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Building getBuildingById(String buildingId) throws NotFoundResponseException {
        logger.debug("Entering getBuildingById with buildingId: {}", buildingId);
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> {
                    logger.error("Building not found for ID: {}", buildingId);
                    return new NotFoundResponseException("Building not found for ID: " + buildingId);
                });
        logger.debug("Building found: {}", building.getName());
        return building;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Building> getBuildingByManagementCompanyId(String managementCompanyId) throws NotFoundResponseException {
        logger.debug("Entering getBuildingByManagementCompanyId with managementCompanyId: {}", managementCompanyId);
        List<Building> buildings = buildingRepository.findByManagementCompany_ManagementCompanyId(managementCompanyId)
                .orElseThrow(() -> {
                    logger.error("Buildings not found for Management Company: {}", managementCompanyId);
                    return new NotFoundResponseException("Buildings not found for Management Company: " + managementCompanyId);
                });
        logger.debug("Buildings found for Management Company {}:", managementCompanyId);
        return buildings;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Building> getAllBuildings() {
        logger.debug("Entering getAllBuildings");
        List<Building> buildings = buildingRepository.findAll();
        logger.debug("Retrieved {} buildings", buildings.size());
        return buildings;
    }

    @Transactional
    @Override
    public Building saveBuilding(Building building) {
        logger.debug("Entering saveBuilding with building: {}", building);
        Building savedBuilding = buildingRepository.save(building);
        logger.debug("Building saved successfully: {}", savedBuilding);
        return savedBuilding;
    }

    @Transactional
    @Override
    public void deleteBuildingById(String buildingId) {
        logger.debug("Entering deleteBuildingById with buildingId: {}", buildingId);
        buildingRepository.deleteById(buildingId);
        logger.debug("Building with ID {} deleted successfully", buildingId);
    }}