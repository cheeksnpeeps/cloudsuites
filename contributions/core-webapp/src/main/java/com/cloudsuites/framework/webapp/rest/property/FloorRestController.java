package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.entities.property.Floor;
import com.cloudsuites.framework.services.property.BuildingService;
import com.cloudsuites.framework.services.property.FloorService;
import com.cloudsuites.framework.webapp.rest.property.dto.FloorDTO;
import com.cloudsuites.framework.webapp.rest.property.mapper.FloorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api/buildings/{buildingId}/floors")
public class FloorRestController {

    private final FloorService floorService;
    private final BuildingService buildingService;
    private final FloorMapper mapper;

    @Autowired
    public FloorRestController(FloorService floorService, BuildingService buildingService, FloorMapper mapper) {
        this.floorService = floorService;
        this.buildingService = buildingService;
        this.mapper = mapper;
    }

    @GetMapping("/{floorId}")
    public FloorDTO getFloorById(@PathVariable Long buildingId, @PathVariable Long floorId) {
        Floor floor = floorService.getFloorById(buildingId, floorId);
        return mapper.convertToDTO(floor);
    }

    @GetMapping("/")
    public List<FloorDTO> getAllFloors(@PathVariable Long buildingId) {
        List<Floor> floors = floorService.getAllFloors(buildingId);
        return mapper.convertToDTOList(floors);
    }

   @PostMapping("/")
    public FloorDTO saveFloor(@PathVariable Long buildingId, @RequestBody FloorDTO floorDTO) {
        Floor floor = mapper.convertToEntity(floorDTO);
        floor.setBuilding(buildingService.getBuildingById(buildingId));
        floor = floorService.saveFloor(buildingId,floor);
        buildingService.addFloor(buildingId, floor);
        return mapper.convertToDTO(floor);
    }

    @DeleteMapping("/{floorId}")
    public void deleteFloorById(@PathVariable Long buildingId, @PathVariable Long floorId) {
        floorService.deleteFloorById(buildingId, floorId);
    }
}
