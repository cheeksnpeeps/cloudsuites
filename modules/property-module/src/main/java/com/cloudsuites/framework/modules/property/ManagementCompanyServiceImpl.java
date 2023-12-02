package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.repository.ManagementCompanyRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.entities.property.Building;
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

    private final BuildingRepository buildingRepository;

    @Autowired
    public ManagementCompanyServiceImpl(ManagementCompanyRepository managementCompanyRepository, BuildingRepository buildingRepository) {
        this.managementCompanyRepository = managementCompanyRepository;
        this.buildingRepository = buildingRepository;
    }

    @Override
    public ManagementCompany getManagementCompanyById(Long managementCompanyId) throws NotFoundResponseException {
        return managementCompanyRepository.findById(managementCompanyId).orElseThrow(() -> new NotFoundResponseException("Management Company not found: "+managementCompanyId));
    }

    @Override
    public List<ManagementCompany> getAllManagementCompanies() {
        return managementCompanyRepository.findAll();
    }


    @Override
    public ManagementCompany saveManagementCompany(ManagementCompany managementCompany) {
        // Save ManagementCompany
        ManagementCompany savedCompany = managementCompanyRepository.save(managementCompany);

        // Set ManagementCompany in Building
        if (savedCompany.getBuildings() != null) {
            savedCompany.getBuildings().forEach(building -> {
                building.setManagementCompany(savedCompany);
                if (building.getFloors() != null)
                    building.getFloors().forEach(floor -> floor.setBuilding(building));
                if (building.getUnits() != null)
                    building.getUnits().forEach(unit -> unit.setBuilding(building));
            });

            List<Building> savedBuildings = buildingRepository.saveAll(savedCompany.getBuildings());
            savedCompany.setBuildings(savedBuildings);
            managementCompanyRepository.save(savedCompany);
        }

        return savedCompany;
    }

    @Override
    public void deleteManagementCompanyById(Long managementCompanyId) {
        managementCompanyRepository.deleteById(managementCompanyId);
    }

}

