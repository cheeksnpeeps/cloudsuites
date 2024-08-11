package com.cloudsuites.framework.webapp.rest.user.role;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.user.AdminRoleService;
import com.cloudsuites.framework.services.user.entities.Admin;
import com.cloudsuites.framework.services.user.entities.AdminRole;
import com.cloudsuites.framework.services.user.entities.AdminStatus;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.user.dto.AdminDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.AdminMapper;
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
@Tags(value = {@Tag(name = "Admin Roles Management", description = "Operations related to admin roles")})
public class AdminRoleRestController {

    private static final Logger logger = LoggerFactory.getLogger(AdminRoleRestController.class);
    private final AdminRoleService adminRoleService;
    private final AdminMapper mapper;

    @Autowired
    public AdminRoleRestController(AdminRoleService adminRoleService, AdminMapper mapper) {
        this.adminRoleService = adminRoleService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Get Admin by ID", description = "Retrieve admin details based on their ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Admin not found")
    @JsonView(Views.RoleView.class)  // Use the specific view for admin role
    @GetMapping("/admins/{adminId}/roles")
    public ResponseEntity<AdminDto> getAdminRoleById(
            @Parameter(description = "ID of the admin to be retrieved") @PathVariable String adminId) throws NotFoundResponseException {
        logger.debug("Getting admin {}", adminId);
        Admin admin = adminRoleService.getAdminRole(adminId);
        logger.debug("Found admin {}", adminId);
        return ResponseEntity.ok().body(mapper.convertToDTO(admin));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Update Admin Role", description = "Update the role of an existing admin")
    @ApiResponse(responseCode = "200", description = "Admin role updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Admin not found")
    @JsonView(Views.RoleView.class)  // Use the specific view for admin role
    @PutMapping("/admins/{adminId}/roles")
    public ResponseEntity<AdminDto> updateAdminRole(
            @Parameter(description = "ID of the admin to be updated") @PathVariable String adminId,
            @Valid @RequestBody @Parameter(description = "Admin role payload") AdminDto adminDto) throws NotFoundResponseException {
        logger.debug("Updating admin role for {}", adminId);
        Admin updatedAdmin = adminRoleService.updateAdminRole(adminId, adminDto.getRole());
        logger.debug("Admin role updated successfully: {} {}", updatedAdmin.getAdminId(), updatedAdmin.getRole());
        return ResponseEntity.ok().body(mapper.convertToDTO(updatedAdmin));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Delete Admin Role", description = "Delete the role of an admin by ID")
    @ApiResponse(responseCode = "204", description = "Admin role deleted successfully")
    @ApiResponse(responseCode = "404", description = "Admin not found")
    @DeleteMapping("/admins/{adminId}/roles")
    public ResponseEntity<Void> deleteAdminRole(
            @Parameter(description = "ID of the admin whose role is to be deleted") @PathVariable String adminId) throws NotFoundResponseException {
        logger.debug("Deleting admin role for {}", adminId);
        adminRoleService.deleteAdminRole(adminId);
        logger.debug("Admin role deleted successfully: {}", adminId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Get All Admins Roles", description = "Retrieve all admins roles")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @JsonView(Views.RoleView.class)  // Use the specific view for admin role
    @GetMapping("/admins/roles")
    public ResponseEntity<List<AdminDto>> getAdminsRoles() {
        logger.debug("Getting all admins roles");
        List<Admin> admins = adminRoleService.getAdminsByRole();
        logger.debug("Found {} admins roles", admins.size());
        return ResponseEntity.ok().body(mapper.convertToDTOList(admins));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Get Admins by Role", description = "Retrieve all admins with a specific role")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @JsonView(Views.RoleView.class)  // Use the specific view for admin role
    @GetMapping("/admins/roles/{adminRole}")
    public ResponseEntity<List<AdminDto>> getAdminsByRole(
            @Parameter(description = "Admin role to filter by") @PathVariable AdminRole adminRole) {
        logger.debug("Getting admins with role {}", adminRole);
        List<Admin> admins = adminRoleService.getAdminsByRole(adminRole);
        logger.debug("Found {} admins with role {}", admins.size(), adminRole);
        return ResponseEntity.ok().body(mapper.convertToDTOList(admins));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Get Admins by Role and Status", description = "Retrieve all admins with a specific role and status")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @JsonView(Views.RoleView.class)  // Use the specific view for admin role
    @GetMapping("/admins/roles/{adminRole}/status/{status}")
    public ResponseEntity<List<AdminDto>> getAdminsByRoleAndStatus(
            @Parameter(description = "Admin role to filter by") @PathVariable AdminRole adminRole,
            @Parameter(description = "Admin status to filter by") @PathVariable AdminStatus status) {
        logger.debug("Getting admins with role {} and status {}", adminRole, status);
        List<Admin> admins = adminRoleService.getAdminsByRoleAndStatus(adminRole, status);
        logger.debug("Found {} admins with role {} and status {}", admins.size(), adminRole, status);
        return ResponseEntity.ok().body(mapper.convertToDTOList(admins));
    }
}

