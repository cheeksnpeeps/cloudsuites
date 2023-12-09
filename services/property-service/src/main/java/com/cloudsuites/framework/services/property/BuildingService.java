package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.entities.property.Building;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BuildingService {

    public Building getBuildingById(Long buildingId) throws NotFoundResponseException;

    List<Building> getBuildingByManagementCompanyId(Long managementCompanyId) throws NotFoundResponseException;

    List<Building> getAllBuildings();

    public Building saveBuilding(Building building);

    public void deleteBuildingById(Long buildingId);

}
