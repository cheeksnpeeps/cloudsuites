package com.cloudsuites.framework.modules.property.features.module;

import com.cloudsuites.framework.modules.property.features.repository.AddressRepository;
import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.ManagementCompanyRepository;
import com.cloudsuites.framework.modules.property.personas.module.StaffServiceImpl;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.ManagementCompany;
import com.cloudsuites.framework.services.property.features.service.ManagementCompanyService;
import com.cloudsuites.framework.services.user.entities.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ManagementCompanyServiceImpl implements ManagementCompanyService {

    private final ManagementCompanyRepository managementCompanyRepository;

    private static final Logger logger = LoggerFactory.getLogger(ManagementCompanyServiceImpl.class);
    private final AddressRepository addressRepository;

    @Autowired
    public ManagementCompanyServiceImpl(ManagementCompanyRepository managementCompanyRepository, BuildingRepository buildingRepository, StaffServiceImpl staffServiceImpl, AddressRepository addressRepository) {
        this.managementCompanyRepository = managementCompanyRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public ManagementCompany getManagementCompanyById(String managementCompanyId) throws NotFoundResponseException {
        logger.debug("Entering getManagementCompanyById with managementCompanyId: {}", managementCompanyId);

        ManagementCompany managementCompany = managementCompanyRepository.findById(managementCompanyId)
                .orElseThrow(() -> {
                    logger.error("Management Company not found for ID: {}", managementCompanyId);
                    return new NotFoundResponseException("Management Company not found: " + managementCompanyId);
                });

        logger.debug("Management Company found: {}", managementCompany.getName());
        return managementCompany;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ManagementCompany> getAllManagementCompanies() {
        List<ManagementCompany> managementCompanies = managementCompanyRepository.findAll();
        logger.debug("Retrieved {} managementCompanies", managementCompanies.size());
        return managementCompanies;
    }

    @Transactional
    @Override
    public void deleteManagementCompanyById(String managementCompanyId) {
        logger.debug("Entering deleteManagementCompanyById with managementCompanyId: {}", managementCompanyId);
        managementCompanyRepository.deleteById(managementCompanyId);
        logger.debug("Management Company deleted: {}", managementCompanyId);
    }

    @Transactional
    @Override
    public ManagementCompany saveManagementCompany(ManagementCompany managementCompany) {
        logger.debug("Entering saveManagementCompany with managementCompany: {}", managementCompany.getName());
        Address address = addressRepository.save(managementCompany.getAddress());
        managementCompany.setAddress(address);
        ManagementCompany savedCompany = managementCompanyRepository.save(managementCompany);
        logger.debug("Created Management Company: {}", savedCompany.getName());
        return savedCompany;
    }
}

