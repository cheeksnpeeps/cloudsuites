package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AmenityBookingRepository extends JpaRepository<AmenityBooking, String> {

    @Query("SELECT COUNT(ab) FROM AmenityBooking ab WHERE ab.amenity.amenityId = :amenityId AND ab.userId = :userId AND ab.startTime < :endTime AND ab.endTime > :startTime")
    int countBookingsForUser(@Param("userId") String userId, @Param("amenityId") String amenityId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT ab FROM AmenityBooking ab WHERE ab.amenity.amenityId = :amenityId AND (ab.startTime < :endTime AND ab.endTime > :startTime)")
    List<AmenityBooking> findOverlappingBookings(@Param("amenityId") String amenityId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    int deleteByEndTimeBefore(LocalDateTime cutoffDate);
}

