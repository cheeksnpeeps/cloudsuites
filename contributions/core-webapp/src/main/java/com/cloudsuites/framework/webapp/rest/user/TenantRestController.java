package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UsernameAlreadyExistsException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.BuildingService;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.services.property.personas.service.TenantService;
import com.cloudsuites.framework.webapp.authentication.util.WebAppConstants;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.TenantMapper;
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

    @Operation(summary = "Create Tenant", description = "Create tenant with details")
    @ApiResponse(responseCode = "201", description = "Tenant created successfully", content = @Content(mediaType = "application/json"))
    @PostMapping("/buildings/{buildingId}/units/{unitId}/tenants")
    @JsonView(Views.TenantView.class)
    public ResponseEntity<TenantDto> createTenant(@Valid @RequestBody @Parameter(description = "Tenant details") TenantDto tenantDto,
                                                  @PathVariable String buildingId,
                                                  @PathVariable String unitId) throws InvalidOperationException, UsernameAlreadyExistsException, NotFoundResponseException {
        Unit unit = validateBuildingAndUnit(buildingId, unitId);
        logger.debug(WebAppConstants.Tenant.LOG_REGISTERING_TENANT, tenantDto.getIdentity().getUsername());
        Tenant tenant = tenantMapper.convertToEntity(tenantDto);
        tenant = tenantService.createTenant(tenant, unit);
        logger.info(WebAppConstants.Tenant.LOG_TENANT_REGISTERED_SUCCESS, tenant.getTenantId(), tenantDto.getIdentity().getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantMapper.convertToDTO(tenant));
    }

    @Operation(summary = "List All Tenants by Building ID", description = "Retrieve a list of all tenants for a given Building")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @GetMapping("/buildings/{buildingId}/tenants")
    @JsonView(Views.TenantView.class)
    public ResponseEntity<List<TenantDto>> listTenantsByBuildingId(
            @PathVariable String buildingId,
            @RequestParam(value = "status", required = false, defaultValue = "ACTIVE") TenantStatus status) throws NotFoundResponseException {
        logger.info(WebAppConstants.Tenant.LOG_FETCHING_TENANTS_BY_BUILDING, buildingId);
        if (buildingService.getBuildingById(buildingId) == null) {
            throw new NotFoundResponseException(WebAppConstants.Building.LOG_BUILDING_NOT_FOUND + buildingId);
        }
        List<TenantDto> tenants = tenantService.getAllTenantsByBuilding(buildingId, status).stream()
                .map(tenantMapper::convertToDTO)
                .collect(Collectors.toList());
        logger.debug(WebAppConstants.Tenant.LOG_FOUND_TENANTS_BY_BUILDING, tenants.size());
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
        logger.info(WebAppConstants.Tenant.LOG_FETCHING_TENANTS_BY_UNIT, unitId);
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
        logger.info(WebAppConstants.Tenant.LOG_FETCHING_TENANTS_BY_UNIT, tenantId);
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
        logger.info(WebAppConstants.Tenant.LOG_UPDATING_TENANT, tenantId);
        Tenant tenant = tenantMapper.convertToEntity(tenantDto);
        Building building = buildingService.getBuildingById(buildingId);
        Unit unit = unitService.getUnitById(buildingId, unitId);
        if (building == null) {
            throw new NotFoundResponseException(WebAppConstants.Building.LOG_BUILDING_NOT_FOUND + buildingId);
        }
        if (unit == null
                || !unit.getBuilding().getBuildingId().equals(buildingId)
                || !unit.getUnitId().equals(unitId)) {
            throw new NotFoundResponseException(WebAppConstants.Unit.LOG_UNIT_NOT_FOUND + unitId);
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
        validateBuildingAndUnit(buildingId, unitId);
        Tenant tenant = tenantService.getTenantById(tenantId);
        tenantService.inactivateTenant(tenant);
        return ResponseEntity.ok().body("Tenant successfully deleted");
    }


    @Operation(summary = "Update Tenant", description = "Transfer Tenant to a Different Unit")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @PutMapping("/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}/transfer/{newUnitId}")
    public ResponseEntity<String> transferTenant(@PathVariable String buildingId,
                                                 @PathVariable String tenantId,
                                                 @PathVariable String unitId,
                                                 @PathVariable String newUnitId) throws NotFoundResponseException, InvalidOperationException {
        Unit newUnit = validateBuildingAndUnit(buildingId, newUnitId);
        Tenant tenant = tenantService.getTenantById(tenantId);
        if (tenant == null || !unitId.equals(tenant.getUnit().getUnitId()))
            throw new NotFoundResponseException("Tenant not found for ID: " + tenantId + " in unit " + unitId);

        Unit oldUnit = unitService.getUnitById(buildingId, unitId);

        tenantService.transferTenant(tenant, newUnit, oldUnit);

        return ResponseEntity.ok().body("Tenant transferred to new unit");
    }


    @DeleteMapping("/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}/inactivate")
    public ResponseEntity<String> inactivateTenant(@PathVariable String buildingId,
                                                   @PathVariable String tenantId,
                                                   @PathVariable String unitId) throws NotFoundResponseException {
        Building building = buildingService.getBuildingById(buildingId);
        if (building == null) throw new NotFoundResponseException("Building not found for ID: " + buildingId);

        Tenant tenant = tenantService.getTenantById(tenantId);
        if (tenant == null || !unitId.equals(tenant.getUnit().getUnitId()))
            throw new NotFoundResponseException("Tenant not found for ID: " + tenantId + " in unit " + unitId);

        tenantService.inactivateTenant(tenant);

        return ResponseEntity.ok().body("Tenant inactivated");
    }

    private Unit validateBuildingAndUnit(String buildingId, String unitId) throws NotFoundResponseException {
        Building building = buildingService.getBuildingById(buildingId);
        if (building == null) {
            throw new NotFoundResponseException(WebAppConstants.Building.LOG_BUILDING_NOT_FOUND + buildingId);
        }
        Unit unit = unitService.getUnitById(buildingId, unitId);
        if (unit == null) {
            throw new NotFoundResponseException(WebAppConstants.Unit.LOG_UNIT_NOT_FOUND + unitId);
        }
        unit.setBuilding(building);
        return unit;
    }

}
