package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.ManagementCompanyRepository;
import com.cloudsuites.framework.services.entities.property.ManagementCompany;
import com.cloudsuites.framework.services.property.ManagementCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Component
@Transactional
public class ManagementCompanyServiceImpl implements ManagementCompanyService {

    private final ManagementCompanyRepository managementCompanyRepository;

    @Autowired
    public ManagementCompanyServiceImpl(ManagementCompanyRepository managementCompanyRepository) {
        this.managementCompanyRepository = managementCompanyRepository;
    }

    @Override
    public ManagementCompany getManagementCompanyById(Long managementCompanyId) {
        return managementCompanyRepository.findById(managementCompanyId).orElse(null);
    }

    @Override
    public List<ManagementCompany> getAllPropertyManagementCompanies() {
        return managementCompanyRepository.findAll();
    }

    @Override
    public ManagementCompany saveManagementCompany(ManagementCompany managementCompany) {
        return managementCompanyRepository.save(managementCompany);
    }

    @Override
    public void deleteManagementCompanyById(Long managementCompanyId) {
        managementCompanyRepository.deleteById(managementCompanyId);
    }

}

