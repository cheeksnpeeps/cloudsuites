package com.cloudsuites.framework.services.property.features.service;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Company;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CompanyService {

    Company getCompanyById(String companyId) throws NotFoundResponseException;

    List<Company> getAllManagementCompanies();

    Company saveCompany(Company company);

    void deleteCompanyById(String companyId) throws NotFoundResponseException;
}

