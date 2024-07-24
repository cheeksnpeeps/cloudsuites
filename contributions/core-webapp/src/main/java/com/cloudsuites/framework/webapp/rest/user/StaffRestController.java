package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.service.BuildingService;
import com.cloudsuites.framework.services.property.features.service.CompanyService;
import com.cloudsuites.framework.services.property.personas.entities.Staff;
import com.cloudsuites.framework.services.property.personas.service.StaffService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staff")
@Tags(value = {@Tag(name = "Staff", description = "Operations related to staffs")})
public class StaffRestController {

    private static final Logger logger = LoggerFactory.getLogger(StaffRestController.class);
    private final StaffService staffService;
    private final StaffMapper mapper;
    private final UserService userService;
    private final CompanyService companyService;
    private final BuildingService buildingService;

    @Autowired
    public StaffRestController(StaffService staffService, StaffMapper mapper, UserService userService, CompanyService companyService, BuildingService buildingService) {
        this.staffService = staffService;
        this.mapper = mapper;
        this.userService = userService;
        this.companyService = companyService;
        this.buildingService = buildingService;
    }

    @Operation(summary = "Get All Staffs by Company ID", description = "Retrieve all staffs")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Staffs not found")
    @GetMapping("/companies/{companyId}")
    @JsonView(Views.StaffView.class)
    public ResponseEntity<List<StaffDto>> getAllStaffsByCompanyId(@PathVariable String companyId) {
        logger.info("Fetching all staffs by Company Id");
        try {
            List<Staff> staffs = staffService.getAllStaffByCompany(companyId);
            logger.info("Fetched {} staffs by Company Id", staffs.size());
            return ResponseEntity.ok().body(mapper.convertToDTOList(staffs));
        } catch (NotFoundResponseException e) {
            logger.error("Staffs not found : {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create a new Staff for Company", description = "Create a new staff for Company")
    @ApiResponse(responseCode = "201", description = "Staff created successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @PostMapping("/companies/{companyId}")
    @JsonView(Views.StaffView.class)
    public ResponseEntity<StaffDto> createStaff(@RequestBody @Parameter(description = "Staff details to be saved") StaffDto staffDto,
                                                @PathVariable String companyId) throws NotFoundResponseException {
        Staff staff = mapper.convertToEntity(staffDto);
        staff.setCompany(companyService.getCompanyById(companyId));
        logger.info("Creating a new staff for Company with ID: {}", companyId);
        Identity identity = userService.createUser(staff.getIdentity());
        staff.setIdentity(identity);
        Staff createdStaff = staffService.createStaff(staff);
        logger.info("Staff created with ID: {}", createdStaff.getStaffId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(createdStaff));
    }

    @Operation(summary = "Get All Staffs by Building ID", description = "Retrieve all staffs")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Staffs not found")
    @GetMapping("/buildings/{buildingId}")
    @JsonView(Views.StaffView.class)
    public ResponseEntity<List<StaffDto>> getAllStaffsByBuildingId(@PathVariable String buildingId) {
        logger.info("Fetching all staffs");
        try {
            List<Staff> staffs = staffService.getAllStaffsByBuilding(buildingId);
            logger.info("Fetched {} staffs", staffs.size());
            return ResponseEntity.ok().body(mapper.convertToDTOList(staffs));
        } catch (NotFoundResponseException e) {
            logger.error("Staffs not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create a new Building Staff", description = "Create a new Building staff")
    @ApiResponse(responseCode = "201", description = "Staff created successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @PostMapping("/companies/{companyId}/building/{buildingId}")
    @JsonView(Views.StaffView.class)
    public ResponseEntity<StaffDto> createBuildingStaff(@RequestBody @Parameter(description = "Staff details to be saved") StaffDto staffDto,
                                                        @PathVariable String companyId,
                                                        @PathVariable String buildingId) throws NotFoundResponseException {
        Staff staff = mapper.convertToEntity(staffDto);
        staff.setCompany(companyService.getCompanyById(companyId));
        staff.setBuilding(buildingService.getBuildingById(buildingId));
        logger.info("Creating a new staff");
        Identity identity = userService.createUser(staff.getIdentity());
        staff.setIdentity(identity);
        Staff createdStaff = staffService.createStaff(staff);
        logger.info("Staff created with ID: {}", createdStaff.getStaffId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(createdStaff));
    }

    @Operation(summary = "Get Staff by ID", description = "Retrieve staff details by ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Staff not found")
    @GetMapping("/{id}")
    @JsonView(Views.StaffView.class)
    public ResponseEntity<StaffDto> getStaffById(@Parameter(description = "ID of the staff to be retrieved") @PathVariable String id) {
        logger.info("Fetching staff with ID: {}", id);
        try {
            Staff staff = staffService.getStaffById(id);
            logger.info("Staff fetched with ID: {}", staff.getStaffId());
            return ResponseEntity.ok().body(mapper.convertToDTO(staff));
        } catch (NotFoundResponseException e) {
            logger.error("Staff not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update Staff by ID", description = "Update staff details by ID")
    @ApiResponse(responseCode = "200", description = "Staff updated successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Staff not found")
    @PutMapping("/{id}")
    @JsonView(Views.StaffView.class)
    public ResponseEntity<StaffDto> updateStaff(@Parameter(description = "ID of the staff to be updated") @PathVariable String id,
                                                @RequestBody @Parameter(description = "Updated staff details") StaffDto staffDto) {
        logger.info("Updating staff with ID: {}", id);
        try {
            Staff staff = mapper.convertToEntity(staffDto);
            staff = staffService.updateStaff(id, staff);
            logger.info("Staff updated successfully with ID: {}", id);
            return ResponseEntity.ok().body(mapper.convertToDTO(staff));
        } catch (NotFoundResponseException e) {
            logger.error("Staff not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete Staff by ID", description = "Delete an staff by ID")
    @ApiResponse(responseCode = "204", description = "Staff deleted successfully")
    @ApiResponse(responseCode = "404", description = "Staff not found")
    @DeleteMapping("/{staffId}")
    public ResponseEntity<Void> deleteStaff(@Parameter(description = "ID of the staff to be deleted") @PathVariable String staffId) throws NotFoundResponseException {
        logger.info("Deleting staff with ID: {}", staffId);
        staffService.deleteStaff(staffId);
        logger.info("Staff deleted successfully with ID: {}", staffId);
        return ResponseEntity.noContent().build();
    }
}
