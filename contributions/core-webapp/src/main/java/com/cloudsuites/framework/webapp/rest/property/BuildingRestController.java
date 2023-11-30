package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.entities.property.Building;
import com.cloudsuites.framework.services.property.BuildingService;
import com.cloudsuites.framework.webapp.rest.property.dto.BuildingDTO;
import com.cloudsuites.framework.webapp.rest.property.mapper.BuildingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings")
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
    public List<BuildingDTO> getBuildings(@RequestParam(required = false) Long managementCompanyId) {
        if (managementCompanyId != null) {
            // If managementCompanyId is provided, filter buildings by management company
            List<Building> buildings = buildingService.getBuildingByManagementCompanyId(managementCompanyId);
            return mapper.convertToDTOList(buildings);
        } else {
            // If managementCompanyId is not provided, return all buildings
            List<Building> buildings = buildingService.getAllBuildings();
            return mapper.convertToDTOList(buildings);
        }
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


}

