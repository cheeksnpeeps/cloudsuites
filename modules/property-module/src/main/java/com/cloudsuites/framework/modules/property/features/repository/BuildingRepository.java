package com.cloudsuites.framework.modules.property.features.repository;

import com.cloudsuites.framework.services.property.features.entities.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, String> {

    Optional<List<Building>> findByCompany_CompanyId(String companyId);

    Optional<Building> findByCompany_CompanyIdAndBuildingId(String companyId, String buildingId);
}