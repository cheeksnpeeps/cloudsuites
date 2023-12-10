package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.entities.property.Floor;
import com.cloudsuites.framework.services.property.BuildingService;
import com.cloudsuites.framework.services.property.FloorService;
import com.cloudsuites.framework.webapp.rest.property.dto.FloorDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.property.mapper.FloorMapper;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buildings/{buildingId}/floors")
public class FloorRestController {

    private final FloorService floorService;
    private final FloorMapper mapper;

    @Autowired
    public FloorRestController(FloorService floorService, BuildingService buildingService, FloorMapper mapper) {
        this.floorService = floorService;
        this.mapper = mapper;
    }

    @Operation(summary = "Get All Floors", description = "Retrieve all floors for a building based on its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Building not found")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @Tags(value = {@Tag(name = "Floors", description = "Operations related to floors")})
    @JsonView(Views.UnitView.class)
    @GetMapping("")
    public ResponseEntity<List<FloorDto>> getAllFloors(
            @Parameter(description = "ID of the building to retrieve all floors") @PathVariable Long buildingId) {

        return ResponseEntity.ok().body(mapper.convertToDTOList(floorService.getAllFloors(buildingId)));
    }

    @Operation(summary = "Save a Floor", description = "Create a new floor for a building based on its ID")
    @ApiResponse(responseCode = "201", description = "Floor created successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Building not found")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @Tags(value = {@Tag(name = "Floors", description = "Operations related to floors")})
    @JsonView(Views.UnitView.class)
    @PostMapping("")
    public ResponseEntity<FloorDto> saveFloor(
            @Parameter(description = "ID of the building to save the floor") @PathVariable Long buildingId,
            @RequestBody @Parameter(description = "Floor details to be saved") FloorDto floorDTO) {

        Floor floor = mapper.convertToEntity(floorDTO);
        floor = floorService.saveFloor(buildingId, floor);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(floor));
    }
    @Operation(summary = "Get a Floor by ID", description = "Retrieve floor details based on building ID and floor ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Building or floor not found")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @Tags(value = {@Tag(name = "Floors", description = "Operations related to floors")})
    @JsonView(Views.UnitView.class)
    @GetMapping("/{floorId}")
    public ResponseEntity<FloorDto> getFloorById(
            @Parameter(description = "ID of the building") @PathVariable Long buildingId,
            @Parameter(description = "ID of the floor to be retrieved") @PathVariable Long floorId)
            throws NotFoundResponseException {

        return ResponseEntity.ok().body(mapper.convertToDTO(floorService.getFloorById(buildingId, floorId)));
    }


    @Operation(summary = "Delete a Floor by ID", description = "Delete a floor based on building ID and floor ID")
    @ApiResponse(responseCode = "204", description = "Floor deleted successfully")
    @ApiResponse(responseCode = "404", description = "Building or floor not found")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @Tags(value = {@Tag(name = "Floors", description = "Operations related to floors")})
    @DeleteMapping("/{floorId}")
    public ResponseEntity<Void> deleteFloorById(
            @Parameter(description = "ID of the building") @PathVariable Long buildingId,
            @Parameter(description = "ID of the floor to be deleted") @PathVariable Long floorId) {

        floorService.deleteFloorById(buildingId, floorId);
        return ResponseEntity.noContent().build();
    }
}
