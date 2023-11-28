package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.common.entities.property.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {
    // Add custom query methods if needed
}
