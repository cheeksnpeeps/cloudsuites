package com.cloudsuites.framework.services.property.features.service;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BuildingService {

    Building getBuildingById(String buildingId) throws NotFoundResponseException;

    List<Building> getBuildingByCompanyId(String companyId) throws NotFoundResponseException;

    List<Building> getAllBuildings();

    Building saveBuilding(Building building);

    void deleteBuildingById(String buildingId);

}
