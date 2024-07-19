package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.property.entities.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByIdentity_FirstName(String name);

    Optional<Owner> findByIdentity_UserId(Long userId);

    Optional<Owner> findByIdentity_Email(String email);
}