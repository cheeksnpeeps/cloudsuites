package com.cloudsuites.framework.modules.property.personas.repository;

import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.entities.OwnerRole;
import com.cloudsuites.framework.services.property.personas.entities.OwnerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, String> {

    @Transactional(readOnly = true)
    Optional<Owner> findByIdentity_FirstName(String name);

    @Transactional(readOnly = true)
    Optional<Owner> findByIdentity_UserId(String userId);

    @Transactional(readOnly = true)
    Optional<Owner> findByIdentity_Email(String email);

    @Transactional(readOnly = true)
    List<Owner> findByRoleAndStatus(OwnerRole ownerRole, OwnerStatus status);

    @Transactional(readOnly = true)
    List<Owner> findByRole(OwnerRole ownerRole);
}
