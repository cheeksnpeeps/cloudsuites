package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.DailyAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityRepository extends JpaRepository<DailyAvailability, String> {

}
