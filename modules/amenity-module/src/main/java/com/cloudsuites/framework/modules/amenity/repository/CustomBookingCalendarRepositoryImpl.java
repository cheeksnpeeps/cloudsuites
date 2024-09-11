package com.cloudsuites.framework.modules.amenity.repository;

import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomBookingCalendarRepositoryImpl implements CustomBookingCalendarRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public int countBookingsForUser(String userId, String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        Predicate[] predicates = new Predicate[4];
        predicates[0] = cb.equal(booking.get("amenity").get("amenityId"), amenityId);
        predicates[1] = cb.equal(booking.get("userId"), userId);
        predicates[2] = cb.lessThan(booking.get("endTime"), endTime);
        predicates[3] = cb.greaterThan(booking.get("startTime"), startTime);

        cq.select(cb.count(booking)).where(predicates);

        return entityManager.createQuery(cq).getSingleResult().intValue();
    }

    @Override
    public List<AmenityBooking> findOverlappingBookings(String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AmenityBooking> cq = cb.createQuery(AmenityBooking.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(booking.get("amenity").get("amenityId"), amenityId);
        predicates[1] = cb.lessThan(booking.get("endTime"), endTime);
        predicates[2] = cb.greaterThan(booking.get("startTime"), startTime);

        cq.select(booking).where(predicates);

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public int deleteByEndTimeBefore(LocalDateTime cutoffDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<AmenityBooking> delete = cb.createCriteriaDelete(AmenityBooking.class);
        Root<AmenityBooking> booking = delete.from(AmenityBooking.class);

        delete.where(cb.lessThan(booking.get("endTime"), cutoffDate));

        return entityManager.createQuery(delete).executeUpdate();
    }

    @Override
    public List<AmenityBooking> findByUserIdAndFilters(String userId, String amenityId, AmenityType type, LocalDateTime startDate, LocalDateTime endDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AmenityBooking> cq = cb.createQuery(AmenityBooking.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        List<Predicate> predicates = new ArrayList<>();

        if (userId != null) {
            predicates.add(cb.equal(booking.get("userId"), userId));
        }
        if (amenityId != null) {
            predicates.add(cb.equal(booking.get("amenity").get("amenityId"), amenityId));
        }
        if (type != null) {
            predicates.add(cb.equal(booking.get("amenity").get("type"), type));
        }
        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(booking.get("startTime"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(booking.get("endTime"), endDate));
        }

        // Apply predicates only if they exist
        if (!predicates.isEmpty()) {
            cq.select(booking).where(cb.and(predicates.toArray(new Predicate[0])));
        } else {
            cq.select(booking);
        }

        return entityManager.createQuery(cq).getResultList();
    }


    @Override
    public List<AmenityBooking> findByAmenityIdAndTimeRange(String amenityId, LocalDateTime start, LocalDateTime end) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AmenityBooking> cq = cb.createQuery(AmenityBooking.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(booking.get("amenity").get("amenityId"), amenityId);
        predicates[1] = cb.greaterThanOrEqualTo(booking.get("startTime"), start);
        predicates[2] = cb.lessThanOrEqualTo(booking.get("endTime"), end);

        cq.select(booking).where(predicates);

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<AmenityBooking> findByAmenity_AmenityId(String amenityId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AmenityBooking> cq = cb.createQuery(AmenityBooking.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        cq.select(booking).where(cb.equal(booking.get("amenity").get("amenityId"), amenityId));

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public int countOccupancyDuringTimeRange(String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(booking.get("amenity").get("amenityId"), amenityId);
        predicates[1] = cb.lessThan(booking.get("endTime"), endTime);
        predicates[2] = cb.greaterThan(booking.get("startTime"), startTime);

        cq.select(cb.count(booking)).where(predicates);

        return entityManager.createQuery(cq).getSingleResult().intValue();
    }

    @Override
    public List<AmenityBooking> findByAmenity_AmenityIdAndBookingId(String amenityId, String bookingId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AmenityBooking> cq = cb.createQuery(AmenityBooking.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(booking.get("amenity").get("amenityId"), amenityId);
        predicates[1] = cb.equal(booking.get("bookingId"), bookingId);

        cq.select(booking).where(predicates);

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public int countOverlappingBookings(String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AmenityBooking> booking = cq.from(AmenityBooking.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(booking.get("amenity").get("amenityId"), amenityId);
        predicates[1] = cb.lessThan(booking.get("endTime"), endTime);
        predicates[2] = cb.greaterThan(booking.get("startTime"), startTime);

        cq.select(cb.count(booking)).where(predicates);

        return entityManager.createQuery(cq).getSingleResult().intValue();
    }
}
