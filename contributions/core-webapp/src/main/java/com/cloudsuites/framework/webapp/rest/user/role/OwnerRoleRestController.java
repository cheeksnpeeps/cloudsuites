package com.cloudsuites.framework.webapp.rest.user.role;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.entities.OwnerRole;
import com.cloudsuites.framework.services.property.personas.entities.OwnerStatus;
import com.cloudsuites.framework.services.property.personas.service.role.OwnerRoleService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tags(value = {@Tag(name = "Owner Roles Management", description = "Operations related to owner roles")})
public class OwnerRoleRestController {

    private static final Logger logger = LoggerFactory.getLogger(OwnerRoleRestController.class);
    private final OwnerRoleService ownerRoleService;
    private final OwnerMapper mapper;

    @Autowired
    public OwnerRoleRestController(OwnerRoleService ownerRoleService, OwnerMapper mapper) {
        this.ownerRoleService = ownerRoleService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAuthority('BUILDING_SUPERVISOR')")
    @Operation(summary = "Get Owner by ID", description = "Retrieve owner details based on their ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Owner not found")
    @JsonView(Views.RoleView.class)
    @GetMapping("/owners/{ownerId}/roles")
    public ResponseEntity<OwnerDto> getOwnerRoleById(
            @Parameter(description = "ID of the owner to be retrieved") @PathVariable String ownerId) throws NotFoundResponseException {
        logger.debug("Getting owner {}", ownerId);
        Owner owner = ownerRoleService.getOwnerRole(ownerId);
        logger.debug("Found owner {}", ownerId);
        return ResponseEntity.ok().body(mapper.convertToDTO(owner));
    }

    @PreAuthorize("hasAuthority('BUILDING_SUPERVISOR')")
    @Operation(summary = "Update Owner Role", description = "Update the role of an existing owner")
    @ApiResponse(responseCode = "200", description = "Owner role updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Owner not found")
    @JsonView(Views.RoleView.class)
    @PutMapping("/owners/{ownerId}/roles")
    public ResponseEntity<OwnerDto> updateOwnerRole(
            @Parameter(description = "ID of the owner to be updated") @PathVariable String ownerId,
            @Valid @RequestBody @Parameter(description = "Owner role payload") OwnerDto ownerDto) throws NotFoundResponseException {
        logger.debug("Updating owner role for {}", ownerId);
        Owner updatedOwner = ownerRoleService.updateOwnerRole(ownerId, ownerDto.getRole());
        logger.debug("Owner role updated successfully: {} {}", updatedOwner.getOwnerId(), updatedOwner.getRole());
        return ResponseEntity.ok().body(mapper.convertToDTO(updatedOwner));
    }

    @PreAuthorize("hasAuthority('BUILDING_SUPERVISOR')")
    @Operation(summary = "Delete Owner Role", description = "Delete the role of an owner by ID")
    @ApiResponse(responseCode = "204", description = "Owner role deleted successfully")
    @ApiResponse(responseCode = "404", description = "Owner not found")
    @DeleteMapping("/owners/{ownerId}/roles")
    public ResponseEntity<Void> deleteOwnerRole(
            @Parameter(description = "ID of the owner whose role is to be deleted") @PathVariable String ownerId) throws NotFoundResponseException {
        logger.debug("Deleting owner role for {}", ownerId);
        ownerRoleService.deleteOwnerRole(ownerId);
        logger.debug("Owner role deleted successfully: {}", ownerId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('BUILDING_SUPERVISOR')")
    @Operation(summary = "Get All Owners Roles", description = "Retrieve all owners roles")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @JsonView(Views.RoleView.class)
    @GetMapping("/owners/roles")
    public ResponseEntity<List<OwnerDto>> getOwnersRoles() {
        logger.debug("Getting all owners roles");
        List<Owner> owners = ownerRoleService.getOwnersByRole();
        logger.debug("Found {} owners roles", owners.size());
        return ResponseEntity.ok().body(mapper.convertToDTOList(owners));
    }

    @PreAuthorize("hasAuthority('BUILDING_SUPERVISOR')")
    @Operation(summary = "Get Owners by Role", description = "Retrieve all owners with a specific role")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @JsonView(Views.RoleView.class)
    @GetMapping("/owners/roles/{ownerRole}")
    public ResponseEntity<List<OwnerDto>> getOwnersByRole(
            @Parameter(description = "Owner role to filter by") @PathVariable OwnerRole ownerRole) {
        logger.debug("Getting owners with role {}", ownerRole);
        List<Owner> owners = ownerRoleService.getOwnersByRole(ownerRole);
        logger.debug("Found {} owners with role {}", owners.size(), ownerRole);
        return ResponseEntity.ok().body(mapper.convertToDTOList(owners));
    }

    @PreAuthorize("hasAuthority('BUILDING_SUPERVISOR')")
    @Operation(summary = "Get Owners by Role and Status", description = "Retrieve all owners with a specific role and status")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @JsonView(Views.RoleView.class)
    @GetMapping("/owners/roles/{ownerRole}/status/{status}")
    public ResponseEntity<List<OwnerDto>> getOwnersByRoleAndStatus(
            @Parameter(description = "Owner role to filter by") @PathVariable OwnerRole ownerRole,
            @Parameter(description = "Owner status to filter by") @PathVariable OwnerStatus status) {
        logger.debug("Getting owners with role {} and status {}", ownerRole, status);
        List<Owner> owners = ownerRoleService.getOwnersByRoleAndStatus(ownerRole, status);
        logger.debug("Found {} owners with role {} and status {}", owners.size(), ownerRole, status);
        return ResponseEntity.ok().body(mapper.convertToDTOList(owners));
    }
}
