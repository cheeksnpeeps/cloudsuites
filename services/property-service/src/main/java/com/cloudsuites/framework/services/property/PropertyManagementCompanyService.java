package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.entities.property.PropertyManagementCompany;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface PropertyManagementCompanyService {

    public PropertyManagementCompany getPropertyManagementCompanyById(Long companyId);

    public List<PropertyManagementCompany> getAllPropertyManagementCompanies();

    public PropertyManagementCompany savePropertyManagementCompany(PropertyManagementCompany company);

    public void deletePropertyManagementCompanyById(Long companyId);

}

