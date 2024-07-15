package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.property.entities.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    public Optional<List<Building>> findByManagementCompany_ManagementCompanyId(Long managementCompanyId);

}