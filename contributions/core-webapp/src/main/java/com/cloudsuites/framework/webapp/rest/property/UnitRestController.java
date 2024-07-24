package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.UnitService;
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

    @Operation(summary = "Get All Units for a Building", description = "Retrieve all units for a specific building")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Building not found")
    @JsonView(Views.UnitView.class)
    @GetMapping("/units")
    public ResponseEntity<List<UnitDto>> getAllUnits(
            @Parameter(description = "ID of the building") @PathVariable String buildingId) {
        logger.debug("Getting all units for building: {}", buildingId);
        List<Unit> units = unitService.getAllUnits(buildingId);
        logger.debug("Found {} units for building: {}", units.size(), buildingId);
        return ResponseEntity.ok().body(unitMapper.convertToDTOList(units));
    }

    @Operation(summary = "Get a Unit by ID", description = "Retrieve unit details based on building ID and unit ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Building or unit not found")
    @JsonView(Views.UnitView.class)
    @GetMapping("/units/{unitId}")
    public ResponseEntity<UnitDto> getUnitById(
            @Parameter(description = "ID of the building") @PathVariable String buildingId,
            @Parameter(description = "ID of the unit to be retrieved") @PathVariable String unitId)
            throws NotFoundResponseException {
        logger.debug("Getting unit for building: {} and unit: {}", buildingId, unitId);
        Unit unit = unitService.getUnitById(buildingId, unitId);
        logger.debug("Found unit for building: {} and unit: {}", buildingId, unitId);
        logger.debug("Unit tenants: {}", unit.getTenants().size());
        return ResponseEntity.ok().body(unitMapper.convertToDTO(unit));
    }

    @Operation(summary = "Delete a Unit by ID", description = "Delete a unit based on building ID and unit ID")
    @ApiResponse(responseCode = "204", description = "Unit deleted successfully")
    @ApiResponse(responseCode = "404", description = "Building or unit not found")
    @DeleteMapping("/units/{unitId}")
    public ResponseEntity<Void> deleteUnitById(
            @Parameter(description = "ID of the building") @PathVariable String buildingId,
            @Parameter(description = "ID of the unit to be deleted") @PathVariable String unitId) {
        logger.debug("Deleting unit for building: {} and unit: {}", buildingId, unitId);
        unitService.deleteUnitById(buildingId, "", unitId);
        logger.debug("Unit deleted successfully for building: {} and unit: {}", buildingId, unitId);
        return ResponseEntity.noContent().build();
    }

}
