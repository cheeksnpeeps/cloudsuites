package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.BuildingService;
import com.cloudsuites.framework.services.property.TenantService;
import com.cloudsuites.framework.services.property.UnitService;
import com.cloudsuites.framework.services.property.entities.Building;
import com.cloudsuites.framework.services.property.entities.Tenant;
import com.cloudsuites.framework.services.property.entities.Unit;
import com.cloudsuites.framework.webapp.rest.property.mapper.BuildingMapper;
import com.cloudsuites.framework.webapp.rest.property.mapper.UnitMapper;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.TenantMapper;
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/v1/")
@Tags(value = {@Tag(name = "Tenants", description = "Operations related to tenants")})
public class TenantRestController {

    private static final Logger logger = LoggerFactory.getLogger(TenantRestController.class);

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private BuildingMapper buildingMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private UnitService unitService;

    @Operation(summary = "Create Tenant", description = "Create a new tenant")
    @ApiResponse(responseCode = "201", description = "Tenant created successfully", content = @Content(mediaType = "application/json"))
    @PostMapping("/buildings/{buildingId}/units/{unitId}/tenants")
    public ResponseEntity<TenantDto> createTenant(
            @PathVariable Long buildingId,
            @PathVariable Long unitId,
            @RequestBody TenantDto tenantDto) throws NotFoundResponseException {
        logger.info("Creating new tenant in building ID: {} and unit ID: {}", buildingId, unitId);
        Building building = buildingService.getBuildingById(buildingId);
        Unit unit = unitService.getUnitById(buildingId,unitId);
        Tenant newTenant = tenantService.createTenant(tenantMapper.convertToEntity(tenantDto));
        newTenant.setBuilding(building);
        newTenant.setUnit(unit);
        TenantDto newTenantDto = tenantMapper.convertToDTO(newTenant);
        return ResponseEntity.status(201).body(newTenantDto);
    }
    @Operation(summary = "Get Tenant by ID", description = "Retrieve a tenant by its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @GetMapping("/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}")
    public ResponseEntity<TenantDto> getTenant(
            @PathVariable Long buildingId,
            @PathVariable Long unitId,
            @PathVariable Long tenantId) throws NotFoundResponseException {
        logger.info("Fetching tenant with ID: {}", tenantId);
        Tenant tenant = tenantService.getTenantByBuildingIdAndUnitIdAndTenantId(buildingId, unitId, tenantId);
        TenantDto tenantDto = tenantMapper.convertToDTO(tenant);
        return ResponseEntity.ok(tenantDto);
    }

    @Operation(summary = "Update Tenant", description = "Update an existing tenant by its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @PutMapping("/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}")
    public ResponseEntity<TenantDto> updateTenant(
            @PathVariable Long buildingId,
            @PathVariable Long unitId,
            @PathVariable Long tenantId,
            @RequestBody TenantDto tenantDto) throws NotFoundResponseException {
        logger.info("Updating tenant with ID: {}", tenantId);
        Building building = buildingService.getBuildingById(buildingId);
        Unit unit = unitService.getUnitById(buildingId,unitId);
        tenantDto.setBuilding(buildingMapper.convertToDTO(building));
        tenantDto.setUnit(unitMapper.convertToDTO(unit));
        Tenant updatedTenant = tenantService.updateTenant(tenantId, tenantMapper.convertToEntity(tenantDto));
        TenantDto updatedTenantDto = tenantMapper.convertToDTO(updatedTenant);
        return ResponseEntity.ok(updatedTenantDto);
    }

    @Operation(summary = "List All Tenants for a given unit", description = "Retrieve a list of all tenants for a given unit")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @GetMapping("/buildings/{buildingId}/units/{unitId}/tenants")
    public ResponseEntity<List<TenantDto>> listTenants(
            @PathVariable Long buildingId,
            @PathVariable Long unitId) throws NotFoundResponseException {
        logger.info("Listing all tenants for building ID: {} and unit ID: {}", buildingId, unitId);
        List<TenantDto> tenants = tenantService.getAllTenantsByBuildingAndUnit(buildingId, unitId).stream()
                .map(tenantMapper::convertToDTO)
                .collect(toList());
        return ResponseEntity.ok(tenants);
    }

    @Operation(summary = "List All Tenants for a given building", description = "Retrieve a list of all tenants for a given building")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @GetMapping("/buildings/{buildingId}/tenants")
    public ResponseEntity<List<TenantDto>> listTenantsByBuilding(
            @PathVariable Long buildingId) throws NotFoundResponseException {
        logger.info("Listing all tenants for building ID: {}", buildingId);
        List<TenantDto> tenants = tenantService.getAllTenantsByBuilding(buildingId).stream()
                .map(tenantMapper::convertToDTO)
                .collect(toList());
        return ResponseEntity.ok(tenants);
    }

    @Operation(summary = "List All Tenants", description = "Retrieve a list of all tenants")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @GetMapping("/tenants")
    public ResponseEntity<List<TenantDto>> listTenants() {
        logger.info("Listing all tenants");
        List<TenantDto> tenants = tenantService.getAllTenants().stream()
                .map(tenantMapper::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tenants);
    }
}
