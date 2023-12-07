package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.entities.property.Building;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    public Optional<List<Building>> findByManagementCompany_ManagementCompanyId(Long managementCompanyId);

    @EntityGraph(attributePaths = "units")
    public Optional<Building> findById(Long buildingId);

}