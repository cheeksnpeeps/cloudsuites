package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.common.entities.property.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    // Add custom query methods if needed
}
