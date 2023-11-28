package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.common.entities.property.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    // Add custom query methods if needed
}