package com.cloudsuites.framework.modules.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.modules.amenity.repository.CustomBookingCalendarRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.DailyAvailability;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityNotFoundException;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingStatus;
import com.cloudsuites.framework.services.amenity.service.AmenityBookingCalendarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class AmenityBookingCalendarServiceImpl implements AmenityBookingCalendarService {

    private final AmenityRepository amenityRepository;
    private final CustomBookingCalendarRepository customBookingCalendarRepository;
    private static final Logger logger = LoggerFactory.getLogger(AmenityBookingCalendarServiceImpl.class);

    public AmenityBookingCalendarServiceImpl(AmenityRepository amenityRepository, CustomBookingCalendarRepository customBookingCalendarRepository) {
        this.amenityRepository = amenityRepository;
        this.customBookingCalendarRepository = customBookingCalendarRepository;
    }

    private Set<LocalDateTime> getLocalDateTimes(Amenity amenity, List<AmenityBooking> bookings) {
        Set<LocalDateTime> bookedSlots = new HashSet<>();
        logger.debug("Calculating booked slots from {} bookings.", bookings.size());

        for (AmenityBooking booking : bookings) {
            LocalDateTime slotTime = booking.getStartTime().truncatedTo(ChronoUnit.MINUTES);
            logger.debug("Processing booking from {} to {}.", booking.getStartTime(), booking.getEndTime());

            while (!slotTime.isAfter(booking.getEndTime().minusMinutes(1))) { // Exclude end time itself
                logger.debug("Adding booked slot: {}", slotTime);
                bookedSlots.add(slotTime);
                slotTime = slotTime.plusMinutes(amenity.getMinBookingDuration()); // Use minBookingDuration here
                logger.debug("Next slot time: {}", slotTime);
            }
        }
        logger.debug("Found {} booked slots.", bookedSlots.size());
        return bookedSlots;
    }

    @Override
    public Flux<AmenityBooking> getBookingsForUser(List<String> userIds, List<String> amenityIds, List<BookingStatus> bookingStatuses, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Fetching bookings for userId: {}, amenityIds: {}, startDate: {}, endDate: {}",
                userIds, amenityIds, startDate, endDate);
        return customBookingCalendarRepository.findByUserIdAndFilters(userIds, amenityIds, bookingStatuses, startDate, endDate)
                .doOnNext(booking -> logger.debug("Found booking: {}", booking));
    }

    @Override
    public Flux<AmenityBooking> getBookingsForAmenity(List<String> amenityIds, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Fetching bookings for amenityIds: {}, startDate: {}, endDate: {}",
                amenityIds, startDate, endDate);
        return customBookingCalendarRepository.findByUserIdAndFilters(null, amenityIds, null, startDate, endDate)
                .doOnNext(booking -> logger.debug("Found booking: {}", booking));
    }

    @Override
    public Flux<LocalDateTime> getAvailableSlotsForAmenity(String amenityId, LocalDateTime start, LocalDateTime end) {

        return Mono.fromCallable(() -> amenityRepository.findById(amenityId)
                        .orElseThrow(() -> {
                            logger.error("Amenity with id {} not found.", amenityId);
                            return new AmenityNotFoundException("Amenity not found");
                        }))
                .subscribeOn(Schedulers.boundedElastic()) // Ensure blocking call runs on I/O-bound thread pool
                .doOnNext(amenity -> logger.debug("Found amenity with ID: {}", amenityId))
                .flatMapMany(amenity ->
                        customBookingCalendarRepository.findByAmenityIdAndTimeRange(amenityId, start, end)
                                .collectList() // Collect bookings into a List
                                .flatMapMany(bookings -> { // flatMapMany expects a Publisher, return Flux from it
                                    List<LocalDateTime> availableSlots = calculateAvailableSlots(amenity, bookings, start, end);
                                    logger.debug("Found {} available slots for amenityId: {}", availableSlots.size(), amenityId);
                                    return Flux.fromIterable(availableSlots); // Return Flux<LocalDateTime>
                                })
                );
    }


    private List<LocalDateTime> calculateAvailableSlots(Amenity amenity, List<AmenityBooking> bookings, LocalDateTime start, LocalDateTime end) {
        List<LocalDateTime> availableSlots = new ArrayList<>();
        logger.debug("Calculating available slots based on {} bookings.", bookings.size());

        // Get daily availability for the given date
        DailyAvailability dailyAvailability = getDailyAvailability(amenity, start);
        logger.debug("Daily availability for {}: Open Time: {}, Close Time: {}", start.toLocalDate(), dailyAvailability.getOpenTime(), dailyAvailability.getCloseTime());

        // Create a set to hold all booked slots for quick lookup
        Set<LocalDateTime> bookedSlots = getLocalDateTimes(amenity, bookings);
        logger.debug("Total booked slots: {}", bookedSlots);

        // Generate all possible slots based on minBookingDuration
        LocalDateTime slotStartTime = start.withMinute(0).withSecond(0).withNano(0);
        logger.debug("Starting slot calculation from: {}", slotStartTime);

        while (!slotStartTime.isAfter(end.minusMinutes(amenity.getMinBookingDuration()))) {
            LocalTime slotTime = slotStartTime.toLocalTime();
            logger.debug("Evaluating slot: {}", slotStartTime);

            if (isWithinOpeningHours(amenity, slotTime, dailyAvailability)) {
                if (!bookedSlots.contains(slotStartTime)) {
                    logger.debug("Slot is available: {}", slotStartTime);
                    availableSlots.add(slotStartTime);
                } else {
                    logger.debug("Slot is booked: {}", slotStartTime);
                }
            } else {
                logger.debug("Slot is outside of opening hours: {}", slotStartTime);
            }

            slotStartTime = slotStartTime.plusMinutes(amenity.getMinBookingDuration()); // Use minBookingDuration here
        }

        logger.debug("Calculated {} available slots.", availableSlots.size());
        return availableSlots;
    }

    private DailyAvailability getDailyAvailability(Amenity amenity, LocalDateTime date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        Optional<DailyAvailability> availabilityOpt = amenity.getDailyAvailabilities().stream()
                .filter(avail -> avail.getDayOfWeek().equals(dayOfWeek))
                .findFirst();

        if (availabilityOpt.isEmpty()) {
            logger.error("Daily availability for {} not found.", dayOfWeek);
            throw new IllegalArgumentException("Daily availability not found for " + dayOfWeek);
        }

        return availabilityOpt.get();
    }

    private boolean isWithinOpeningHours(Amenity amenity, LocalTime slotTime, DailyAvailability dailyAvailability) {
        boolean withinHours = !slotTime.isBefore(dailyAvailability.getOpenTime()) &&
                !slotTime.isAfter(dailyAvailability.getCloseTime().minusMinutes(amenity.getMinBookingDuration()));
        logger.debug("Slot Time: {}, Open Time: {}, Close Time: {}, Within Opening Hours: {}",
                slotTime, dailyAvailability.getOpenTime(), dailyAvailability.getCloseTime(), withinHours);
        return withinHours;
    }
}
