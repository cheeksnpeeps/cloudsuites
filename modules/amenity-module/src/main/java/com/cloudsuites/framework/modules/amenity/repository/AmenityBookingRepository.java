package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityBookingRepository extends JpaRepository<AmenityBooking, String> {
}
