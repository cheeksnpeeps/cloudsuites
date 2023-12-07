package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.entities.property.ManagementCompany;
import com.cloudsuites.framework.services.property.ManagementCompanyService;
import com.cloudsuites.framework.webapp.rest.property.dto.ManagementCompanyDto;
import com.cloudsuites.framework.webapp.rest.property.mapper.ManagementCompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ManagementCompanyDto> getManagementCompanyById(@PathVariable Long managementCompanyId) throws NotFoundResponseException {
        ManagementCompany managementCompany = managementCompanyService.getManagementCompanyById(managementCompanyId);
        return ResponseEntity.ok().body(mapper.convertToDTO(managementCompany));
    }

    @GetMapping("")
    public ResponseEntity<List<ManagementCompanyDto>> getAllPropertyManagementCompanies() {
        List<ManagementCompany> companies = managementCompanyService.getAllManagementCompanies();
        return ResponseEntity.ok().body(mapper.convertToDTOList(companies));
    }

    @PostMapping("")
    public ResponseEntity<ManagementCompanyDto> saveManagementCompany(@RequestBody ManagementCompanyDto managementCompanyDTO) {
        ManagementCompany managementCompany = mapper.convertToEntity(managementCompanyDTO);
        managementCompany = managementCompanyService.saveManagementCompany(managementCompany);
        return ResponseEntity.ok().body(mapper.convertToDTO(managementCompany));
    }

    @DeleteMapping("/{managementCompanyId}")
    public ResponseEntity<Void> deleteManagementCompanyById(@PathVariable Long managementCompanyId) {
       managementCompanyService.deleteManagementCompanyById(managementCompanyId);
         return ResponseEntity.ok().build();
    }



}
