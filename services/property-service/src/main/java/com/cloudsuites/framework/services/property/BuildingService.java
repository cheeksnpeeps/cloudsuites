package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.entities.Building;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BuildingService {

    Building getBuildingById(Long buildingId) throws NotFoundResponseException;

    List<Building> getBuildingByManagementCompanyId(Long managementCompanyId) throws NotFoundResponseException;

    List<Building> getAllBuildings();

    Building saveBuilding(Building building);

    void deleteBuildingById(Long buildingId);

}
