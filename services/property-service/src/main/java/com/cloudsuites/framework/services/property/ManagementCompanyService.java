package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.entities.property.ManagementCompany;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ManagementCompanyService {

    public ManagementCompany getManagementCompanyById(Long companyId) throws NotFoundResponseException;

    List<ManagementCompany> getAllManagementCompanies();

    public ManagementCompany saveManagementCompany(ManagementCompany company);

    public void deleteManagementCompanyById(Long companyId);
}

