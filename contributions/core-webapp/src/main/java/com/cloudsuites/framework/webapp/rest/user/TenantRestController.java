package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.BuildingService;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.services.property.personas.service.TenantService;
import com.cloudsuites.framework.webapp.authentication.util.TenantUpdateRequest;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.TenantMapper;
import com.fasterxml.jackson.annotation.JsonView;
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

@RestController
@RequestMapping("/api/v1")
@Tags(value = {@Tag(name = "Tenants", description = "Operations related to tenants")})
public class TenantRestController {

    private static final Logger logger = LoggerFactory.getLogger(TenantRestController.class);

    private final TenantService tenantService;
    private final TenantMapper tenantMapper;
    private final BuildingService buildingService;
    private final UnitService unitService;

    @Autowired
    public TenantRestController(TenantService tenantService, TenantMapper tenantMapper,
                                BuildingService buildingService, UnitService unitService) {
        this.tenantService = tenantService;
        this.tenantMapper = tenantMapper;
        this.buildingService = buildingService;
        this.unitService = unitService;
    }

    @Operation(summary = "List All Tenants by Building ID", description = "Retrieve a list of all tenants for a given Building")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @GetMapping("/buildings/{buildingId}/tenants")
    @JsonView(Views.TenantView.class)
    public ResponseEntity<List<TenantDto>> listTenantsByBuildingId(
            @PathVariable String buildingId,
            @RequestParam(value = "status", required = false, defaultValue = "ACTIVE") TenantStatus status) throws NotFoundResponseException {
        logger.info("Listing all tenants for building ID: {}", buildingId);
        if (buildingService.getBuildingById(buildingId) == null) {
            throw new NotFoundResponseException("Building not found for ID: " + buildingId);
        }
        List<TenantDto> tenants = tenantService.getAllTenantsByBuilding(buildingId, status).stream()
                .map(tenantMapper::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tenants);
    }

    @Operation(summary = "List All Tenants for a given unit", description = "Retrieve a list of all tenants for a given unit")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @GetMapping("/buildings/{buildingId}/units/{unitId}/tenants")
    @JsonView(Views.TenantView.class)
    public ResponseEntity<List<TenantDto>> listTenantsByUnit(
            @PathVariable String buildingId,
            @PathVariable String unitId,
            @RequestParam(value = "status", required = false, defaultValue = "ACTIVE") TenantStatus status) throws NotFoundResponseException {
        logger.info("Listing all tenants for building ID: {} and unit ID: {}", buildingId, unitId);
        List<TenantDto> tenants = tenantService.getAllTenantsByBuildingAndUnit(buildingId, unitId, status).stream()
                .map(tenantMapper::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tenants);
    }

    @Operation(summary = "Get Tenant by ID", description = "Retrieve a tenant by its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @GetMapping("/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}")
    @JsonView(Views.TenantView.class)
    public ResponseEntity<TenantDto> getTenantById(
            @PathVariable String buildingId,
            @PathVariable String unitId,
            @PathVariable String tenantId) throws NotFoundResponseException {
        logger.info("Fetching tenant with ID: {}", tenantId);
        Tenant tenant = tenantService.getTenantByBuildingIdAndUnitIdAndTenantId(buildingId, unitId, tenantId);
        TenantDto tenantDto = tenantMapper.convertToDTO(tenant);
        return ResponseEntity.ok(tenantDto);
    }

    @Operation(summary = "Update Tenant", description = "Update an existing tenant by its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @PutMapping("/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}")
    @JsonView(Views.TenantView.class)
    public ResponseEntity<TenantDto> updateTenant(
            @PathVariable String buildingId,
            @PathVariable String unitId,
            @PathVariable String tenantId,
            @RequestBody TenantDto tenantDto) throws NotFoundResponseException {
        logger.info("Updating tenant with ID: {}", tenantId);
        Tenant tenant = tenantMapper.convertToEntity(tenantDto);
        Building building = buildingService.getBuildingById(buildingId);
        Unit unit = unitService.getUnitById(buildingId, unitId);
        if (building == null) {
            throw new NotFoundResponseException("Building not found for ID: " + buildingId);
        }
        if (unit == null
                || !unit.getBuilding().getBuildingId().equals(buildingId)
                || !unit.getUnitId().equals(unitId)) {
            throw new NotFoundResponseException("Unit not found for ID: " + unitId);
        }
        tenant.setBuilding(building);
        tenant.setUnit(unit);
        Tenant updatedTenant = tenantService.updateTenant(tenantId, tenant);
        TenantDto updatedTenantDto = tenantMapper.convertToDTO(updatedTenant);
        return ResponseEntity.ok(updatedTenantDto);
    }

