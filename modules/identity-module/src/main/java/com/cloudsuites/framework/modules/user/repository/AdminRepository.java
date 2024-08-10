package com.cloudsuites.framework.modules.user.repository;

import com.cloudsuites.framework.services.user.entities.Admin;
import com.cloudsuites.framework.services.user.entities.AdminRole;
import com.cloudsuites.framework.services.user.entities.AdminStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {

    Optional<Admin> findByIdentity_FirstName(String name);

    Optional<Admin> findByIdentity_UserId(String userId);

    Optional<Admin> findByIdentity_Email(String email);

    List<Admin> findByRole(AdminRole adminRole);

    List<Admin> findByRoleAndStatus(AdminRole adminRole, AdminStatus status);
}
