package com.cloudsuites.framework.services.user;

import com.cloudsuites.framework.services.user.entities.UserRole;
import com.cloudsuites.framework.services.user.entities.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {

    // Custom query to find roles by identity ID
    List<UserRole> findByIdentityId(Long identityId);

    // Custom query to find roles by persona ID
    List<UserRole> findByPersonaId(Long personaId);

    // Custom query to find roles by user type
    List<UserRole> findByUserType(UserType userType);

    // Custom query to find roles by role name
    List<UserRole> findByRole(String role);

    List<UserRole> findUserRoleByIdentity_UserIdAndAndPersonaIdAndUserType(String userId, String personaId, UserType userType);
}
