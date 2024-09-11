package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomBookingCalendarRepository {

    @Transactional
    int countBookingsForUser(@Param("userId") String userId,
                             @Param("amenityId") String amenityId,
                             @Param("startTime") LocalDateTime startTime,
                             @Param("endTime") LocalDateTime endTime);

    @Transactional
    List<AmenityBooking> findOverlappingBookings(@Param("amenityId") String amenityId,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    @Transactional
    int deleteByEndTimeBefore(LocalDateTime cutoffDate);

    @Transactional
    List<AmenityBooking> findByUserIdAndFilters(@Param("userId") String userId,
                                                @Param("amenityId") String amenityId,
                                                @Param("type") AmenityType type,
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Param("startDate") LocalDateTime startDate,
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Param("endDate") LocalDateTime endDate);

    @Transactional
    List<AmenityBooking> findByAmenityIdAndTimeRange(@Param("amenityId") String amenityId,
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Param("start") LocalDateTime start,
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Param("end") LocalDateTime end);

    @Transactional
    List<AmenityBooking> findByAmenity_AmenityId(String amenityId);

    @Transactional
    int countOccupancyDuringTimeRange(@Param("amenityId") String amenityId,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Param("startTime") LocalDateTime startTime,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Param("endTime") LocalDateTime endTime);

    @Transactional
    List<AmenityBooking> findByAmenity_AmenityIdAndBookingId(String amenityId, String bookingId);

    @Transactional
    int countOverlappingBookings(@Param("amenityId") String amenityId,
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Param("startTime") LocalDateTime startTime,
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Param("endTime") LocalDateTime endTime);

}
