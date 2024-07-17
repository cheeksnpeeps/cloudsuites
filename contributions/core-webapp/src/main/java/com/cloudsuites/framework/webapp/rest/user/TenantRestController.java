package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.BuildingService;
import com.cloudsuites.framework.services.property.TenantService;
import com.cloudsuites.framework.services.property.UnitService;
import com.cloudsuites.framework.services.property.entities.Building;
import com.cloudsuites.framework.services.property.entities.Tenant;
import com.cloudsuites.framework.services.property.entities.Unit;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.rest.property.mapper.BuildingMapper;
import com.cloudsuites.framework.webapp.rest.property.mapper.UnitMapper;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.IdentityMapper;
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

@RestController
@RequestMapping("/api/v1/")
@Tags(value = {@Tag(name = "Tenants", description = "Operations related to tenants")})
public class TenantRestController {

    private static final Logger logger = LoggerFactory.getLogger(TenantRestController.class);

    private final TenantService tenantService;
    private final TenantMapper tenantMapper;
    private final BuildingMapper buildingMapper;
    private final UnitMapper unitMapper;
    private final BuildingService buildingService;
    private final UnitService unitService;
    private final UserService userService;
    private final IdentityMapper identityMapper;

    @Autowired
    public TenantRestController(TenantService tenantService, TenantMapper tenantMapper,
                                BuildingMapper buildingMapper, UnitMapper unitMapper,
                                BuildingService buildingService, UnitService unitService,
                                UserService userService, IdentityMapper identityMapper) {
        this.tenantService = tenantService;
        this.tenantMapper = tenantMapper;
        this.buildingMapper = buildingMapper;
        this.unitMapper = unitMapper;
        this.buildingService = buildingService;
        this.unitService = unitService;
        this.userService = userService;
        this.identityMapper = identityMapper;
    }

    @Operation(summary = "Create Tenant", description = "Create a new tenant")
    @ApiResponse(responseCode = "201", description = "Tenant created successfully", content = @Content(mediaType = "application/json"))
    @PostMapping("/buildings/{buildingId}/units/{unitId}/tenants")
    public ResponseEntity<TenantDto> createTenant(
            @PathVariable Long buildingId,
            @PathVariable Long unitId,
            @RequestBody TenantDto tenantDto) throws NotFoundResponseException {

        validateIdentity(tenantDto.getIdentity());
        Unit unit = validateBuildingAndUnit(buildingId, unitId);

        Identity identity = userService.createUser(identityMapper.convertToEntity(tenantDto.getIdentity()));
        Tenant tenant = createTenantEntity(tenantDto, buildingId, unitId, identity, unit);

        logger.info("Creating new tenant in building ID: {} and unit ID: {}", buildingId, unitId);
        Tenant newTenant = tenantService.createTenant(tenant);
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

        validateBuildingAndUnit(buildingId, unitId);
        tenantDto.setBuildingId(buildingId);

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
                .collect(Collectors.toList());
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
                .collect(Collectors.toList());
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

    private void validateIdentity(IdentityDto identityDto) throws NotFoundResponseException {
        if (identityDto == null) {
            throw new NotFoundResponseException("Identity details are required");
        }
    }

    private Unit validateBuildingAndUnit(@PathVariable Long buildingId, @PathVariable Long unitId) throws NotFoundResponseException {
        Building building = buildingService.getBuildingById(buildingId);
        Unit unit = unitService.getUnitById(buildingId, unitId);

        if (building == null) {
            throw new NotFoundResponseException("Building not found for ID: " + buildingId);
        }
        if (unit == null) {
            throw new NotFoundResponseException("Unit not found for ID: " + unitId);
        }
        return unit;
    }

    private Tenant createTenantEntity(TenantDto tenantDto, Long buildingId, Long unitId, Identity identity, Unit unit) {
        Tenant tenant = tenantMapper.convertToEntity(tenantDto);
        tenant.setIdentity(identity);
        tenant.setBuildingId(buildingId);
        tenant.setUnit(unit);
        return tenant;
    }
}
