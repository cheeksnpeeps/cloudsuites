package com.cloudsuites.framework.modules.property.features.module;

import com.cloudsuites.framework.modules.property.features.repository.AddressRepository;
import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.CompanyRepository;
import com.cloudsuites.framework.modules.property.personas.module.StaffServiceImpl;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Company;
import com.cloudsuites.framework.services.property.features.service.CompanyService;
import com.cloudsuites.framework.services.user.entities.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CompanyServiceImpl implements CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);
    private final CompanyRepository companyRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository, BuildingRepository buildingRepository, StaffServiceImpl staffServiceImpl, AddressRepository addressRepository) {
        this.companyRepository = companyRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Company getCompanyById(String companyId) throws NotFoundResponseException {
        logger.debug("Entering getCompanyById with companyId: {}", companyId);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> {
                    logger.error("Management Company not found for ID: {}", companyId);
                    return new NotFoundResponseException("Management Company not found: " + companyId);
                });

        logger.debug("Management Company found: {}", company.getName());
        return company;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Company> getAllManagementCompanies() {
        List<Company> managementCompanies = companyRepository.findAll();
        logger.debug("Retrieved {} managementCompanies", managementCompanies.size());
        return managementCompanies;
    }

    @Transactional
    @Override
    public void deleteCompanyById(String companyId) throws NotFoundResponseException {
        logger.debug("Entering deleteCompanyById with companyId: {}", companyId);
        if (!companyRepository.existsById(companyId)) {
            logger.error("Management Company not found for ID: {}", companyId);
            throw new NotFoundResponseException("Management Company not found: " + companyId);
        }
        companyRepository.deleteById(companyId);
        logger.debug("Management Company deleted: {}", companyId);
    }

    @Transactional
    @Override
    public Company saveCompany(Company company) {
        logger.debug("Entering saveCompany with company: {}", company.getName());
        Address address = addressRepository.save(company.getAddress());
        company.setAddress(address);
        Company savedCompany = companyRepository.save(company);
        logger.debug("Created Management Company: {}", savedCompany.getName());
        return savedCompany;
    }
}

