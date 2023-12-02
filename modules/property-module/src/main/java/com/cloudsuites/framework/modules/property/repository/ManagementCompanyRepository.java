package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.entities.property.ManagementCompany;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagementCompanyRepository extends JpaRepository<ManagementCompany, Long> {
    // Add custom query methods if needed
    @EntityGraph(attributePaths = "buildings")
    public Optional<ManagementCompany> findById(Long id);
}

