package com.cloudsuites.framework.modules.property.features.repository;

import com.cloudsuites.framework.services.property.features.entities.Lease;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaseRepository extends JpaRepository<Lease, String> {

    Lease findByUnitIdAndOwnerId(String ownerId, String unitId);
}
