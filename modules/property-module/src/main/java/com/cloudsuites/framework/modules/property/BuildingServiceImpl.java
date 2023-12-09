package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.BuildingRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.entities.property.Building;
import com.cloudsuites.framework.services.property.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;

    @Autowired
    public BuildingServiceImpl(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Building getBuildingById(Long buildingId) throws NotFoundResponseException {
        return buildingRepository.findById(buildingId)
                .orElseThrow(() -> new NotFoundResponseException("Building not found: "+buildingId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Building> getBuildingByManagementCompanyId(Long managementCompanyId) throws NotFoundResponseException {
        return buildingRepository.findByManagementCompany_ManagementCompanyId(managementCompanyId)
                .orElseThrow(() -> new NotFoundResponseException("Building not found for Management Company: "+managementCompanyId));
    }
    @Transactional(readOnly = true)
    @Override
    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }

    @Transactional
    @Override
    public Building saveBuilding(Building building) {
        return buildingRepository.save(building);
    }

    @Transactional
    @Override
    public void deleteBuildingById(Long buildingId) {
        buildingRepository.deleteById(buildingId);
    }
}