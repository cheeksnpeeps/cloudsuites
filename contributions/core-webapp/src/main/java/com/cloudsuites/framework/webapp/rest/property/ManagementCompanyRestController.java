package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.entities.property.ManagementCompany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import com.cloudsuites.framework.services.property.ManagementCompanyService;
import com.cloudsuites.framework.webapp.rest.property.dto.ManagementCompanyDTO;
import com.cloudsuites.framework.webapp.rest.property.mapper.ManagementCompanyMapper;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class ManagementCompanyRestController {

    @Qualifier("managementCompanyServiceImpl")
    private final ManagementCompanyService managementCompanyService;
    private final ManagementCompanyMapper mapper;

    @Autowired
    public ManagementCompanyRestController(@Qualifier("managementCompanyServiceImpl") ManagementCompanyService managementCompanyService, ManagementCompanyMapper mapper) {
        this.managementCompanyService = managementCompanyService;
        this.mapper = mapper;
    }

    @GetMapping("/{managementCompanyId}")
    public ManagementCompanyDTO getManagementCompanyById(@PathVariable Long managementCompanyId) {
        ManagementCompany managementCompany = managementCompanyService.getManagementCompanyById(managementCompanyId);
        return mapper.convertToDTO(managementCompany);
    }

    @GetMapping("/")
    public List<ManagementCompanyDTO> getAllPropertyManagementCompanies() {
        List<ManagementCompany> companies = managementCompanyService.getAllPropertyManagementCompanies();
        return mapper.convertToDTOList(companies);
    }

    @PostMapping("/")
    public ManagementCompanyDTO saveManagementCompany(@RequestBody ManagementCompanyDTO managementCompanyDTO) {
        ManagementCompany managementCompany = mapper.convertToEntity(managementCompanyDTO);
        managementCompany = managementCompanyService.saveManagementCompany(managementCompany);
        return mapper.convertToDTO(managementCompany);
    }

    @DeleteMapping("/{managementCompanyId}")
    public void deleteManagementCompanyById(@PathVariable Long managementCompanyId) {
        managementCompanyService.deleteManagementCompanyById(managementCompanyId);
    }



}
