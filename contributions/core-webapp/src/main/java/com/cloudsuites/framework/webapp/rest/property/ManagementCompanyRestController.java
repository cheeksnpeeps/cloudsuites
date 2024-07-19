package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.ManagementCompanyService;
import com.cloudsuites.framework.services.property.entities.ManagementCompany;
import com.cloudsuites.framework.webapp.rest.property.dto.ManagementCompanyDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.property.mapper.ManagementCompanyMapper;
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
@RequestMapping("/api/v1/companies")
@Tags(value = {@Tag(name = "Management Companies", description = "Operations related to property management companies")})
public class ManagementCompanyRestController {

    private static final Logger logger = LoggerFactory.getLogger(ManagementCompanyRestController.class);

    private final ManagementCompanyService managementCompanyService;
    private final ManagementCompanyMapper mapper;

    @Autowired
    public ManagementCompanyRestController(ManagementCompanyService managementCompanyService, ManagementCompanyMapper mapper) {
        this.managementCompanyService = managementCompanyService;
        this.mapper = mapper;
    }

    @JsonView(Views.ManagementCompanyView.class)
    @Operation(summary = "Get All Property Management Companies", description = "Retrieve a list of all property management companies")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @GetMapping("")
    //@PreAuthorize("hasAuthority('Administrator')")
    public ResponseEntity<List<ManagementCompanyDto>> getAllPropertyManagementCompanies() {
        logger.debug("Getting all property management companies");
        List<ManagementCompany> companies = managementCompanyService.getAllManagementCompanies();
        logger.debug("Found {} property management companies", companies.size());
        return ResponseEntity.ok().body(mapper.convertToDTOList(companies));
    }

    @Operation(summary = "Save a Property Management Company", description = "Create a new property management company")
    @ApiResponse(responseCode = "201", description = "Property management company created successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @JsonView(Views.ManagementCompanyView.class)
    @PostMapping("")
    public ResponseEntity<ManagementCompanyDto> saveManagementCompany(
            @RequestBody @Parameter(description = "Property management company details to be saved") ManagementCompanyDto managementCompanyDTO) {
        logger.debug("Saving property management company {}", managementCompanyDTO.getName());
        ManagementCompany managementCompany = mapper.convertToEntity(managementCompanyDTO);
        managementCompany = managementCompanyService.saveManagementCompany(managementCompany);
        logger.debug("Property management company saved successfully: {} {}", managementCompany.getManagementCompanyId(), managementCompany.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(managementCompany));
    }

    @Operation(summary = "Delete a Property Management Company by ID", description = "Delete a property management company based on its ID")
    @ApiResponse(responseCode = "204", description = "Property management company deleted successfully")
    @ApiResponse(responseCode = "404", description = "Property management company not found")
    @DeleteMapping("/{managementCompanyId}")
    @JsonView(Views.ManagementCompanyView.class)
    public ResponseEntity<Void> deleteManagementCompanyById(
            @Parameter(description = "ID of the property management company to be deleted") @PathVariable Long managementCompanyId) {
        logger.debug("Deleting property management company {}", managementCompanyId);
        managementCompanyService.deleteManagementCompanyById(managementCompanyId);
        logger.debug("Property management company deleted successfully: {}", managementCompanyId);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Get a Property Management Company by ID", description = "Retrieve property management company details based on its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Property management company not found")
    @JsonView(Views.ManagementCompanyView.class)
    @GetMapping("/{managementCompanyId}")
    public ResponseEntity<ManagementCompanyDto> getManagementCompanyById(
            @Parameter(description = "ID of the property management company to be retrieved") @PathVariable Long managementCompanyId)
            throws NotFoundResponseException {
        logger.debug("Getting property management company {}", managementCompanyId);
        ManagementCompany managementCompany = managementCompanyService.getManagementCompanyById(managementCompanyId);
        logger.debug("Found property management company {}", managementCompanyId);
        return ResponseEntity.ok().body(mapper.convertToDTO(managementCompany));
    }
}
