package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.entities.property.Floor;
import com.cloudsuites.framework.services.property.BuildingService;
import com.cloudsuites.framework.services.property.FloorService;
import com.cloudsuites.framework.webapp.rest.property.dto.FloorDto;
import com.cloudsuites.framework.webapp.rest.property.mapper.FloorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings/{buildingId}/floors")
public class FloorRestController {

    private final FloorService floorService;
    private final FloorMapper mapper;

    @Autowired
    public FloorRestController(FloorService floorService, BuildingService buildingService, FloorMapper mapper) {
        this.floorService = floorService;
        this.mapper = mapper;
    }

    @GetMapping("/{floorId}")
    public ResponseEntity<FloorDto> getFloorById(@PathVariable Long buildingId, @PathVariable Long floorId) throws NotFoundResponseException {
        return ResponseEntity.ok().body(mapper.convertToDTO(floorService.getFloorById(buildingId, floorId)));
    }

    @DeleteMapping("/{floorId}")
    public ResponseEntity<Void> deleteFloorById(@PathVariable Long buildingId, @PathVariable Long floorId) {
        floorService.deleteFloorById(buildingId, floorId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<List<FloorDto>> getAllFloors(@PathVariable Long buildingId) {
        return ResponseEntity.ok().body(mapper.convertToDTOList(floorService.getAllFloors(buildingId)));
    }

   @PostMapping("")
    public ResponseEntity<FloorDto> saveFloor(@PathVariable Long buildingId, @RequestBody FloorDto floorDTO) {
        Floor floor = mapper.convertToEntity(floorDTO);
        floor = floorService.saveFloor(buildingId, floor);
        return ResponseEntity.ok().body(mapper.convertToDTO(floor));
    }


}
