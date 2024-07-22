package com.cloudsuites.framework.modules.property.personas.repository;

import com.cloudsuites.framework.services.property.personas.entities.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {

    Optional<Staff> findByIdentity_FirstName(String name);

    Optional<Staff> findByIdentity_UserId(String userId);

    Optional<Staff> findByIdentity_Email(String email);

    Optional<List<Staff>> findByBuilding_BuildingId(String buildingId);

    Optional<List<Staff>> findByCompany_CompanyId(String companyId);

}
