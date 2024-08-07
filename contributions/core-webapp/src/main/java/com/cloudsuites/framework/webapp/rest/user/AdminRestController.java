package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UsernameAlreadyExistsException;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.user.AdminService;
import com.cloudsuites.framework.services.user.entities.Admin;
import com.cloudsuites.framework.webapp.authentication.util.WebAppConstants;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins")
@Tags(value = {@Tag(name = "Admins", description = "Operations related to admins")})
public class AdminRestController {

    private static final Logger logger = LoggerFactory.getLogger(AdminRestController.class);
    private final AdminService adminService;
    private final AdminMapper mapper;

    @Autowired
    public AdminRestController(AdminService adminService, AdminMapper mapper, UnitService unitService) {
        this.adminService = adminService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAnyAuthority('ALL_ADMIN')")
    @Operation(summary = "Get All Admins", description = "Retrieve all admins")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Admins not found")
    @GetMapping("")
    @JsonView(Views.AdminView.class)
    public ResponseEntity<List<AdminDto>> getAllAdmins() throws NotFoundResponseException {

        logger.info(WebAppConstants.Admin.LOG_FETCHING_ADMINS);
        List<Admin> admins = adminService.getAllAdmins();
        logger.info("Fetched {} admins", admins.size());
        return ResponseEntity.ok(mapper.convertToDTOList(admins));
    }

    @PreAuthorize("hasAuthority('ALL_ADMIN')")
    @Operation(summary = "Get Admin by ID", description = "Retrieve admin details by ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Admin not found")
    @GetMapping("/{adminId}")
    @JsonView(Views.AdminView.class)
    public ResponseEntity<AdminDto> getAdminById(@Parameter(description = "ID of the admin to be retrieved") @PathVariable String adminId) {
        logger.info(WebAppConstants.Admin.LOG_FETCHING_ADMIN_BY_ID, adminId);
        try {
            Admin admin = adminService.getAdminById(adminId);
            logger.info(WebAppConstants.Admin.LOG_ADMIN_FETCHED, admin.getAdminId());
            return ResponseEntity.ok(mapper.convertToDTO(admin));
        } catch (NotFoundResponseException e) {
            logger.error(WebAppConstants.Admin.LOG_ADMIN_NOT_FOUND, adminId);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create Admin", description = "Create admin with details")
    @ApiResponse(responseCode = "201", description = "Admin created successfully", content = @Content(mediaType = "application/json"))
    @PostMapping("")
    @JsonView(Views.AdminView.class)
    public ResponseEntity<AdminDto> createAdmin(@Valid @RequestBody @Parameter(description = "Admin details") AdminDto adminDto) throws InvalidOperationException, UsernameAlreadyExistsException {
        logger.debug(WebAppConstants.Admin.LOG_REGISTERING_ADMIN, adminDto.getIdentity().getUsername());
        Admin admin = mapper.convertToEntity(adminDto);
        admin = adminService.createAdmin(admin);
        logger.info(WebAppConstants.Admin.LOG_ADMIN_REGISTERED_SUCCESS, admin.getAdminId(), adminDto.getIdentity().getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(admin));
    }

    @PreAuthorize("hasAuthority('ALL_ADMIN')")
    @Operation(summary = "Update Admin by ID", description = "Update admin details by ID")
    @ApiResponse(responseCode = "200", description = "Admin updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Admin not found")
    @PutMapping("/{adminId}")
    @JsonView(Views.AdminView.class)
    public ResponseEntity<AdminDto> updateAdmin(
            @Parameter(description = "ID of the admin to be updated") @PathVariable String adminId,
            @Valid @RequestBody @Parameter(description = "Updated admin details") AdminDto adminDto) throws NotFoundResponseException {
        logger.info(WebAppConstants.Admin.LOG_UPDATING_ADMIN, adminId);
        Admin admin = mapper.convertToEntity(adminDto);
        admin = adminService.updateAdmin(adminId, admin);
        logger.info(WebAppConstants.Admin.LOG_ADMIN_UPDATED, adminId);
        return ResponseEntity.ok(mapper.convertToDTO(admin));
    }

    @PreAuthorize("hasAuthority('ALL_ADMIN')")
    @Operation(summary = "Delete Admin by ID", description = "Delete an admin by ID")
    @ApiResponse(responseCode = "204", description = "Admin deleted successfully")
    @ApiResponse(responseCode = "404", description = "Admin not found")
    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdmin(@Parameter(description = "ID of the admin to be deleted") @PathVariable String adminId) throws InvalidOperationException, NotFoundResponseException {
        logger.info(WebAppConstants.Admin.LOG_DELETING_ADMIN, adminId);
        adminService.deleteAdmin(adminId);
        logger.info(WebAppConstants.Admin.LOG_ADMIN_DELETED, adminId);
        return ResponseEntity.noContent().build();
    }
}