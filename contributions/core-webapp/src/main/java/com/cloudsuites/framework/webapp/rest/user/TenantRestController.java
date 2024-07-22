package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.BuildingService;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.service.TenantService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.IdentityMapper;
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
@RequestMapping("/api/v1/")
@Tags(value = {@Tag(name = "Tenants", description = "Operations related to tenants")})
public class TenantRestController {

    private static final Logger logger = LoggerFactory.getLogger(TenantRestController.class);

    private final TenantService tenantService;
    private final TenantMapper tenantMapper;
    private final BuildingService buildingService;
    private final UnitService unitService;
    private final UserService userService;
    private final IdentityMapper identityMapper;

    @Autowired
    public TenantRestController(TenantService tenantService, TenantMapper tenantMapper,
                                BuildingService buildingService, UnitService unitService,
                                UserService userService, IdentityMapper identityMapper) {
        this.tenantService = tenantService;
        this.tenantMapper = tenantMapper;
        this.buildingService = buildingService;
        this.unitService = unitService;
        this.userService = userService;
        this.identityMapper = identityMapper;
    }

    @Operation(summary = "Create Tenant", description = "Create a new tenant")
    @ApiResponse(responseCode = "201", description = "Tenant created successfully", content = @Content(mediaType = "application/json"))
    @PostMapping("/buildings/{buildingId}/units/{unitId}/tenants")
    @JsonView(Views.TenantView.class)
    public ResponseEntity<TenantDto> createTenant(
            @PathVariable String buildingId,
            @PathVariable String unitId,
            @RequestBody TenantDto tenantDto) throws NotFoundResponseException {

        if (tenantDto.getIdentity() == null) {
            throw new NotFoundResponseException("Identity details are required");
        }
        Building building = buildingService.getBuildingById(buildingId);
        Unit unit = unitService.getUnitById(buildingId, unitId);
        Identity identity = userService.createUser(identityMapper.convertToEntity(tenantDto.getIdentity()));
        Tenant tenant = tenantMapper.convertToEntity(tenantDto);
        tenant.setIdentity(identity);
        tenant.setBuilding(building);
        tenant.setUnit(unit);
        logger.info("Creating new tenant in building ID: {} and unit ID: {}", buildingId, unitId);
        Tenant newTenant = tenantService.createTenant(tenant, unitId);
        TenantDto newTenantDto = tenantMapper.convertToDTO(newTenant);
        return ResponseEntity.status(201).body(newTenantDto);
    }

    @Operation(summary = "Get Tenant by ID", description = "Retrieve a tenant by its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @GetMapping("/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}")
    @JsonView(Views.TenantView.class)
    public ResponseEntity<TenantDto> getTenant(
            @PathVariable String buildingId,
            @PathVariable String unitId,
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
    @JsonView(Views.TenantView.class)
    public ResponseEntity<TenantDto> updateTenant(
            @PathVariable String buildingId,
            @PathVariable String unitId,
            @PathVariable Long tenantId,
            @RequestBody TenantDto tenantDto) throws NotFoundResponseException {
        logger.info("Updating tenant with ID: {}", tenantId);

        Building building = buildingService.getBuildingById(buildingId);
        Unit unit = unitService.getUnitById(buildingId, unitId);

        if (building == null) {
            throw new NotFoundResponseException("Building not found for ID: " + buildingId);
        }
        if (unit == null) {
            throw new NotFoundResponseException("Unit not found for ID: " + unitId);
        }
        tenantDto.setBuildingId(buildingId);

        Tenant updatedTenant = tenantService.updateTenant(tenantId, tenantMapper.convertToEntity(tenantDto));
        TenantDto updatedTenantDto = tenantMapper.convertToDTO(updatedTenant);
        return ResponseEntity.ok(updatedTenantDto);
    }

    @Operation(summary = "List All Tenants for a given unit", description = "Retrieve a list of all tenants for a given unit")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @GetMapping("/buildings/{buildingId}/units/{unitId}/tenants")
    @JsonView(Views.TenantView.class)
    public ResponseEntity<List<TenantDto>> listTenants(
            @PathVariable String buildingId,
            @PathVariable String unitId) throws NotFoundResponseException {
        logger.info("Listing all tenants for building ID: {} and unit ID: {}", buildingId, unitId);
        List<TenantDto> tenants = tenantService.getAllTenantsByBuildingAndUnit(buildingId, unitId).stream()
                .map(tenantMapper::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tenants);
    }

    @Operation(summary = "List All Tenants for a given building", description = "Retrieve a list of all tenants for a given building")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @GetMapping("/buildings/{buildingId}/tenants")
    @JsonView(Views.TenantView.class)
    public ResponseEntity<List<TenantDto>> listTenantsByBuilding(
            @PathVariable String buildingId) throws NotFoundResponseException {
        logger.info("Listing all tenants for building ID: {}", buildingId);
        List<TenantDto> tenants = tenantService.getAllTenantsByBuilding(buildingId).stream()
                .map(tenantMapper::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tenants);
    }

    @Operation(summary = "List All Tenants", description = "Retrieve a list of all tenants")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @GetMapping("/tenants")
    @JsonView(Views.TenantView.class)
    public ResponseEntity<List<TenantDto>> listTenants() {
        logger.info("Listing all tenants");
        List<TenantDto> tenants = tenantService.getAllTenants().stream()
                .map(tenantMapper::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tenants);
    }

}
