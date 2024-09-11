package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmenityBookingRepository extends JpaRepository<AmenityBooking, String> {

    Optional<AmenityBooking> findByBookingIdAndUserId(String bookingId, String tenantId);
}
