package com.cloudsuites.framework.modules.property;
import com.cloudsuites.framework.modules.property.repository.BuildingRepository;
import com.cloudsuites.framework.services.entities.property.Building;
import com.cloudsuites.framework.services.entities.property.Floor;
import com.cloudsuites.framework.services.property.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;

    @Autowired
    public BuildingServiceImpl(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @Override
    public Building getBuildingById(Long buildingId) {
        return buildingRepository.findById(buildingId).orElse(null);
    }

    @Override
    public List<Building> getBuildingByManagementCompanyId(Long managementCompanyId) {
        return buildingRepository.findByManagementCompany_ManagementCompanyId(managementCompanyId);
    }
    @Override
    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }

    @Override
    public Building saveBuilding(Building building) {
        return buildingRepository.save(building);
    }

    @Override
    public void deleteBuildingById(Long buildingId) {
        buildingRepository.deleteById(buildingId);
    }

    public Optional<Building> getBuildingByIdWithFloors(Long buildingId) {
        return buildingRepository.findById(buildingId)
                .map(building -> {
                    // Force fetching of floors
                    List<Floor> floors = building.getFloors();
                    // Now 'floors' should be populated with the actual data.
                    return building;
                });
    }

    @Override
    public void deleteBuildingByIdWithFloors(Long buildingId) {
        buildingRepository.deleteById(buildingId);
    }
}