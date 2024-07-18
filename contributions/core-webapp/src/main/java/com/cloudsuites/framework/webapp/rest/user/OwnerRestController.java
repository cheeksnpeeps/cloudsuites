package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.OwnerService;
import com.cloudsuites.framework.services.property.UnitService;
import com.cloudsuites.framework.services.property.entities.Owner;
import com.cloudsuites.framework.services.property.entities.Unit;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/owners")
@Tags(value = {@Tag(name = "Owners", description = "Operations related to owners")})
public class OwnerRestController {

    private static final Logger logger = LoggerFactory.getLogger(OwnerRestController.class);
    private final OwnerService ownerService;
    private final OwnerMapper mapper;
    private final UnitService unitService;
    private final UserService userService;

    @Autowired
    public OwnerRestController(OwnerService ownerService, OwnerMapper mapper, UnitService unitService, UserService userService) {
        this.ownerService = ownerService;
        this.mapper = mapper;
        this.unitService = unitService;
        this.userService = userService;
    }

    @Operation(summary = "Get All Owners", description = "Retrieve all owners")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Owners not found")
    @GetMapping("")
    @JsonView(Views.OwnerView.class)
    public ResponseEntity<List<OwnerDto>> getAllOwners() {
        logger.info("Fetching all owners");
        try {
            List<Owner> owners = ownerService.getAllOwners();
            logger.info("Fetched {} owners", owners.size());
            return ResponseEntity.ok().body(mapper.convertToDTOList(owners));
        } catch (NotFoundResponseException e) {
            logger.error("Owners not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create a new Owner", description = "Create a new owner")
    @ApiResponse(responseCode = "201", description = "Owner created successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @PostMapping("")
    @JsonView(Views.OwnerView.class)
    public ResponseEntity<OwnerDto> createOwner(@RequestBody @Parameter(description = "Owner details to be saved") OwnerDto ownerDto) {
        logger.info("Creating a new owner");
        Owner owner = mapper.convertToEntity(ownerDto);
        Identity identity = userService.createUser(owner.getIdentity());
        owner.setIdentity(identity);
        Owner createdOwner = ownerService.createOwner(owner);
        logger.info("Owner created with ID: {}", createdOwner.getOwnerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(createdOwner));
    }

    @Operation(summary = "Get Owner by ID", description = "Retrieve owner details by ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Owner not found")
    @GetMapping("/{id}")
    @JsonView(Views.OwnerView.class)
    public ResponseEntity<OwnerDto> getOwnerById(@Parameter(description = "ID of the owner to be retrieved") @PathVariable Long id) {
        logger.info("Fetching owner with ID: {}", id);
        try {
            Owner owner = ownerService.getOwnerById(id);
            logger.info("Owner fetched with ID: {}", owner.getOwnerId());
            return ResponseEntity.ok().body(mapper.convertToDTO(owner));
        } catch (NotFoundResponseException e) {
            logger.error("Owner not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update Owner by ID", description = "Update owner details by ID")
    @ApiResponse(responseCode = "200", description = "Owner updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Owner not found")
    @PutMapping("/{id}")
    @JsonView(Views.OwnerView.class)
    public ResponseEntity<OwnerDto> updateOwner(@Parameter(description = "ID of the owner to be updated") @PathVariable Long id,
                                                @RequestBody @Parameter(description = "Updated owner details") OwnerDto ownerDto) {
        logger.info("Updating owner with ID: {}", id);
        try {
            Owner owner = mapper.convertToEntity(ownerDto);
            owner = ownerService.updateOwner(id, owner);
            logger.info("Owner updated successfully with ID: {}", id);
            return ResponseEntity.ok().body(mapper.convertToDTO(owner));
        } catch (NotFoundResponseException e) {
            logger.error("Owner not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete Owner by ID", description = "Delete an owner by ID")
    @ApiResponse(responseCode = "204", description = "Owner deleted successfully")
    @ApiResponse(responseCode = "404", description = "Owner not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwner(@Parameter(description = "ID of the owner to be deleted") @PathVariable Long id) {
        logger.info("Deleting owner with ID: {}", id);
        try {
            ownerService.deleteOwner(id);
            logger.info("Owner deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundResponseException e) {
            logger.error("Owner not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Add Unit to Owner", description = "Add an existing unit to an owner by owner ID, building ID, and unit ID. " +
            "This operation transfers ownership of the unit to the specified owner.")
    @ApiResponse(responseCode = "200", description = "Unit added to owner successfully",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Owner or Unit not found")
    @PostMapping("/{ownerId}/buildings/{buildingId}/units/{unitId}/transfer")
    @JsonView(Views.OwnerView.class)
    public ResponseEntity<OwnerDto> addUnitToOwner(
            @Parameter(description = "ID of the owner") @PathVariable Long ownerId,
            @Parameter(description = "ID of the building to be added") @PathVariable Long buildingId,
            @Parameter(description = "ID of the unit to be added") @PathVariable Long unitId) {
        logger.info("Adding unit to owner with ownerId={}, buildingId={}, unitId={}", ownerId, buildingId, unitId);
        try {
            logger.info("Fetching owner with ID: {}", ownerId);
            Owner owner = ownerService.getOwnerById(ownerId);
            logger.info("Owner fetched with ID: {}", owner.getOwnerId());

            logger.info("Fetching unit with building ID: {} and unit ID: {}", buildingId, unitId);
            Unit unit = unitService.getUnitById(buildingId, unitId);
            logger.info("Unit fetched with ID: {}", unit.getUnitId());

            // Remove unit from previous owner, if any
            if (unit.getOwner() != null) {
                logger.info("Removing unit from previous owner with ID: {}", unit.getOwner().getOwnerId());
                unit.getOwner().getUnits().remove(unit);
            }

            // Transfer unit to new owner
            if (owner.getUnits().stream().noneMatch(
                    u -> u.getUnitId().equals(unitId) && u.getBuilding().getBuildingId().equals(buildingId)
            )) {
                logger.info("Adding unit to new owner with ID: {}", ownerId);
                owner.getUnits().add(unit);
            } else {
                logger.info("Unit already exists for owner with ID: {}", ownerId);
            }
            unit.setOwner(owner);

            // Save updates
            logger.info("Updating owner with ID: {}", ownerId);
            Owner updatedOwner = ownerService.updateOwner(ownerId, owner);

            logger.info("Saving unit with building ID: {} and floor ID: {}", buildingId, unit.getFloor().getFloorId());
            unitService.saveUnit(buildingId, unit.getFloor().getFloorId(), unit);

            logger.info("Unit successfully added to owner with ID: {}", updatedOwner.getOwnerId());
            return ResponseEntity.ok().body(mapper.convertToDTO(updatedOwner));
        } catch (NotFoundResponseException e) {
            logger.error("Owner or Unit not found: ownerId={}, buildingId={}, unitId={}", ownerId, buildingId, unitId);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Remove Unit from Owner", description = "Remove a unit from an owner by ID")
    @ApiResponse(responseCode = "200", description = "Unit removed successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Owner or Unit not found")
    @DeleteMapping("/{ownerId}/units/{unitId}")
    @JsonView(Views.OwnerView.class)
    public ResponseEntity<OwnerDto> removeUnitFromOwner(@Parameter(description = "ID of the owner") @PathVariable Long ownerId,
                                                        @Parameter(description = "ID of the unit to be removed") @PathVariable Long unitId) {
        logger.info("Removing unit from owner with ownerId={}, unitId={}", ownerId, unitId);
        try {
            Owner owner = ownerService.getOwnerById(ownerId);
            owner.getUnits().removeIf(unit -> unit.getUnitId().equals(unitId));
            Owner updatedOwner = ownerService.updateOwner(ownerId, owner);
            logger.info("Unit successfully removed from owner with ID: {}", ownerId);
            return ResponseEntity.ok().body(mapper.convertToDTO(updatedOwner));
        } catch (NotFoundResponseException e) {
            logger.error("Owner or Unit not found: ownerId={}, unitId={}", ownerId, unitId);
            return ResponseEntity.notFound().build();
        }
    }
}
