package com.cloudsuites.framework.webapp.rest.user.role;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Staff;
import com.cloudsuites.framework.services.property.personas.entities.StaffRole;
import com.cloudsuites.framework.services.property.personas.entities.StaffStatus;
import com.cloudsuites.framework.services.property.personas.service.role.StaffRoleService;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.user.dto.StaffDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.StaffMapper;
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
@Tags(value = {@Tag(name = "Staff Roles Management", description = "Operations related to staff roles")})
public class StaffRoleRestController {

    private static final Logger logger = LoggerFactory.getLogger(StaffRoleRestController.class);
    private final StaffRoleService staffRoleService;
    private final StaffMapper mapper;

    @Autowired
    public StaffRoleRestController(StaffRoleService staffRoleService, StaffMapper mapper) {
        this.staffRoleService = staffRoleService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAuthority('ALL_ADMIN') or hasAuthority('BUILDING_SUPERVISOR')")
    @Operation(summary = "Get Staff by ID", description = "Retrieve staff details based on their ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Staff not found")
    @JsonView(Views.RoleView.class)
    @GetMapping("/staff/{staffId}/roles")
    public ResponseEntity<StaffDto> getStaffRoleById(
            @Parameter(description = "ID of the staff to be retrieved") @PathVariable String staffId) throws NotFoundResponseException {
        logger.debug("Getting staff {}", staffId);
        Staff staff = staffRoleService.getStaffRole(staffId);
        logger.debug("Found staff {}", staffId);
        return ResponseEntity.ok().body(mapper.convertToDTO(staff));
    }

    @PreAuthorize("hasAuthority('ALL_ADMIN') or hasAuthority('BUILDING_SUPERVISOR')")
    @Operation(summary = "Update Staff Role", description = "Update the role of an existing staff member")
    @ApiResponse(responseCode = "200", description = "Staff role updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Staff not found")
    @JsonView(Views.RoleView.class)
    @PutMapping("/staff/{staffId}/roles")
    public ResponseEntity<StaffDto> updateStaffRole(
            @Parameter(description = "ID of the staff to be updated") @PathVariable String staffId,
            @Valid @RequestBody @Parameter(description = "Staff role payload") StaffDto staffDto) throws NotFoundResponseException {
        logger.debug("Updating staff role for {}", staffId);
        Staff updatedStaff = staffRoleService.updateStaffRole(staffId, staffDto.getRole());
        logger.debug("Staff role updated successfully: {} {}", updatedStaff.getStaffId(), updatedStaff.getRole());
        return ResponseEntity.ok().body(mapper.convertToDTO(updatedStaff));
    }

    @PreAuthorize("hasAuthority('ALL_ADMIN') or hasAuthority('BUILDING_SUPERVISOR')")
    @Operation(summary = "Delete Staff Role", description = "Delete the role of a staff member by ID")
    @ApiResponse(responseCode = "204", description = "Staff role deleted successfully")
    @ApiResponse(responseCode = "404", description = "Staff not found")
    @DeleteMapping("/staff/{staffId}/roles")
    public ResponseEntity<Void> deleteStaffRole(
            @Parameter(description = "ID of the staff whose role is to be deleted") @PathVariable String staffId) throws NotFoundResponseException {
        logger.debug("Deleting staff role for {}", staffId);
        staffRoleService.deleteStaffRole(staffId);
        logger.debug("Staff role deleted successfully: {}", staffId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ALL_ADMIN') or hasAuthority('BUILDING_SUPERVISOR')")
    @Operation(summary = "Get All Staff Roles", description = "Retrieve all staff roles")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @JsonView(Views.RoleView.class)
    @GetMapping("/staff/roles")
    public ResponseEntity<List<StaffDto>> getStaffRoles() {
        logger.debug("Getting all staff roles");
        List<Staff> staffMembers = staffRoleService.getStaffByRole();
        logger.debug("Found {} staff roles", staffMembers.size());
        return ResponseEntity.ok().body(mapper.convertToDTOList(staffMembers));
    }

    @PreAuthorize("hasAuthority('ALL_ADMIN') or hasAuthority('BUILDING_SUPERVISOR')")
    @Operation(summary = "Get Staff by Role", description = "Retrieve all staff members with a specific role")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @JsonView(Views.RoleView.class)
    @GetMapping("/staff/roles/{staffRole}")
    public ResponseEntity<List<StaffDto>> getStaffByRole(
            @Parameter(description = "Staff role to filter by") @PathVariable StaffRole staffRole) {
        logger.debug("Getting staff with role {}", staffRole);
        List<Staff> staffMembers = staffRoleService.getStaffByRole(staffRole);
        logger.debug("Found {} staff with role {}", staffMembers.size(), staffRole);
        return ResponseEntity.ok().body(mapper.convertToDTOList(staffMembers));
    }

    @PreAuthorize("hasAuthority('ALL_ADMIN') or hasAuthority('BUILDING_SUPERVISOR')")
    @Operation(summary = "Get Staff by Role and Status", description = "Retrieve all staff members with a specific role and status")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @JsonView(Views.RoleView.class)
    @GetMapping("/staff/roles/{staffRole}/status/{status}")
    public ResponseEntity<List<StaffDto>> getStaffByRoleAndStatus(
            @Parameter(description = "Staff role to filter by") @PathVariable StaffRole staffRole,
            @Parameter(description = "Staff status to filter by") @PathVariable StaffStatus status) {
        logger.debug("Getting staff with role {} and status {}", staffRole, status);
        List<Staff> staffMembers = staffRoleService.getStaffByRoleAndStatus(staffRole, status);
        logger.debug("Found {} staff with role {} and status {}", staffMembers.size(), staffRole, status);
        return ResponseEntity.ok().body(mapper.convertToDTOList(staffMembers));
    }
}
