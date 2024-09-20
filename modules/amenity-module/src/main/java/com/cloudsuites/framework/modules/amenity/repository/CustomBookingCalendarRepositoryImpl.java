package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CustomBookingCalendarRepositoryImpl implements CustomBookingCalendarRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomBookingCalendarRepositoryImpl.class);

    private final R2dbcEntityTemplate template;

    public CustomBookingCalendarRepositoryImpl(R2dbcEntityTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<Long> countBookingsForUser(String userId, String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Counting bookings for userId: {}, amenityId: {}, between startTime: {} and endTime: {}", userId, amenityId, startTime, endTime);

        Criteria criteria = Criteria.where("amenity_id").is(amenityId)
                .and("user_id").is(userId)
                .and("end_time").lessThan(endTime)
                .and("start_time").greaterThan(startTime);

        return template.select(Query.query(criteria), AmenityBooking.class)
                .count()
                .doOnNext(result -> logger.debug("Counted {} bookings for userId: {}, amenityId: {}", result, userId, amenityId));
    }

    @Override
    public Flux<AmenityBooking> findOverlappingBookings(String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Finding overlapping bookings for amenityId: {}, between startTime: {} and endTime: {}", amenityId, startTime, endTime);

        Criteria criteria = Criteria.where("amenity_id").is(amenityId)
                .and("end_time").lessThan(endTime)
                .and("start_time").greaterThan(startTime);

        return template.select(Query.query(criteria), AmenityBooking.class)
                .doOnNext(booking -> logger.debug("Found overlapping booking: {}", booking));
    }

    @Override
    public Mono<Long> deleteByEndTimeBefore(LocalDateTime cutoffDate) {
        logger.debug("Deleting bookings with endTime before {}", cutoffDate);

        Criteria criteria = Criteria.where("end_time").lessThan(cutoffDate);

        return template.delete(Query.query(criteria), AmenityBooking.class)
                .doOnNext(deleted -> logger.debug("Deleted {} bookings with endTime before {}", deleted, cutoffDate));
    }

    @Override
    public Flux<AmenityBooking> findByUserIdAndFilters(List<String> userIds, List<String> amenityIds, List<BookingStatus> bookingStatuses, LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Finding bookings for userIds: {}, amenityIds: {}, bookingStatuses: {}, between startDate: {} and endDate: {}", userIds, amenityIds, bookingStatuses, startDate, endDate);

        Criteria criteria = Criteria.empty();

        if (userIds != null) {
            criteria = criteria.and("user_id").in(userIds);
        }
        if (amenityIds != null) {
            criteria = criteria.and("amenity_id").in(amenityIds);
        }
        if (bookingStatuses != null) {
            criteria = criteria.and("status").in(bookingStatuses);
        }
        if (startDate != null) {
            criteria = criteria.and("start_time").greaterThan(startDate).or("start_time").is(startDate);
        }
        if (endDate != null) {
            criteria = criteria.and("end_time").lessThan(endDate).or("end_time").is(endDate);
        }

        return template.select(Query.query(criteria), AmenityBooking.class)
                .doOnNext(booking -> logger.debug("Found booking: {}", booking));
    }

    @Override
    public Flux<AmenityBooking> findByAmenityIdAndTimeRange(String amenityId, LocalDateTime start, LocalDateTime end) {
        logger.debug("Finding bookings for amenityId: {}, between startTime: {} and endTime: {}", amenityId, start, end);

        Criteria criteria = Criteria.where("amenity_id").is(amenityId)
                .and("start_time").greaterThan(start).or("start_time").is(start)
                .and("end_time").lessThan(end).or("end_time").is(end);

        return template.select(Query.query(criteria), AmenityBooking.class)
                .doOnNext(booking -> logger.debug("Found booking: {}", booking));
    }

    @Override
    public Flux<AmenityBooking> findByAmenity_AmenityId(String amenityId) {
        logger.debug("Finding bookings for amenityId: {}", amenityId);

        Criteria criteria = Criteria.where("amenity_id").is(amenityId);

        return template.select(Query.query(criteria), AmenityBooking.class)
                .doOnNext(booking -> logger.debug("Found booking: {}", booking));
    }

    @Override
    public Mono<Long> countOccupancyDuringTimeRange(String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Counting occupancy for amenityId: {}, between startTime: {} and endTime: {}", amenityId, startTime, endTime);

        Criteria criteria = Criteria.where("amenity_id").is(amenityId)
                .and("end_time").lessThan(endTime)
                .and("start_time").greaterThan(startTime);

        return template.select(Query.query(criteria), AmenityBooking.class)
                .count()
                .doOnNext(count -> logger.debug("Counted {} bookings during the time range for amenityId: {}", count, amenityId));
    }

    @Override
    public Flux<AmenityBooking> findByAmenity_AmenityIdAndBookingId(String amenityId, String bookingId) {
        logger.debug("Finding bookings for amenityId: {} and bookingId: {}", amenityId, bookingId);

        Criteria criteria = Criteria.where("amenity_id").is(amenityId)
                .and("booking_id").is(bookingId);

        return template.select(Query.query(criteria), AmenityBooking.class)
                .doOnNext(booking -> logger.debug("Found booking: {}", booking));
    }

    @Override
    public Mono<Long> countOverlappingBookings(String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Counting overlapping bookings for amenityId: {}, between startTime: {} and endTime: {}", amenityId, startTime, endTime);

        Criteria criteria = Criteria.where("amenity_id").is(amenityId)
                .and("end_time").lessThan(endTime)
                .and("start_time").greaterThan(startTime);

        return template.select(Query.query(criteria), AmenityBooking.class)
                .count()
                .doOnNext(count -> logger.debug("Counted {} overlapping bookings for amenityId: {}", count, amenityId));
    }
}
