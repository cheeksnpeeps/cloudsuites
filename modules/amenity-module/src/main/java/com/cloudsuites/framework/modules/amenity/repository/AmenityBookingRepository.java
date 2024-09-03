package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AmenityBookingRepository extends JpaRepository<AmenityBooking, String> {

    @Transactional
    @Query("SELECT COUNT(ab) FROM AmenityBooking ab WHERE ab.amenity.amenityId = :amenityId AND ab.userId = :userId AND ab.startTime < :endTime AND ab.endTime > :startTime")
    int countBookingsForUser(@Param("userId") String userId, @Param("amenityId") String amenityId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Transactional
    @Query("SELECT ab FROM AmenityBooking ab WHERE ab.amenity.amenityId = :amenityId AND (ab.startTime < :endTime AND ab.endTime > :startTime)")
    List<AmenityBooking> findOverlappingBookings(@Param("amenityId") String amenityId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Transactional
    int deleteByEndTimeBefore(LocalDateTime cutoffDate);

    @Transactional
    @Query("SELECT b FROM AmenityBooking b WHERE b.userId = :userId AND (:type IS NULL OR b.amenity.type = :type)" +
            " AND (:startDate IS NULL OR b.startTime >= :startDate) AND (:endDate IS NULL OR b.endTime <= :endDate)")
    List<AmenityBooking> findByUserIdAndFilters(@Param("userId") String userId,
                                                @Param("type") AmenityType type,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b FROM AmenityBooking b WHERE b.amenity.amenityId = :amenityId AND b.startTime >= :start " +
            "AND b.endTime <= :end")
    List<AmenityBooking> findByAmenityIdAndTimeRange(@Param("amenityId") String amenityId,
                                                     @Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end);


    @Transactional
    List<AmenityBooking> findByAmenity_AmenityId(String amenityId);

    @Query("SELECT COUNT(b) FROM AmenityBooking b WHERE b.amenity.amenityId = :amenityId " +
            "AND b.startTime < :endTime AND b.endTime > :startTime")
    int countOccupancyDuringTimeRange(@Param("amenityId") String amenityId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    @Transactional
    List<AmenityBooking> findByAmenity_AmenityIdAndBookingId(String amenityId, String bookingId);
}

