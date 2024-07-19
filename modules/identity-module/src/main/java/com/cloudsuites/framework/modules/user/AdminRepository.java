package com.cloudsuites.framework.modules.user;

import com.cloudsuites.framework.services.user.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByIdentity_FirstName(String name);

    Optional<Admin> findByIdentity_UserId(Long userId);

    Optional<Admin> findByIdentity_Email(String email);
}