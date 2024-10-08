package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.service.AmenityService;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Company;
import com.cloudsuites.framework.services.property.features.service.BuildingService;
import com.cloudsuites.framework.services.property.features.service.CompanyService;
import com.cloudsuites.framework.webapp.rest.amenity.mapper.AmenityMapper;
import com.cloudsuites.framework.webapp.rest.property.dto.BuildingDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.property.mapper.BuildingMapper;
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
@RequestMapping("/api/v1/companies/{companyId}/buildings")
@Tags(value = {@Tag(name = "Buildings", description = "Operations related to buildings")})
public class BuildingRestController {

    private static final Logger logger = LoggerFactory.getLogger(BuildingRestController.class);
    private final BuildingService buildingService;
    private final BuildingMapper mapper;
    private final CompanyService companyService;
    private final AmenityService amenityService;
    private final AmenityMapper amenityMapper;
    @Autowired
    public BuildingRestController(BuildingService buildingService, BuildingMapper mapper, CompanyService companyService, AmenityService amenityService, AmenityMapper amenityMapper) {
        this.buildingService = buildingService;
        this.mapper = mapper;
        this.companyService = companyService;
        this.amenityService = amenityService;
        this.amenityMapper = amenityMapper;
    }

    @PreAuthorize("hasAuthority('ALL_ADMIN')")
    @Operation(summary = "Get Buildings", description = "Get a list of buildings based on optional management company ID - Use -1 to get all buildings")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Not Found")
    @JsonView(Views.BuildingView.class)
    @GetMapping("")
    public ResponseEntity<List<BuildingDto>> getBuildings(@PathVariable String companyId) throws NotFoundResponseException {
        logger.debug("Getting all buildings for management company: {}", companyId);
        // If companyId is provided, filter buildings by management company
        if (companyId == null) {
            List<Building> buildings = buildingService.getAllBuildings();
            logger.debug("Found {} buildings", buildings.size());
            return ResponseEntity.ok().body(mapper.convertToDTOList(buildings));
        }
        List<Building> buildings = buildingService.getBuildingByCompanyId(companyId);
        logger.debug("Found {} buildings for management company: {}", buildings.size(), companyId);
        return ResponseEntity.ok().body(mapper.convertToDTOList(buildings));
    }

    @PreAuthorize("hasAuthority('ALL_ADMIN')")
    @Operation(summary = "Create a Building", description = "Create a new building")
    @ApiResponse(responseCode = "201", description = "Building created successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @JsonView(Views.BuildingView.class)
    @PostMapping("")
    public ResponseEntity<BuildingDto> saveBuilding(@Valid @RequestBody @Parameter(description = "Building payload") BuildingDto buildingDTO, @PathVariable String companyId) throws NotFoundResponseException {
        Building building = mapper.convertToEntity(buildingDTO);
        Company company = companyService.getCompanyById(companyId);
        building.setCompany(company);
        logger.debug("Saving building {}", building.getName());
        building = buildingService.saveBuilding(building);
        logger.debug("Building saved successfully: {} {}", building.getBuildingId(), building.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(building));
    }

    @PreAuthorize("hasAuthority('ALL_ADMIN')")
    @Operation(summary = "Delete a Building by ID", description = "Delete a building based on its ID")
    @ApiResponse(responseCode = "204", description = "Building deleted successfully")
    @ApiResponse(responseCode = "404", description = "Building not found")
    @DeleteMapping("/{buildingId}")
    public ResponseEntity<Void> deleteBuildingById(
            @Parameter(description = "ID of the building to be deleted") @PathVariable String buildingId, @PathVariable String companyId) throws NotFoundResponseException {
        logger.debug("Deleting building {} for company {}", buildingId, companyId);
        buildingService.deleteBuildingById(companyId, buildingId);
        logger.debug("Building deleted successfully: {}", buildingId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ALL_STAFF')")
    @Operation(summary = "Get a Building by ID", description = "Retrieve building details based on its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Building not found")
    @JsonView(Views.BuildingView.class)
    @GetMapping("/{buildingId}")
    public ResponseEntity<BuildingDto>  getBuildingById(
            @RequestParam(value = "includeAmenities", required = false, defaultValue = "false") boolean includeAmenities, // Optional parameter
            @Parameter(description = "ID of the building to be retrieved") @PathVariable String buildingId, @PathVariable String companyId)
            throws NotFoundResponseException {
        logger.debug("Getting building {}", buildingId);
        Building building = buildingService.getBuildingById(buildingId);
        logger.debug("Found building {}", buildingId);
        BuildingDto buildingDto = mapper.convertToDTO(building);
        if (!includeAmenities) {
            return ResponseEntity.ok().body(buildingDto);
        }
        logger.debug("Getting amenities for building {}", buildingId);
        List<Amenity> amenities = amenityService.getAmenitiesByBuildingId(buildingId);
        buildingDto.setAmenities(amenityMapper.convertToDTOList(amenities));
        return ResponseEntity.ok().body(buildingDto);
    }

}