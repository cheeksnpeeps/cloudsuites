package com.cloudsuites.framework.modules.user.repository;

import com.cloudsuites.framework.services.user.entities.Identity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Identity, String> {

    @Transactional(readOnly = true)
    Optional<Identity> findByPhoneNumber(String phoneNumber);

    @Transactional(readOnly = true)
    Optional<Identity> findByEmail(String email);

    @Transactional(readOnly = true)
    boolean existsByEmail(String email);

    @Transactional(readOnly = true)
    Optional<List<Identity>> findByFirstNameLikeOrLastNameLikeOrEmailLikeOrPhoneNumberLike(String query, String query1, String query2, String query3);
}

