package com.cloudsuites.framework.modules.property;
import com.cloudsuites.framework.modules.property.repository.BuildingRepository;
import com.cloudsuites.framework.services.common.entities.property.Building;
import com.cloudsuites.framework.services.property.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
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
}