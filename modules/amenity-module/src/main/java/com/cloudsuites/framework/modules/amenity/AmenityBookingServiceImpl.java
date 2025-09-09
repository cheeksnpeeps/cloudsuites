package com.cloudsuites.framework.modules.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.modules.amenity.repository.CustomBookingCalendarRepositoryImpl;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingException;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingStatus;
import com.cloudsuites.framework.services.amenity.service.AmenityBookingService;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AmenityBookingServiceImpl implements AmenityBookingService {

    private final AmenityRepository amenityRepository;
    private final AmenityBookingRepository bookingRepository;
    private final AmenityBookingValidator bookingValidator;
    private final CustomBookingCalendarRepositoryImpl customBookingCalendarRepository;

    private static final Logger logger = LoggerFactory.getLogger(AmenityBookingServiceImpl.class);

    public AmenityBookingServiceImpl(AmenityRepository amenityRepository,
                                     AmenityBookingRepository bookingRepository,
                                     AmenityBookingValidator bookingValidator,
                                     CustomBookingCalendarRepositoryImpl customBookingCalendarRepository) {
        this.amenityRepository = amenityRepository;
        this.bookingRepository = bookingRepository;
        this.bookingValidator = bookingValidator;
        this.customBookingCalendarRepository = customBookingCalendarRepository;
    }

    @Override
    public Mono<AmenityBooking> bookAmenity(Amenity amenity, String userId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Attempting to lock amenity with ID: {}", amenity.getAmenityId());
        return Mono.fromCallable(() -> amenityRepository.lockAmenityForBooking(amenity.getAmenityId()))
                .switchIfEmpty(Mono.error(new BookingException("Amenity not found for locking.")))
                .doOnNext(lockedAmenity -> logger.debug("Amenity with ID: {} locked for booking.", lockedAmenity.getAmenityId()))
                .flatMap(lockedAmenity -> {
                    bookingValidator.validateBookingConstraints(lockedAmenity, userId, startTime, endTime);
                    logger.debug("Booking constraints validated for amenity with ID: {}.", lockedAmenity.getAmenityId());

                    AmenityBooking booking = new AmenityBooking();
                    booking.setAmenity(lockedAmenity);
                    booking.setUserId(userId);
                    booking.setStartTime(startTime);
                    booking.setEndTime(endTime);

                    return Mono.fromCallable(() -> bookingRepository.save(booking)).subscribeOn(Schedulers.boundedElastic())
                            .doOnSuccess(savedBooking -> logger.debug("Booking with ID: {} saved successfully.", savedBooking.getBookingId()));

                });
    }

    @Override
    public Mono<Void> cancelBooking(String bookingId, String tenantId) {
        logger.debug("Attempting to cancel booking with ID: {} for tenant: {}", bookingId, tenantId);

        // Use Mono.fromCallable to execute blocking repository methods on a separate thread
        return Mono.fromCallable(() -> bookingRepository.findByBookingIdAndUserId(bookingId, tenantId))
                .subscribeOn(Schedulers.boundedElastic())  // Offload blocking operations to boundedElastic thread pool
                .switchIfEmpty(Mono.error(new NotFoundResponseException("Booking " + bookingId + " not found for tenant " + tenantId)))
                .flatMap(booking ->
                        Mono.fromRunnable(() -> bookingRepository.delete(booking))  // Run blocking delete operation
                                .subscribeOn(Schedulers.boundedElastic())  // Ensure delete is also on a separate thread
                                .then()  // Complete the Mono<Void> after the delete
                                .doOnSuccess(aVoid -> logger.debug("Booking with ID: {} cancelled successfully.", bookingId))
                );
    }



    @Override
    public Mono<Boolean> isAvailable(String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Checking availability for amenity with ID: {} from {} to {}", amenityId, startTime, endTime);
        return Mono.fromCallable(() -> amenityRepository.findById(amenityId))
                .doOnNext(amenity -> logger.debug("Found amenity with ID: {}", amenityId))
                .flatMap(amenity -> bookingValidator.isAvailable(amenity.get(), startTime, endTime).flatMap(available -> {
                            logger.debug("Amenity with ID: {} availability check result: {}", amenityId, available);
                            return Mono.just(available);
                        })
                );
    }

    @Override
    public Flux<AmenityBooking> getAllBookingsForAmenity(String amenityId) {
        logger.debug("Retrieving all bookings for amenity with ID: {}", amenityId);
        return customBookingCalendarRepository.findByAmenity_AmenityId(amenityId)
                .doOnNext(bookings -> logger.debug("Found {} bookings for amenity with ID: {}", bookings.getBookingId(), amenityId));
    }

    @Override
    public Mono<AmenityBooking> getAmenityBooking(String bookingId) {
        logger.debug("Retrieving booking with ID: {}", bookingId);
        return Mono.fromCallable(() -> bookingRepository.findById(bookingId))
                .flatMap(optionalBooking ->
                        optionalBooking.map(Mono::just)
                                .orElseGet(() -> Mono.error(new BookingException("Booking not found.")))
                )
                .doOnNext(booking -> logger.debug("Retrieved booking: {}", booking));
    }

    @Override
    @Transactional
    public Mono<AmenityBooking> updateBooking(AmenityBooking booking, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        logger.debug("Attempting to update booking with ID: {} to new times from {} to {}", booking.getBookingId(), newStartTime, newEndTime);
        return Mono.fromCallable(() -> amenityRepository.lockAmenityForBooking(booking.getAmenity().getAmenityId()))
                .switchIfEmpty(Mono.error(new BookingException("Amenity not found for locking.")))
                .doOnNext(lockedAmenity -> logger.debug("Amenity with ID: {} locked for booking update.", lockedAmenity.getAmenityId()))
                .flatMap(lockedAmenity ->
                        bookingValidator.validateBookingConstraints(lockedAmenity, booking.getUserId(), newStartTime, newEndTime)
                                .then(Mono.defer(() -> {
                                    booking.setStartTime(newStartTime);
                                    booking.setEndTime(newEndTime);
                                    return Mono.fromCallable(() -> bookingRepository.save(booking))
                                            .doOnSuccess(savedBooking -> logger.debug("Booking with ID: {} updated successfully.", savedBooking.getBookingId()));
                                }))
                );
    }
    @Override
    public Mono<AmenityBooking> updateBookingStatus(String bookingId, BookingStatus status) {
        logger.debug("Updating booking status with ID: {}", bookingId);

        return Mono.fromCallable(() -> bookingRepository.findById(bookingId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalBooking ->
                        optionalBooking.map(Mono::just)
                                .orElseGet(() -> Mono.error(new BookingException("Booking not found.")))
                )
                .flatMap(booking -> {
                    booking.setStatus(status);

                    return Mono.fromCallable(() -> bookingRepository.save(booking))
                            .doOnSuccess(savedBooking -> logger.debug("Booking with ID: {} updated successfully.", bookingId));
                });
    }

    @Override
    public AmenityBooking bookAmenitySync(Amenity amenity, String userId, LocalDateTime startTime, LocalDateTime endTime) {
        return bookAmenity(amenity, userId, startTime, endTime).block();
    }

    @Override
    public Void cancelBookingSync(String bookingId, String tenantId) {
        return cancelBooking(bookingId, tenantId).block();
    }

    @Override
    public Boolean isAvailableSync(String amenityId, LocalDateTime startTime, LocalDateTime endTime) {
        return isAvailable(amenityId, startTime, endTime).block();
    }

    @Override
    public List<AmenityBooking> getAllBookingsForAmenitySync(String amenityId) {
        logger.debug("Retrieving all bookings for amenity with ID: {}", amenityId);
        try {
            return getAllBookingsForAmenity(amenityId)
                    .collectList()
                    .block();
        } catch (Exception e) {
            logger.error("Error retrieving bookings for amenity {}: {}", amenityId, e.getMessage(), e);
            throw e; // or wrap in a domain exception
        }
    }

    @Override
    public AmenityBooking getAmenityBookingSync(String bookingId) {
        return getAmenityBooking(bookingId).block();
    }

    @Override
    public AmenityBooking updateBookingSync(AmenityBooking booking, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        return updateBooking(booking, newStartTime, newEndTime).block();
    }

    @Override
    public AmenityBooking updateBookingStatusSync(String bookingId, BookingStatus status) {
        return updateBookingStatus(bookingId, status).block();
    }

}
