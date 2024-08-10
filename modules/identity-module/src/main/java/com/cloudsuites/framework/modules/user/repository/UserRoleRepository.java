package com.cloudsuites.framework.modules.user.repository;

import com.cloudsuites.framework.services.user.entities.UserRole;
import com.cloudsuites.framework.services.user.entities.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {

    @Transactional(readOnly = true)
    List<UserRole> findByIdentityId(String identityId);

    @Transactional(readOnly = true)
    List<UserRole> findByPersonaId(String personaId);

    @Transactional(readOnly = true)
    List<UserRole> findByUserType(UserType userType);

    @Transactional(readOnly = true)
    List<UserRole> findByRole(String role);

    @Transactional(readOnly = true)
    List<UserRole> findUserRoleByIdentityId(String userId);

    @Transactional(readOnly = true)
    List<UserRole> findUserRoleByPersonaId(String adminId);
}