    @Operation(summary = "Delete Tenant", description = "Delete a tenant by its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @DeleteMapping("/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}")
    public ResponseEntity<String> deleteTenant(
            @PathVariable String buildingId,
            @PathVariable String unitId,
            @PathVariable String tenantId) throws NotFoundResponseException {
        Building building = buildingService.getBuildingById(buildingId);
        // Check if the unit exists
        Unit unit = unitService.getUnitById(buildingId, unitId);

        if (building == null) {
            throw new NotFoundResponseException("Building not found for ID: " + buildingId);
        }
        if (unit == null
                || !unit.getBuilding().getBuildingId().equals(buildingId)
                || !unit.getUnitId().equals(unitId)) {
            throw new NotFoundResponseException("Unit not found for ID: " + unitId);
        }
        Tenant tenant = tenantService.getTenantById(tenantId);
        // Remove tenant from unit
        unit.getTenants().stream()
                .filter(t -> t.getTenantId().equals(tenantId))
                .forEach(t -> unit.getTenants().remove(t));
        unitService.saveUnit(buildingId, unit.getFloor().getFloorId(), unit); // Save the updated unit
        // Delete tenant
        tenantService.deleteByTenantId(tenantId);
        return ResponseEntity.ok().body("Tenant successfully deleted");
    }


    @Operation(summary = "Update Tenant", description = "Transfer Tenant to a Different Unit")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @PutMapping("/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}/transfer-to-unit/{newUnitId}")
    public ResponseEntity<String> transferTenant(@PathVariable String buildingId,
                                                 @PathVariable String tenantId,
                                                 @PathVariable String unitId,
                                                 @PathVariable String newUnitId) throws NotFoundResponseException, InvalidOperationException {
        Building building = buildingService.getBuildingById(buildingId);
        if (building == null) {
            throw new NotFoundResponseException("Building not found for ID: " + buildingId);
        }
        Unit newUnit = unitService.getUnitById(buildingId, newUnitId);
        if (newUnit == null) {
            throw new NotFoundResponseException("Unit not found for ID: " + newUnitId);
        }
        Tenant tenant = tenantService.getTenantById(tenantId);
        if (tenant == null || !unitId.equals(tenant.getUnit().getUnitId())) {
            throw new NotFoundResponseException("Tenant not found for ID: " + tenantId + " in unit " + unitId);
        }
        Unit oldUnit = unitService.getUnitById(buildingId, unitId);
        tenantService.transferTenant(tenant, newUnit, oldUnit);

        return ResponseEntity.ok().body("Tenant transferred to new unit");
    }

    // Tenant Resigns or Leaves the Building
    @DeleteMapping("/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}/resigns")
    public ResponseEntity<?> tenantResigns(@PathVariable String tenantId) {
        // Implementation here
        return ResponseEntity.ok().body("Tenant Resigns or Leaves the Building");
    }

    // Tenant Changes Contact Information or Preferences
    @PutMapping("/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}/update-info")
    public ResponseEntity<?> updateTenantInfo(@PathVariable String tenantId, @RequestBody TenantUpdateRequest request) {
        // Implementation here
        return ResponseEntity.ok().body("Tenant Changes Contact Information or Preferences");
    }

    // Tenant is Marked as Inactive or Suspended
    @PutMapping("/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}/inactive")
    public ResponseEntity<?> markTenantInactive(@PathVariable String tenantId) {
        // Implementation here
        return ResponseEntity.ok().body("Tenant is Marked as Inactive or Suspended");
    }

}
