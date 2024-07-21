package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.property.entities.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByIdentity_FirstName(String name);

    Optional<Staff> findByIdentity_UserId(Long userId);

    Optional<Staff> findByIdentity_Email(String email);

    Optional<List<Staff>> findByBuilding_BuildingId(Long buildingId);

    Optional<List<Staff>> findByManagementCompany_ManagementCompanyId(String managementCompanyId);

}
