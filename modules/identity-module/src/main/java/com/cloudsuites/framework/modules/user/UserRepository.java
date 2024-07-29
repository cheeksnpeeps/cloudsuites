package com.cloudsuites.framework.modules.user;

import com.cloudsuites.framework.services.user.entities.Identity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Identity, String> {
    Optional<Identity> findByPhoneNumber(String phoneNumber);

    Optional<Identity> findByEmail(String email);

    boolean existsByUsername(String username);
}

