package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.entities.property.Building;
import com.cloudsuites.framework.services.property.BuildingService;
import com.cloudsuites.framework.webapp.rest.property.dto.BuildingDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.property.mapper.BuildingMapper;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


    @JsonView(Views.UnitView.class)
    @GetMapping("")
    public ResponseEntity<List<BuildingDto>> getBuildings(@RequestParam(required = false) Long managementCompanyId) throws NotFoundResponseException {
        if (managementCompanyId != null) {
            // If managementCompanyId is provided, filter buildings by management company
            List<Building> buildings = buildingService.getBuildingByManagementCompanyId(managementCompanyId);
            return ResponseEntity.ok().body(mapper.convertToDTOList(buildings));
        }
            // If managementCompanyId is not provided, return all buildings
        List<Building> buildings = buildingService.getAllBuildings();
        return ResponseEntity.ok().body(mapper.convertToDTOList(buildings));
    }

    @JsonView(Views.BuildingView.class)
    @PostMapping("")
    public ResponseEntity<BuildingDto> saveBuilding(@RequestBody BuildingDto buildingDTO) {
        Building building = mapper.convertToEntity(buildingDTO);
        building = buildingService.saveBuilding(building);
        return ResponseEntity.ok().body(mapper.convertToDTO(building));
    }

    @DeleteMapping("/{buildingId}")
    public ResponseEntity<Void> deleteBuildingById(@PathVariable Long buildingId) {
        buildingService.deleteBuildingById(buildingId);
        return ResponseEntity.ok().build();
    }

    @JsonView(Views.UnitView.class)
    @GetMapping("/{buildingId}")
    public ResponseEntity<BuildingDto> getBuildingById(@PathVariable Long buildingId) throws NotFoundResponseException {
        Building building = buildingService.getBuildingById(buildingId);
        return ResponseEntity.ok().body(mapper.convertToDTO(building));
    }

}

