package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.PropertyManagementCompanyRepository;
import com.cloudsuites.framework.services.entities.property.PropertyManagementCompany;
import com.cloudsuites.framework.services.property.PropertyManagementCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Component
@Transactional
public class PropertyManagementCompanyServiceImpl implements PropertyManagementCompanyService {

    private final PropertyManagementCompanyRepository companyRepository;

    @Autowired
    public PropertyManagementCompanyServiceImpl(PropertyManagementCompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public PropertyManagementCompany getPropertyManagementCompanyById(Long companyId) {
        return companyRepository.findById(companyId).orElse(null);
    }

    @Override
    public List<PropertyManagementCompany> getAllPropertyManagementCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public PropertyManagementCompany savePropertyManagementCompany(PropertyManagementCompany company) {
        return companyRepository.save(company);
    }

    @Override
    public void deletePropertyManagementCompanyById(Long companyId) {
        companyRepository.deleteById(companyId);
    }

}

