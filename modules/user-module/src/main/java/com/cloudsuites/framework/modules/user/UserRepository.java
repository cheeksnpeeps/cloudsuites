package com.cloudsuites.framework.modules.user;

import com.cloudsuites.framework.services.common.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Add custom queries if needed
}

