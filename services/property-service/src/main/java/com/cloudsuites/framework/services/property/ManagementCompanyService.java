package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.entities.property.ManagementCompany;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ManagementCompanyService {

    public ManagementCompany getManagementCompanyById(Long managementCompanyId);

    public List<ManagementCompany> getAllPropertyManagementCompanies();

    public ManagementCompany saveManagementCompany(ManagementCompany managementCompany);

    public void deleteManagementCompanyById(Long managementCompanyId);

}

