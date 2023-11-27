package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.services.common.entities.property.PropertyManagementCompany;
import com.cloudsuites.framework.services.property.PropertyManagementCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class PropertyManagementCompanyServiceImpl implements PropertyManagementCompanyService {

    private final PropertyManagementCompanyRepository companyRepository;

    @Autowired
    public PropertyManagementCompanyServiceImpl(PropertyManagementCompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public PropertyManagementCompany getCompanyById(Long companyId) {
        return companyRepository.findById(companyId).orElse(null);
    }

    @Override
    public List<PropertyManagementCompany> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public PropertyManagementCompany saveCompany(PropertyManagementCompany company) {
        return companyRepository.save(company);
    }

    @Override
    public void deleteCompanyById(Long companyId) {
        companyRepository.deleteById(companyId);
    }

    @Override
    public PropertyManagementCompany getCompanyWithAssociations(Long companyId) {
        // Retrieve a PropertyManagementCompany with associated sub-entities
        Optional<PropertyManagementCompany> companyOptional = companyRepository.findById(companyId);
        return companyOptional.orElse(null);
    }
}

