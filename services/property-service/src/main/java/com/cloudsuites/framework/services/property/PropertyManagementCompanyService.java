package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.entities.property.PropertyManagementCompany;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface PropertyManagementCompanyService {

    public PropertyManagementCompany getCompanyById(Long companyId);

    public List<PropertyManagementCompany> getAllCompanies();

    public PropertyManagementCompany saveCompany(PropertyManagementCompany company);

    public void deleteCompanyById(Long companyId);

    public PropertyManagementCompany getCompanyWithAssociations(Long companyId);
}

