package com.cloudsuites.framework.webapp.rest.user.role;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantRole;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.services.property.personas.service.role.TenantRoleService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tags(value = {@Tag(name = "Tenant Roles Management", description = "Operations related to tenant roles")})
public class TenantRoleRestController {

    private static final Logger logger = LoggerFactory.getLogger(TenantRoleRestController.class);
    private final TenantRoleService tenantRoleService;
    private final TenantMapper mapper;

    @Autowired
    public TenantRoleRestController(TenantRoleService tenantRoleService, TenantMapper mapper) {
        this.tenantRoleService = tenantRoleService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAuthority('BUILDING_SECURITY')")
    @Operation(summary = "Get Tenant by ID", description = "Retrieve tenant details based on their ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @JsonView(Views.RoleView.class)
    @GetMapping("/tenants/{tenantId}/roles")
    public ResponseEntity<TenantDto> getTenantRoleById(
            @Parameter(description = "ID of the tenant to be retrieved") @PathVariable String tenantId) throws NotFoundResponseException {
        logger.debug("Getting tenant {}", tenantId);
        Tenant tenant = tenantRoleService.getTenantRole(tenantId);
        logger.debug("Found tenant {}", tenantId);
        return ResponseEntity.ok().body(mapper.convertToDTO(tenant));
    }

    @PreAuthorize("hasAuthority('BUILDING_SECURITY')")
    @Operation(summary = "Update Tenant Role", description = "Update the role of an existing tenant")
    @ApiResponse(responseCode = "200", description = "Tenant role updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @JsonView(Views.RoleView.class)
    @PutMapping("/tenants/{tenantId}/roles")
    public ResponseEntity<TenantDto> updateTenantRole(
            @Parameter(description = "ID of the tenant to be updated") @PathVariable String tenantId,
            @Valid @RequestBody @Parameter(description = "Tenant role payload") TenantDto tenantDto) throws NotFoundResponseException {
        logger.debug("Updating tenant role for {}", tenantId);
        Tenant updatedTenant = tenantRoleService.updateTenantRole(tenantId, tenantDto.getRole());
        logger.debug("Tenant role updated successfully: {} {}", updatedTenant.getTenantId(), updatedTenant.getRole());
        return ResponseEntity.ok().body(mapper.convertToDTO(updatedTenant));
    }

    @PreAuthorize("hasAuthority('BUILDING_SECURITY')")
    @Operation(summary = "Delete Tenant Role", description = "Delete the role of a tenant by ID")
    @ApiResponse(responseCode = "204", description = "Tenant role deleted successfully")
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @DeleteMapping("/tenants/{tenantId}/roles")
    public ResponseEntity<Void> deleteTenantRole(
            @Parameter(description = "ID of the tenant whose role is to be deleted") @PathVariable String tenantId) throws NotFoundResponseException {
        logger.debug("Deleting tenant role for {}", tenantId);
        tenantRoleService.deleteTenantRole(tenantId);
        logger.debug("Tenant role deleted successfully: {}", tenantId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('BUILDING_SECURITY')")
    @Operation(summary = "Get All Tenants Roles", description = "Retrieve all tenants roles")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @JsonView(Views.RoleView.class)
    @GetMapping("/tenants/roles")
    public ResponseEntity<List<TenantDto>> getTenantsRoles() {
        logger.debug("Getting all tenants roles");
        List<Tenant> tenants = tenantRoleService.getTenantsByRole();
        logger.debug("Found {} tenants roles", tenants.size());
        return ResponseEntity.ok().body(mapper.convertToDTOList(tenants));
    }

    @PreAuthorize("hasAuthority('BUILDING_SECURITY')")
    @Operation(summary = "Get Tenants by Role", description = "Retrieve all tenants with a specific role")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @JsonView(Views.RoleView.class)
    @GetMapping("/tenants/roles/{tenantRole}")
    public ResponseEntity<List<TenantDto>> getTenantsByRole(
            @Parameter(description = "Tenant role to filter by") @PathVariable TenantRole tenantRole) {
        logger.debug("Getting tenants with role {}", tenantRole);
        List<Tenant> tenants = tenantRoleService.getTenantsByRole(tenantRole);
        logger.debug("Found {} tenants with role {}", tenants.size(), tenantRole);
        return ResponseEntity.ok().body(mapper.convertToDTOList(tenants));
    }

    @PreAuthorize("hasAuthority('BUILDING_SECURITY')")
    @Operation(summary = "Get Tenants by Role and Status", description = "Retrieve all tenants with a specific role and status")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @JsonView(Views.RoleView.class)
    @GetMapping("/tenants/roles/{tenantRole}/status/{status}")
    public ResponseEntity<List<TenantDto>> getTenantsByRoleAndStatus(
            @Parameter(description = "Tenant role to filter by") @PathVariable TenantRole tenantRole,
            @Parameter(description = "Tenant status to filter by") @PathVariable TenantStatus status) {
        logger.debug("Getting tenants with role {} and status {}", tenantRole, status);
        List<Tenant> tenants = tenantRoleService.getTenantsByRoleAndStatus(tenantRole, status);
        logger.debug("Found {} tenants with role {} and status {}", tenants.size(), tenantRole, status);
        return ResponseEntity.ok().body(mapper.convertToDTOList(tenants));
    }
}
