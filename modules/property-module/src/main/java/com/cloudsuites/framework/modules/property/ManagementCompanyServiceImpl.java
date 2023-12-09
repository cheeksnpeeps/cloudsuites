package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.repository.ManagementCompanyRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.entities.property.ManagementCompany;
import com.cloudsuites.framework.services.property.ManagementCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ManagementCompanyServiceImpl implements ManagementCompanyService {

    private final ManagementCompanyRepository managementCompanyRepository;

    @Autowired
    public ManagementCompanyServiceImpl(ManagementCompanyRepository managementCompanyRepository, BuildingRepository buildingRepository) {
        this.managementCompanyRepository = managementCompanyRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public ManagementCompany getManagementCompanyById(Long managementCompanyId) throws NotFoundResponseException {
        return managementCompanyRepository.findById(managementCompanyId).orElseThrow(() -> new NotFoundResponseException("Management Company not found: "+managementCompanyId));
    }
    @Transactional(readOnly = true)
    @Override
    public List<ManagementCompany> getAllManagementCompanies() {
        return managementCompanyRepository.findAll();
    }

    @Transactional
    @Override
    public ManagementCompany saveManagementCompany(ManagementCompany managementCompany) {
        // Save ManagementCompany
        ManagementCompany savedCompany = managementCompanyRepository.save(managementCompany);

        // Set ManagementCompany in Building
        if (savedCompany.getBuildings() != null) {
            savedCompany.getBuildings().forEach(building -> {
                building.setManagementCompany(savedCompany);
                if (building.getFloors() != null)
                    building.getFloors().forEach(
                            floor -> {
                                floor.setBuilding(building);
                                if (floor.getUnits() != null)
                                    floor.getUnits().forEach(unit -> unit.setFloor(floor));
                            }
                    );
                if (building.getUnits() != null)
                    building.getUnits().forEach(unit -> unit.setBuilding(building));
            });
            managementCompanyRepository.save(savedCompany);
        }

        return savedCompany;
    }
    @Transactional
    @Override
    public void deleteManagementCompanyById(Long managementCompanyId) {
        managementCompanyRepository.deleteById(managementCompanyId);
    }

}

