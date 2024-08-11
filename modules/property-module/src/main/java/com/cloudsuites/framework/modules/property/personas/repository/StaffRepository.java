package com.cloudsuites.framework.modules.property.personas.repository;

import com.cloudsuites.framework.services.property.personas.entities.Staff;
import com.cloudsuites.framework.services.property.personas.entities.StaffRole;
import com.cloudsuites.framework.services.property.personas.entities.StaffStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {

    @Transactional(readOnly = true)
    Optional<Staff> findByIdentity_FirstName(String name);

    @Transactional(readOnly = true)
    Optional<Staff> findByIdentity_UserId(String userId);

    @Transactional(readOnly = true)
    Optional<Staff> findByIdentity_Email(String email);

    @Transactional(readOnly = true)
    Optional<List<Staff>> findByBuilding_BuildingId(String buildingId);

    @Transactional(readOnly = true)
    Optional<List<Staff>> findByCompany_CompanyId(String companyId);

    @Transactional(readOnly = true)
    List<Staff> findByRole(StaffRole staffRole);

    @Transactional(readOnly = true)
    List<Staff> findByRoleAndStatus(StaffRole staffRole, StaffStatus status);
}
