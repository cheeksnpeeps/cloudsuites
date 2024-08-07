package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Floor;
import com.cloudsuites.framework.services.property.features.service.FloorService;
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
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buildings/{buildingId}/floors")
@Tags(value = {@Tag(name = "Floors", description = "Operations related to floors")})
public class FloorRestController {

    private final FloorService floorService;
    private final FloorMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(FloorRestController.class);

    @Autowired
    public FloorRestController(FloorService floorService, FloorMapper mapper) {
        this.floorService = floorService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Get All Floors", description = "Retrieve all floors for a building based on its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Building not found")
    @JsonView(Views.FloorView.class)
    @GetMapping("")
    public ResponseEntity<List<FloorDto>> getAllFloorsByBuildingId(
            @Parameter(description = "ID of the building to retrieve all floors") @PathVariable String buildingId) throws NotFoundResponseException {
        logger.debug("Fetching all floors for building ID: {}", buildingId);
        List<FloorDto> floors = mapper.convertToDTOList(floorService.getAllFloorsByBuildingId(buildingId));
        logger.info("Successfully retrieved {} floors for building ID: {}", floors.size(), buildingId);
        return ResponseEntity.ok().body(floors);
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Get a Floor by ID", description = "Retrieve floor details based on building ID and floor ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Building or floor not found")
    @JsonView(Views.FloorView.class)
    @GetMapping("/{floorId}")
    public ResponseEntity<FloorDto> getFloorById(
            @Parameter(description = "ID of the building") @PathVariable String buildingId,
            @Parameter(description = "ID of the floor to be retrieved") @PathVariable String floorId) throws NotFoundResponseException {
        logger.debug("Fetching floor ID: {} for building ID: {}", floorId, buildingId);
        FloorDto floor = mapper.convertToDTO(floorService.getFloorById(buildingId, floorId));
        logger.info("Successfully retrieved floor ID: {} for building ID: {}", floorId, buildingId);
        return ResponseEntity.ok().body(floor);
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Save a Floor", description = "Create a new floor for a building based on its ID")
    @ApiResponse(responseCode = "201", description = "Floor created successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Building not found")
    @JsonView(Views.FloorView.class)
    @PostMapping("")
    public ResponseEntity<FloorDto> saveFloor(
            @Parameter(description = "ID of the building to save the floor") @PathVariable String buildingId,
            @Valid @RequestBody @Parameter(description = "Floor details to be saved") FloorDto floorDTO) throws NotFoundResponseException {
        logger.debug("Saving floor for building ID: {} with details: {}", buildingId, floorDTO);
        Floor floor = mapper.convertToEntity(floorDTO);
        floor = floorService.saveFloorAndUnits(buildingId, floor);
        logger.info("Successfully saved floor ID: {} for building ID: {}", floor.getFloorId(), buildingId);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(floor));
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Delete a Floor by ID", description = "Delete a floor based on building ID and floor ID")
    @ApiResponse(responseCode = "204", description = "Floor deleted successfully")
    @ApiResponse(responseCode = "404", description = "Building or floor not found")
    @DeleteMapping("/{floorId}")
    public ResponseEntity<Void> deleteFloorById(
            @Parameter(description = "ID of the building") @PathVariable String buildingId,
            @Parameter(description = "ID of the floor to be deleted") @PathVariable String floorId) throws NotFoundResponseException {
        logger.debug("Deleting floor ID: {} for building ID: {}", floorId, buildingId);
        floorService.deleteFloorById(buildingId, floorId);
        logger.info("Successfully deleted floor ID: {} for building ID: {}", floorId, buildingId);
        return ResponseEntity.noContent().build();
    }
}