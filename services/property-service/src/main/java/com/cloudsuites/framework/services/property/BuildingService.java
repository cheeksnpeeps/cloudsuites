package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.entities.property.Building;
import com.cloudsuites.framework.services.entities.property.Floor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface BuildingService {

    // Find a building by its ID
    public Building getBuildingById(Long buildingId);

    List<Building> getBuildingByManagementCompanyId(Long managementCompanyId);

    // Find all buildings
    public List<Building> getAllBuildings();

    // Save a new building or update an existing one
    public Building saveBuilding(Building building);

    // Delete a building by its ID
    public void deleteBuildingById(Long buildingId);

    public void addFloor(Long buildingId, Floor floor);
}
