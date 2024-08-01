package com.cloudsuites.framework.modules.property.features.module;

import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.service.BuildingService;
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
    public List<Building> getBuildingByCompanyId(String companyId) throws NotFoundResponseException {
        logger.debug("Entering getBuildingByCompanyId with companyId: {}", companyId);
        List<Building> buildings = buildingRepository.findByCompany_CompanyId(companyId)
                .orElseThrow(() -> {
                    logger.error("Buildings not found for Management Company: {}", companyId);
                    return new NotFoundResponseException("Buildings not found for Management Company: " + companyId);
                });
        logger.debug("Buildings found for Management Company {}:", companyId);
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
    public void deleteBuildingById(String companyId, String buildingId) throws NotFoundResponseException {
        buildingRepository.findByCompany_CompanyIdAndBuildingId(companyId, buildingId).orElseThrow(() -> {
            logger.error("Building not found for company ID {} and building ID: {}", companyId, buildingId);
            return new NotFoundResponseException("Building not found for company ID " + companyId + " and building ID: " + buildingId);
        });
        logger.debug("Entering deleteBuildingById with buildingId: {}", buildingId);
        buildingRepository.deleteById(buildingId);
        logger.debug("Building with ID {} deleted successfully", buildingId);
    }}