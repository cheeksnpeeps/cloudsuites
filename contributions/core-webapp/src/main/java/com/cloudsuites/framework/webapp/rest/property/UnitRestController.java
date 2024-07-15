package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.UnitService;
import com.cloudsuites.framework.services.property.entities.Unit;
import com.cloudsuites.framework.webapp.rest.property.dto.UnitDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.property.mapper.UnitMapper;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buildings/{buildingId}")
@Tags(value = {@Tag(name = "Units", description = "Operations related to units")})
public class UnitRestController {

    private static final Logger logger = LoggerFactory.getLogger(UnitRestController.class);

    public final UnitService unitService;
    public final UnitMapper unitMapper;
    @Autowired
    public UnitRestController(UnitService unitService, UnitMapper unitMapper) {
        this.unitService = unitService;
        this.unitMapper = unitMapper;
    }

    @Operation(summary = "Get All Units by Floor", description = "Retrieve all units for a floor based on building ID and floor ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Building or floor not found")
    @JsonView(Views.UnitView.class)
    @GetMapping("/floors/{floorId}/units")
    public ResponseEntity<List<UnitDto>> getAllUnitsByFloor(
            @Parameter(description = "ID of the building") @PathVariable Long buildingId,
            @Parameter(description = "ID of the floor") @PathVariable Long floorId)
            throws NotFoundResponseException {
        logger.debug("Getting all units for building: {} and floor: {}", buildingId, floorId);
        List<UnitDto> units = unitMapper.convertToDTOList(unitService.getAllUnitsByFloor(buildingId, floorId));
        logger.debug("Found {} units for building: {} and floor: {}", units.size(), buildingId, floorId);
        return ResponseEntity.ok().body(units);
    }


    @Operation(summary = "Save a Unit", description = "Create a new unit for a floor based on building ID and floor ID")
    @ApiResponse(responseCode = "201", description = "Unit created successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Building or floor not found")
    @JsonView(Views.UnitView.class)
    @PostMapping("/floors/{floorId}/units")
    public ResponseEntity<UnitDto> saveUnit(
            @Parameter(description = "ID of the building") @PathVariable Long buildingId,
            @Parameter(description = "ID of the floor") @PathVariable Long floorId,
            @RequestBody @Parameter(description = "Unit details to be saved") UnitDto unitDTO) {
        logger.debug("Saving unit for building: {} and floor: {}", buildingId, floorId);
        UnitDto unit = unitMapper.convertToDTO(unitService.saveUnit(buildingId, floorId, unitMapper.convertToEntity(unitDTO)));
        logger.debug("Unit saved successfully for building: {} and floor: {}", buildingId, floorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(unit);
    }


    @Operation(summary = "Delete a Unit by ID", description = "Delete a unit based on building ID and unit ID")
    @ApiResponse(responseCode = "204", description = "Unit deleted successfully")
    @ApiResponse(responseCode = "404", description = "Building or unit not found")
    @DeleteMapping("/units/{unitId}")
    public ResponseEntity<Void> deleteUnitById(
            @Parameter(description = "ID of the building") @PathVariable Long buildingId,
            @Parameter(description = "ID of the unit to be deleted") @PathVariable Long unitId) {
        logger.debug("Deleting unit for building: {} and unit: {}", buildingId, unitId);
        unitService.deleteUnitById(buildingId, unitId);
        logger.debug("Unit deleted successfully for building: {} and unit: {}", buildingId, unitId);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Get a Unit by ID", description = "Retrieve unit details based on building ID and unit ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Building or unit not found")
    @JsonView(Views.UnitView.class)
    @GetMapping("/units/{unitId}")
    public ResponseEntity<UnitDto> getUnitById(
            @Parameter(description = "ID of the building") @PathVariable Long buildingId,
            @Parameter(description = "ID of the unit to be retrieved") @PathVariable Long unitId)
            throws NotFoundResponseException {
        logger.debug("Getting unit for building: {} and unit: {}", buildingId, unitId);
        Unit unit = unitService.getUnitById(buildingId, unitId);
        logger.debug("Found unit for building: {} and unit: {}", buildingId, unitId);
        return ResponseEntity.ok().body(unitMapper.convertToDTO(unit));
    }

    @Operation(summary = "Get All Units for a Building", description = "Retrieve all units for a specific building")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Building not found")
    @JsonView(Views.UnitView.class)
    @GetMapping("/units")
    public ResponseEntity<List<UnitDto>> getAllUnits(
            @Parameter(description = "ID of the building") @PathVariable Long buildingId) {
        logger.debug("Getting all units for building: {}", buildingId);
        List<Unit> units = unitService.getAllUnits(buildingId);
        logger.debug("Found {} units for building: {}", units.size(), buildingId);
        return ResponseEntity.ok().body(unitMapper.convertToDTOList(units));
    }

}
