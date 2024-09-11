package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, String> {

    @Transactional
    boolean existsByName(String name);

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Amenity a WHERE a.amenityId = :amenityId")
    Amenity lockAmenityForBooking(@Param("amenityId") String amenityId);
}
