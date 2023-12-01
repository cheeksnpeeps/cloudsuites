package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.repository.ManagementCompanyRepository;
import com.cloudsuites.framework.services.entities.property.Building;
import com.cloudsuites.framework.services.entities.property.ManagementCompany;
import com.cloudsuites.framework.services.property.ManagementCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class ManagementCompanyServiceImpl implements ManagementCompanyService {

    private final ManagementCompanyRepository managementCompanyRepository;

    @Autowired
    public ManagementCompanyServiceImpl(ManagementCompanyRepository managementCompanyRepository, BuildingRepository buildingRepository) {
        this.managementCompanyRepository = managementCompanyRepository;
    }

    @Override
    public ManagementCompany getManagementCompanyById(Long managementCompanyId) {
        return managementCompanyRepository.findById(managementCompanyId).orElse(null);
    }

    @Override
    public List<ManagementCompany> getAllManagementCompanies() {
        return managementCompanyRepository.findAll();
    }

    // Query all management companies with their buildings
    @Override
    public Optional<ManagementCompany> getManagementCompanyByIdWithBuildings(Long managementCompanyId) {
        return managementCompanyRepository.findById(managementCompanyId)
                .map(managementCompany -> {
                    // Force fetching of buildings
                    List<Building> buildings = managementCompany.getBuildings();
                    // Now 'buildings' should be populated with the actual data.
                    return managementCompany;
                });
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

