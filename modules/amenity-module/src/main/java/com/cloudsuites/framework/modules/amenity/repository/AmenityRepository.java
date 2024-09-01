package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, String> {

}
