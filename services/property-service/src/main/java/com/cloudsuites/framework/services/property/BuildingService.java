package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.entities.property.Building;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface BuildingService {

    public Building getBuildingById(Long buildingId);

    List<Building> getBuildingByManagementCompanyId(Long managementCompanyId);

    List<Building> getAllBuildings();

    public Building saveBuilding(Building building);

    public void deleteBuildingById(Long buildingId);

    public Optional<Building> getBuildingByIdWithFloors(Long buildingId);

    public void deleteBuildingByIdWithFloors(Long buildingId);

}
