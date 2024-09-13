package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomBookingCalendarRepositoryImpl implements CustomBookingCalendarRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomBookingCalendarRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public int countBookingsForUser(String userId, String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Counting bookings for userId: {}, amenityId: {}, between startTime: {} and endTime: {}", userId, amenityId, startTime, endTime);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        Predicate[] predicates = new Predicate[4];
        predicates[0] = cb.equal(booking.get("amenity").get("amenityId"), amenityId);
        predicates[1] = cb.equal(booking.get("userId"), userId);
        predicates[2] = cb.lessThan(booking.get("endTime"), endTime);
        predicates[3] = cb.greaterThan(booking.get("startTime"), startTime);

        cq.select(cb.count(booking)).where(predicates);

        int result = entityManager.createQuery(cq).getSingleResult().intValue();
        logger.debug("Counted {} bookings for userId: {}, amenityId: {}", result, userId, amenityId);

        return result;
    }

    @Override
    public List<AmenityBooking> findOverlappingBookings(String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Finding overlapping bookings for amenityId: {}, between startTime: {} and endTime: {}", amenityId, startTime, endTime);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AmenityBooking> cq = cb.createQuery(AmenityBooking.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(booking.get("amenity").get("amenityId"), amenityId);
        predicates[1] = cb.lessThan(booking.get("endTime"), endTime);
        predicates[2] = cb.greaterThan(booking.get("startTime"), startTime);

        cq.select(booking).where(predicates);

        List<AmenityBooking> result = entityManager.createQuery(cq).getResultList();
        logger.debug("Found {} overlapping bookings for amenityId: {}", result.size(), amenityId);

        return result;
    }

    @Override
    public int deleteByEndTimeBefore(LocalDateTime cutoffDate) {
        logger.debug("Deleting bookings with endTime before {}", cutoffDate);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<AmenityBooking> delete = cb.createCriteriaDelete(AmenityBooking.class);
        Root<AmenityBooking> booking = delete.from(AmenityBooking.class);

        delete.where(cb.lessThan(booking.get("endTime"), cutoffDate));

        int rowsDeleted = entityManager.createQuery(delete).executeUpdate();
        logger.debug("Deleted {} bookings with endTime before {}", rowsDeleted, cutoffDate);

        return rowsDeleted;
    }

    @Override
    public List<AmenityBooking> findByUserIdAndFilters(List<String> userIds, List<String> amenityIds, List<BookingStatus> bookingStatuses, LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Finding bookings for userIds: {}, amenityIds: {}, bookingStatuses: {}, between startDate: {} and endDate: {}", userIds, amenityIds, bookingStatuses, startDate, endDate);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AmenityBooking> cq = cb.createQuery(AmenityBooking.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        List<Predicate> predicates = new ArrayList<>();

        if (userIds != null) {
            predicates.add(booking.get("userId").in(userIds));
        }
        if (amenityIds != null) {
            predicates.add(booking.get("amenity").get("amenityId").in(amenityIds));
        }
        if (bookingStatuses != null) {
            predicates.add(booking.get("status").in(bookingStatuses));
        }
        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(booking.get("startTime"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(booking.get("endTime"), endDate));
        }

        if (!predicates.isEmpty()) {
            cq.select(booking).where(cb.and(predicates.toArray(new Predicate[0])));
        } else {
            cq.select(booking);
        }

        List<AmenityBooking> result = entityManager.createQuery(cq).getResultList();
        logger.debug("Found {} bookings matching filters", result.size());

        return result;
    }

    @Override
    public List<AmenityBooking> findByAmenityIdAndTimeRange(String amenityId, LocalDateTime start, LocalDateTime end) {
        logger.debug("Finding bookings for amenityId: {}, between startTime: {} and endTime: {}", amenityId, start, end);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AmenityBooking> cq = cb.createQuery(AmenityBooking.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(booking.get("amenity").get("amenityId"), amenityId);
        predicates[1] = cb.greaterThanOrEqualTo(booking.get("startTime"), start);
        predicates[2] = cb.lessThanOrEqualTo(booking.get("endTime"), end);

        cq.select(booking).where(predicates);

        List<AmenityBooking> result = entityManager.createQuery(cq).getResultList();
        logger.debug("Found {} bookings for amenityId: {} in the time range", result.size(), amenityId);

        return result;
    }

    @Override
    public List<AmenityBooking> findByAmenity_AmenityId(String amenityId) {
        logger.debug("Finding bookings for amenityId: {}", amenityId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AmenityBooking> cq = cb.createQuery(AmenityBooking.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        cq.select(booking).where(cb.equal(booking.get("amenity").get("amenityId"), amenityId));

        List<AmenityBooking> result = entityManager.createQuery(cq).getResultList();
        logger.debug("Found {} bookings for amenityId: {}", result.size(), amenityId);

        return result;
    }

    @Override
    public int countOccupancyDuringTimeRange(String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Counting occupancy for amenityId: {}, between startTime: {} and endTime: {}", amenityId, startTime, endTime);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(booking.get("amenity").get("amenityId"), amenityId);
        predicates[1] = cb.lessThan(booking.get("endTime"), endTime);
        predicates[2] = cb.greaterThan(booking.get("startTime"), startTime);

        cq.select(cb.count(booking)).where(predicates);

        int result = entityManager.createQuery(cq).getSingleResult().intValue();
        logger.debug("Counted {} bookings during the time range for amenityId: {}", result, amenityId);

        return result;
    }

    @Override
    public List<AmenityBooking> findByAmenity_AmenityIdAndBookingId(String amenityId, String bookingId) {
        logger.debug("Finding bookings for amenityId: {} and bookingId: {}", amenityId, bookingId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AmenityBooking> cq = cb.createQuery(AmenityBooking.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(booking.get("amenity").get("amenityId"), amenityId);
        predicates[1] = cb.equal(booking.get("bookingId"), bookingId);

        cq.select(booking).where(predicates);

        List<AmenityBooking> result = entityManager.createQuery(cq).getResultList();
        logger.debug("Found {} bookings for amenityId: {} and bookingId: {}", result.size(), amenityId, bookingId);

        return result;
    }

    @Override
    public int countOverlappingBookings(String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Counting overlapping bookings for amenityId: {}, between startTime: {} and endTime: {}", amenityId, startTime, endTime);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(booking.get("amenity").get("amenityId"), amenityId);
        predicates[1] = cb.lessThan(booking.get("endTime"), endTime);
        predicates[2] = cb.greaterThan(booking.get("startTime"), startTime);

        cq.select(cb.count(booking)).where(predicates);

        int result = entityManager.createQuery(cq).getSingleResult().intValue();
        logger.debug("Counted {} overlapping bookings for amenityId: {}", result, amenityId);

        return result;
    }
}
