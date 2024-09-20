package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityBookingRepository extends JpaRepository<AmenityBooking, String> {

    AmenityBooking findByBookingIdAndUserId(String bookingId, String tenantId);
}
