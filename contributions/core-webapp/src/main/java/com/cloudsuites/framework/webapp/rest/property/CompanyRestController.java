package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Company;
import com.cloudsuites.framework.services.property.features.service.CompanyService;
import com.cloudsuites.framework.webapp.rest.property.dto.CompanyDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.property.mapper.CompanyMapper;
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
public class CompanyRestController {

    private static final Logger logger = LoggerFactory.getLogger(CompanyRestController.class);

    private final CompanyService companyService;
    private final CompanyMapper mapper;

    @Autowired
    public CompanyRestController(CompanyService companyService, CompanyMapper mapper) {
        this.companyService = companyService;
        this.mapper = mapper;
    }

    @JsonView(Views.CompanyView.class)
    @Operation(summary = "Get All Property Management Companies", description = "Retrieve a list of all property management companies")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @GetMapping("")
    //@PreAuthorize("hasAuthority('Administrator')")
    public ResponseEntity<List<CompanyDto>> getAllPropertyManagementCompanies() {
        logger.debug("Getting all property management companies");
        List<Company> companies = companyService.getAllManagementCompanies();
        logger.debug("Found {} property management companies", companies.size());
        return ResponseEntity.ok().body(mapper.convertToDTOList(companies));
    }

    @Operation(summary = "Save a Property Management Company", description = "Create a new property management company")
    @ApiResponse(responseCode = "201", description = "Property management company created successfully", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @JsonView(Views.CompanyView.class)
    @PostMapping("")
    public ResponseEntity<CompanyDto> saveCompany(
            @RequestBody @Parameter(description = "Property management company details to be saved") CompanyDto companyDTO) {
        logger.debug("Saving property management company {}", companyDTO.getName());
        Company company = mapper.convertToEntity(companyDTO);
        company = companyService.saveCompany(company);
        logger.debug("Property management company saved successfully: {} {}", company.getCompanyId(), company.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertToDTO(company));
    }

    @Operation(summary = "Delete a Property Management Company by ID", description = "Delete a property management company based on its ID")
    @ApiResponse(responseCode = "204", description = "Property management company deleted successfully")
    @ApiResponse(responseCode = "404", description = "Property management company not found")
    @DeleteMapping("/{companyId}")
    @JsonView(Views.CompanyView.class)
    public ResponseEntity<Void> deleteCompanyById(
            @Parameter(description = "ID of the property management company to be deleted") @PathVariable String companyId) {
        logger.debug("Deleting property management company {}", companyId);
        companyService.deleteCompanyById(companyId);
        logger.debug("Property management company deleted successfully: {}", companyId);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Get a Property Management Company by ID", description = "Retrieve property management company details based on its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Property management company not found")
    @JsonView(Views.CompanyView.class)
    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDto> getCompanyById(
            @Parameter(description = "ID of the property management company to be retrieved") @PathVariable String companyId)
            throws NotFoundResponseException {
        logger.debug("Getting property management company {}", companyId);
        Company company = companyService.getCompanyById(companyId);
        logger.debug("Found property management company {}", companyId);
        return ResponseEntity.ok().body(mapper.convertToDTO(company));
    }
}
