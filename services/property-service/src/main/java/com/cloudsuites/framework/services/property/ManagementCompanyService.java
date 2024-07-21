package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.entities.ManagementCompany;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ManagementCompanyService {

    ManagementCompany getManagementCompanyById(String companyId) throws NotFoundResponseException;

    List<ManagementCompany> getAllManagementCompanies();

    ManagementCompany saveManagementCompany(ManagementCompany company);

    void deleteManagementCompanyById(String companyId);
}

