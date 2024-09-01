package com.cloudsuites.framework.webapp.rest.amenity;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.MaintenanceStatus;
import com.cloudsuites.framework.services.amenity.service.AmenityService;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityDto;
import com.cloudsuites.framework.webapp.rest.amenity.mapper.AmenityMapper;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
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
@RequestMapping("/api/v1/")
@Tags(value = {@Tag(name = "Amenities", description = "Operations related to amenities")})
public class AmenityRestController {

    private static final Logger logger = LoggerFactory.getLogger(AmenityRestController.class);
    private final AmenityService amenityService;
    private final AmenityMapper mapper;

    @Autowired
    public AmenityRestController(AmenityService amenityService, AmenityMapper mapper) {
        this.amenityService = amenityService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAuthority('ALL_ADMIN')")
    @Operation(summary = "Get Amenities", description = "Get a list of all amenities")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Amenities not found")
    @JsonView(Views.AmenityView.class)
    @GetMapping("/buildings/amenities")
    public ResponseEntity<List<AmenityDto>> getAllAmenities() {
        logger.debug("Getting all amenities");
        List<Amenity> amenities = amenityService.getAllAmenities();
        logger.debug("Found {} amenities", amenities.size());
        return ResponseEntity.ok().body(mapper.convertToDTOList(amenities));
    }

    @PreAuthorize("hasAuthority('ALL_ADMIN')")
    @Operation(summary = "Get Amenities by Building Id", description = "Get a list of all amenities by building ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Amenities not found")
    @JsonView(Views.AmenityView.class)
    @GetMapping("/buildings/{buildingId}/amenities")
    public ResponseEntity<List<AmenityDto>> getAmenitiesByBuildingId(
            @PathVariable String buildingId) {
        logger.debug("Getting amenities by building ID: {}", buildingId);
        List<Amenity> amenities = amenityService.getAmenitiesByBuildingId(buildingId);
        logger.debug("Found {} amenities", amenities.size());
        return ResponseEntity.ok().body(mapper.convertToDTOList(amenities));
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Create an Amenity", description = "Create a new amenity")
    @ApiResponse(responseCode = "201", description = "Amenity created successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @JsonView(Views.AmenityView.class)
    @PostMapping("/amenities")
    public ResponseEntity<AmenityDto> createAmenity(
            @Valid @RequestBody @Parameter(description = "Amenity payload") AmenityDto amenityDto) {
        Amenity amenity = mapper.convertToEntity(amenityDto);
        logger.debug("Creating new amenity: {}", amenity.getName());
        amenity = amenityService.createAmenity(amenity, amenityDto.getBuildingIds());
        logger.debug("Amenity created successfully: {}", amenity.getAmenityId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(amenity));
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Delete an Amenity by ID", description = "Delete an amenity based on its ID")
    @ApiResponse(responseCode = "204", description = "Amenity deleted successfully")
    @ApiResponse(responseCode = "404", description = "Amenity not found")
    @DeleteMapping("/buildings/{buildingId}/amenities/{amenityId}")
    public ResponseEntity<Void> deleteAmenityById(
            @Parameter(description = "ID of the amenity to be deleted") @PathVariable String amenityId,
            @PathVariable String buildingId) {
        logger.debug("Deleting amenity {}", amenityId);
        amenityService.deleteAmenity(amenityId);
        logger.debug("Amenity deleted successfully: {}", amenityId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ALL_STAFF') or hasAuthority('TENANT') or hasAuthority('OWNER')")
    @Operation(summary = "Get an Amenity by ID", description = "Retrieve amenity details based on its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Amenity not found")
    @JsonView(Views.AmenityView.class)
    @GetMapping("/buildings/{buildingId}/amenities/{amenityId}")
    public ResponseEntity<AmenityDto> getAmenityById(
            @Parameter(description = "ID of the amenity to be retrieved") @PathVariable String amenityId,
            @PathVariable String buildingId) throws NotFoundResponseException {
        logger.debug("Getting amenity {}", amenityId);
        Amenity amenity = amenityService.getAmenityById(amenityId)
                .orElseThrow(() -> new NotFoundResponseException("Amenity not found with ID: " + amenityId));
        logger.debug("Found amenity {}", amenityId);
        return ResponseEntity.ok().body(mapper.convertToDTO(amenity));
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Update an Amenity by ID", description = "Update an existing amenity")
    @ApiResponse(responseCode = "200", description = "Amenity updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Amenity not found")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @JsonView(Views.AmenityView.class)
    @PutMapping("/buildings/{buildingId}/amenities/{amenityId}")
    public ResponseEntity<AmenityDto> updateAmenity(
            @Parameter(description = "ID of the amenity to be updated")
            @PathVariable String amenityId,
            @Valid @RequestBody @Parameter(description = "Amenity payload") AmenityDto amenityDto,
            @PathVariable String buildingId) {
        if (!amenityId.equals(amenityDto.getAmenityId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        logger.debug("Updating amenity {}", amenityId);
        Amenity amenity = mapper.convertToEntity(amenityDto);
        amenity = amenityService.updateAmenity(amenity, amenityDto.getBuildingIds());
        logger.debug("Amenity updated successfully: {}", amenity.getAmenityId());
        return ResponseEntity.ok().body(mapper.convertToDTO(amenity));
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Update Amenity Maintenance Status", description = "Update the maintenance status of an amenity")
    @ApiResponse(responseCode = "200", description = "Amenity maintenance status updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Amenity not found")
    @PutMapping("/buildings/{buildingId}/amenities/{amenityId}/maintenance-status")
    public ResponseEntity<AmenityDto> updateMaintenanceStatus(
            @Parameter(description = "ID of the amenity") @PathVariable String amenityId,
            @Valid @RequestBody @Parameter(description = "New maintenance status") MaintenanceStatus status, @PathVariable String buildingId) {
        logger.debug("Updating maintenance status for amenity {}", amenityId);
        Amenity updatedAmenity = amenityService.updateMaintenanceStatus(amenityId, status);
        logger.debug("Maintenance status updated for amenity {}", amenityId);
        return ResponseEntity.ok().body(mapper.convertToDTO(updatedAmenity));
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Add Building to Amenity", description = "Add a building association to an amenity")
    @ApiResponse(responseCode = "200", description = "Building added to amenity successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Amenity or Building not found")
    @PostMapping("/amenities/{amenityId}/add-building/{buildingId}")
    public ResponseEntity<AmenityDto> addBuildingToAmenity(
            @Parameter(description = "ID of the amenity") @PathVariable String amenityId,
            @Parameter(description = "ID of the building to add") @PathVariable String buildingId) {
        logger.debug("Adding building {} to amenity {}", buildingId, amenityId);
        Amenity updatedAmenity = amenityService.addBuildingToAmenity(amenityId, buildingId);
        logger.debug("Building {} added to amenity {}", buildingId, amenityId);
        return ResponseEntity.ok().body(mapper.convertToDTO(updatedAmenity));
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Remove Building from Amenity", description = "Remove a building association from an amenity")
    @ApiResponse(responseCode = "200", description = "Building removed from amenity successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Amenity or Building not found")
    @DeleteMapping("/amenities/{amenityId}/remove-building/{buildingId}")
    public ResponseEntity<AmenityDto> removeBuildingFromAmenity(
            @Parameter(description = "ID of the amenity") @PathVariable String amenityId,
            @Parameter(description = "ID of the building to remove") @PathVariable String buildingId) {
        logger.debug("Removing building {} from amenity {}", buildingId, amenityId);
        Amenity updatedAmenity = amenityService.removeBuildingFromAmenity(amenityId, buildingId);
        logger.debug("Building {} removed from amenity {}", buildingId, amenityId);
        return ResponseEntity.ok().body(mapper.convertToDTO(updatedAmenity));
    }

}
