package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.service.OwnerService;
import com.cloudsuites.framework.webapp.authentication.util.WebAppConstants;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.user.dto.OwnerDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.OwnerMapper;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/owners")
@Tags(value = {@Tag(name = "Owners", description = "Operations related to owners")})
public class OwnerRestController {

    private static final Logger logger = LoggerFactory.getLogger(OwnerRestController.class);
    private final OwnerService ownerService;
    private final OwnerMapper mapper;
    private final UnitService unitService;

    @Autowired
    public OwnerRestController(OwnerService ownerService, OwnerMapper mapper, UnitService unitService) {
        this.ownerService = ownerService;
        this.mapper = mapper;
        this.unitService = unitService;
    }

    @Operation(summary = "Get All Owners", description = "Retrieve all owners")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Owners not found")
    @GetMapping("")
    @JsonView(Views.OwnerView.class)
    public ResponseEntity<List<OwnerDto>> getAllOwners() throws NotFoundResponseException {
        logger.info(WebAppConstants.Owner.LOG_FETCHING_OWNERS);
        List<Owner> owners = ownerService.getAllOwners();
        logger.info("Fetched {} owners", owners.size());
        return ResponseEntity.ok(mapper.convertToDTOList(owners));
    }

    @Operation(summary = "Get Owner by ID", description = "Retrieve owner details by ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Owner not found")
    @GetMapping("/{ownerId}")
    @JsonView(Views.OwnerView.class)
    public ResponseEntity<OwnerDto> getOwnerById(@Parameter(description = "ID of the owner to be retrieved") @PathVariable String ownerId) {
        logger.info(WebAppConstants.Owner.LOG_FETCHING_OWNER_BY_ID, ownerId);
        try {
            Owner owner = ownerService.getOwnerById(ownerId);
            logger.info(WebAppConstants.Owner.LOG_OWNER_FETCHED, owner.getOwnerId());
            return ResponseEntity.ok(mapper.convertToDTO(owner));
        } catch (NotFoundResponseException e) {
            logger.error(WebAppConstants.Owner.LOG_OWNER_NOT_FOUND, ownerId);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create Owner", description = "Create owner with details")
    @ApiResponse(responseCode = "201", description = "Owner created successfully", content = @Content(mediaType = "application/json"))
    @PostMapping("")
    @JsonView(Views.OwnerView.class)
    public ResponseEntity<OwnerDto> createOwner(@Valid @RequestBody @Parameter(description = "Owner details") OwnerDto ownerDto) {
        logger.debug(WebAppConstants.Owner.LOG_REGISTERING_OWNER, ownerDto.getIdentity().getUsername());
        Owner owner = mapper.convertToEntity(ownerDto);
        owner = ownerService.createOwner(owner);
        logger.info(WebAppConstants.Owner.LOG_OWNER_REGISTERED_SUCCESS, owner.getOwnerId(), ownerDto.getIdentity().getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(owner));
    }

    @Operation(summary = "Update Owner by ID", description = "Update owner details by ID")
    @ApiResponse(responseCode = "200", description = "Owner updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Owner not found")
    @PutMapping("/{ownerId}")
    @JsonView(Views.OwnerView.class)
    public ResponseEntity<OwnerDto> updateOwner(
            @Parameter(description = "ID of the owner to be updated") @PathVariable String ownerId,
            @Valid @RequestBody @Parameter(description = "Updated owner details") OwnerDto ownerDto) throws NotFoundResponseException {
        logger.info(WebAppConstants.Owner.LOG_UPDATING_OWNER, ownerId);
            Owner owner = mapper.convertToEntity(ownerDto);
            owner = ownerService.updateOwner(ownerId, owner);
            logger.info(WebAppConstants.Owner.LOG_OWNER_UPDATED, ownerId);
            return ResponseEntity.ok(mapper.convertToDTO(owner));
    }

    @Operation(summary = "Delete Owner by ID", description = "Delete an owner by ID")
    @ApiResponse(responseCode = "204", description = "Owner deleted successfully")
    @ApiResponse(responseCode = "404", description = "Owner not found")
    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deleteOwner(@Parameter(description = "ID of the owner to be deleted") @PathVariable String ownerId) {
        logger.info(WebAppConstants.Owner.LOG_DELETING_OWNER, ownerId);
        try {
            ownerService.deleteOwner(ownerId);
            logger.info(WebAppConstants.Owner.LOG_OWNER_DELETED, ownerId);
            return ResponseEntity.noContent().build();
        } catch (NotFoundResponseException e) {
            logger.error(WebAppConstants.Owner.LOG_OWNER_NOT_FOUND, ownerId);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Add Unit to Owner", description = "Add an existing unit to an owner by owner ID, building ID, and unit ID. This operation transfers ownership of the unit to the specified owner.")
    @ApiResponse(responseCode = "200", description = "Unit added to owner successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Owner or Unit not found")
    @PostMapping("/{ownerId}/buildings/{buildingId}/units/{unitId}/transfer")
    @JsonView(Views.OwnerView.class)
    public ResponseEntity<OwnerDto> addUnitToOwner(
            @Parameter(description = "ID of the owner") @PathVariable String ownerId,
            @Parameter(description = "ID of the building to be added") @PathVariable String buildingId,
            @Parameter(description = "ID of the unit to be added") @PathVariable String unitId) throws NotFoundResponseException {
        logger.info(WebAppConstants.Owner.LOG_ADDING_UNIT_TO_OWNER, ownerId, buildingId, unitId);
            Owner owner = ownerService.getOwnerById(ownerId);
            Unit unit = unitService.getUnitById(buildingId, unitId);
            if (unit.getOwner() != null) {
                logger.info(WebAppConstants.Owner.LOG_REMOVING_UNIT_FROM_PREVIOUS_OWNER, unit.getOwner().getOwnerId());
                unit.getOwner().getUnits().remove(unit);
            } else {
                owner.setUnits(new ArrayList<>());
            }
            if (owner.getUnits().stream().noneMatch(u -> u.getUnitId().equals(unitId))) {
                logger.info(WebAppConstants.Owner.LOG_UNIT_ADDED_TO_OWNER, ownerId);
                owner.getUnits().add(unit);
            }
            unit.setOwner(owner);
            ownerService.updateOwner(ownerId, owner);
            unitService.saveUnit(unit);
            logger.info(WebAppConstants.Owner.LOG_UNIT_ADDED_SUCCESSFULLY, ownerId);
            return ResponseEntity.ok(mapper.convertToDTO(owner));
    }

    @Operation(summary = "Remove Unit from Owner", description = "Remove a unit from an owner by ID")
    @ApiResponse(responseCode = "200", description = "Unit removed successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Owner or Unit not found")
    @DeleteMapping("/{ownerId}/units/{unitId}")
    @JsonView(Views.OwnerView.class)
    public ResponseEntity<OwnerDto> removeUnitFromOwner(
            @Parameter(description = "ID of the owner") @PathVariable String ownerId,
            @Parameter(description = "ID of the unit to be removed") @PathVariable String unitId) throws NotFoundResponseException {
        logger.info(WebAppConstants.Owner.LOG_REMOVING_UNIT_FROM_OWNER, ownerId, unitId);
            Owner owner = ownerService.getOwnerById(ownerId);
            owner.getUnits().removeIf(unit -> unit.getUnitId().equals(unitId));
            Owner updatedOwner = ownerService.updateOwner(ownerId, owner);
            logger.info(WebAppConstants.Owner.LOG_UNIT_REMOVED_SUCCESSFULLY, ownerId);
            return ResponseEntity.ok(mapper.convertToDTO(updatedOwner));
    }
}