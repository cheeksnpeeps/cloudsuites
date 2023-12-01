package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.entities.property.ManagementCompany;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ManagementCompanyService {

    public ManagementCompany getManagementCompanyById(Long companyId);

    List<ManagementCompany> getAllManagementCompanies();

    // Query all management companies with their buildings
    Optional<ManagementCompany> getManagementCompanyByIdWithBuildings(Long managementCompanyId);

    public ManagementCompany saveManagementCompany(ManagementCompany company);

    public void deleteManagementCompanyById(Long companyId);
}

