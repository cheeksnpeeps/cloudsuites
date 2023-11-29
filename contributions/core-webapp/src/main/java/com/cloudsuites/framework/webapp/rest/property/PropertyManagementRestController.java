package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.entities.property.PropertyManagementCompany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import com.cloudsuites.framework.services.property.PropertyManagementCompanyService;
import com.cloudsuites.framework.webapp.rest.property.dto.PropertyManagementCompanyDTO;
import com.cloudsuites.framework.webapp.rest.property.mapper.PropertyManagementCompanyMapper;

import java.util.List;


@RestController("/api/companies")
public class PropertyManagementRestController {

    @Qualifier("propertyManagementCompanyServiceImpl")
    private final PropertyManagementCompanyService companyService;
    private final PropertyManagementCompanyMapper mapper;

    @Autowired
    public PropertyManagementRestController(@Qualifier("propertyManagementCompanyServiceImpl") PropertyManagementCompanyService companyService, PropertyManagementCompanyMapper mapper) {
        this.companyService = companyService;
        this.mapper = mapper;
    }

    @GetMapping("/{companyId}")
    public PropertyManagementCompanyDTO getPropertyManagementCompanyById(@PathVariable Long companyId) {
        PropertyManagementCompany company = companyService.getPropertyManagementCompanyById(companyId);
        return mapper.convertToDTO(company);
    }

    @GetMapping("/")
    public List<PropertyManagementCompanyDTO> getAllPropertyManagementCompanies() {
        List<PropertyManagementCompany> companies = companyService.getAllPropertyManagementCompanies();
        return mapper.convertToDTOList(companies);
    }

    @PostMapping("/")
    public PropertyManagementCompanyDTO savePropertyManagementCompany(@RequestBody PropertyManagementCompanyDTO companyDTO) {
        PropertyManagementCompany company = mapper.convertToEntity(companyDTO);
        company = companyService.savePropertyManagementCompany(company);
        return mapper.convertToDTO(company);
    }

    @DeleteMapping("/{companyId}")
    public void deletePropertyManagementCompanyById(@PathVariable Long companyId) {
        companyService.deletePropertyManagementCompanyById(companyId);
    }



}
