package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.entities.property.Building;
import com.cloudsuites.framework.services.property.BuildingService;
import com.cloudsuites.framework.webapp.rest.property.dto.BuildingDTO;
import com.cloudsuites.framework.webapp.rest.property.mapper.BuildingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api/buildings")
public class BuildingRestController {

    private final BuildingService buildingService;
    private final BuildingMapper mapper;

    @Autowired
    public BuildingRestController(BuildingService buildingService, BuildingMapper mapper) {
        this.buildingService = buildingService;
        this.mapper = mapper;
    }

    @GetMapping("/{buildingId}")
    public BuildingDTO getBuildingById(@PathVariable Long buildingId) {
        Building building = buildingService.getBuildingById(buildingId);
        return mapper.convertToDTO(building);
    }

    @GetMapping("/")
    public List<BuildingDTO> getAllBuildings() {
        List<Building> buildings = buildingService.getAllBuildings();
        return mapper.convertToDTOList(buildings);
    }

    @PostMapping("/")
    public BuildingDTO saveBuilding(@RequestBody BuildingDTO buildingDTO) {
        Building building = mapper.convertToEntity(buildingDTO);
        building = buildingService.saveBuilding(building);
        return mapper.convertToDTO(building);
    }

    @DeleteMapping("/{buildingId}")
    public void deleteBuildingById(@PathVariable Long buildingId) {
        buildingService.deleteBuildingById(buildingId);
    }

    @GetMapping("/")
    public List<BuildingDTO> getBuildingByPropertyManagementCompanyId(@RequestParam Long companyId) {
        List<Building> buildings = buildingService.getBuildingByPropertyManagementCompanyId(companyId);
        return mapper.convertToDTOList(buildings);
    }

}

