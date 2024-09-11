package com.cloudsuites.framework.modules.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBuildingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.modules.amenity.repository.AvailabilityRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.AmenityBuilding;
import com.cloudsuites.framework.services.amenity.entities.DailyAvailability;
import com.cloudsuites.framework.services.amenity.entities.MaintenanceStatus;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityAlreadyExistsException;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityNotFoundException;
import com.cloudsuites.framework.services.amenity.service.AmenityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AmenityServiceImpl implements AmenityService {

    private static final Logger logger = LoggerFactory.getLogger(AmenityServiceImpl.class);

    private final AmenityRepository amenityRepository;
    private final AmenityBuildingRepository amenityBuildingRepository;
    private final AvailabilityRepository availabilityRepository;

    public AmenityServiceImpl(AmenityRepository amenityRepository, AmenityBuildingRepository amenityBuildingRepository, AvailabilityRepository availabilityRepository) {
        this.amenityRepository = amenityRepository;
        this.amenityBuildingRepository = amenityBuildingRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    public Optional<Amenity> getAmenityById(String amenityId) {
        logger.debug("Fetching amenity with ID: {}", amenityId);
        return amenityRepository.findById(amenityId);
    }

    @Override
    public List<Amenity> getAllAmenities() {
        logger.debug("Fetching all amenities");
        return amenityRepository.findAll();
    }

    @Override
    public Amenity createAmenity(Amenity amenity, List<String> buildingIds) {
        logger.debug("Creating a new amenity: {}", amenity);

        // Check if amenity already exists
        if (amenityRepository.existsByName(amenity.getName())) {
            logger.warn("Amenity already exists with name: {}", amenity.getName());
            throw new AmenityAlreadyExistsException("Amenity already exists with name: " + amenity.getName());
        }

        List<DailyAvailability> dailyAvailabilities = amenity.getDailyAvailabilities();
        if (dailyAvailabilities != null) {
            dailyAvailabilities.forEach(dailyAvailability -> dailyAvailability.setAmenity(amenity));
        }

        Amenity savedAmenity = amenityRepository.save(amenity);
        logger.debug("Saved amenity with ID: {}", savedAmenity.getAmenityId());

        if (dailyAvailabilities != null) {
            logger.debug("Saving daily availabilities for amenity ID: {}", savedAmenity.getAmenityId());
            availabilityRepository.saveAll(dailyAvailabilities);
            logger.debug("Saved daily availabilities: {}", dailyAvailabilities);
        }

        if (buildingIds != null) {
            List<AmenityBuilding> amenityBuildings = buildingIds.stream()
                    .map(buildingId -> new AmenityBuilding(savedAmenity.getAmenityId(), buildingId))
                    .collect(Collectors.toList());
            logger.debug("Creating building associations for amenity ID: {}", savedAmenity.getAmenityId());
            logger.debug("Building associations: {}", amenityBuildings);

            amenityBuildingRepository.saveAll(amenityBuildings);
            logger.debug("Building associations created for amenity ID: {}", savedAmenity.getAmenityId());
        }

        return savedAmenity;
    }


    @Override
    public Amenity updateAmenity(Amenity amenity, List<String> buildingIds) {
        logger.debug("Updating amenity with ID: {}", amenity.getAmenityId());

        // Retrieve existing amenity
        Optional<Amenity> existingAmenityOpt = amenityRepository.findById(amenity.getAmenityId());
        if (existingAmenityOpt.isEmpty()) {
            logger.error("Amenity not found with ID: {}", amenity.getAmenityId());
            throw new AmenityNotFoundException("Amenity not found with ID: " + amenity.getAmenityId());
        }
        List<DailyAvailability> dailyAvailabilities = null;
        if (amenity.getDailyAvailabilities() != null) {
            dailyAvailabilities = amenity.getDailyAvailabilities();
            dailyAvailabilities.forEach(dailyAvailability -> dailyAvailability.setAmenity(amenity));
        }

        Amenity updatedAmenity = amenityRepository.save(amenity);
        logger.debug("Saved updated amenity with ID: {}", updatedAmenity.getAmenityId());

        if (dailyAvailabilities != null) {
            logger.debug("Saving daily availabilities for amenity ID: {}", updatedAmenity.getAmenityId());
            availabilityRepository.saveAll(dailyAvailabilities);
        }

        // Handle building associations
        if (buildingIds != null) {
            Set<String> currentBuildingIds = amenityBuildingRepository.findByAmenityId(amenity.getAmenityId())
                    .stream()
                    .map(AmenityBuilding::getBuildingId)
                    .collect(Collectors.toSet());

            Set<String> newBuildingIds = new HashSet<>(buildingIds);

            // Determine which associations to remove
            Set<String> toRemove = new HashSet<>(currentBuildingIds);
            toRemove.removeAll(newBuildingIds);
            if (!toRemove.isEmpty()) {
                logger.debug("Removing building associations for amenity ID: {} -> {}", updatedAmenity.getAmenityId(), toRemove);
                amenityBuildingRepository.deleteByAmenityIdAndBuildingIdIn(amenity.getAmenityId(), toRemove);
            }

            // Determine which associations to add
            newBuildingIds.removeAll(currentBuildingIds);
            List<AmenityBuilding> newAssociations = newBuildingIds.stream()
                    .map(buildingId -> new AmenityBuilding(updatedAmenity.getAmenityId(), buildingId))
                    .collect(Collectors.toList());
            if (!newAssociations.isEmpty()) {
                logger.debug("Adding building associations for amenity ID: {} -> {}", updatedAmenity.getAmenityId(), newBuildingIds);
                amenityBuildingRepository.saveAll(newAssociations);
            }
        }

        return updatedAmenity;
    }

    @Override
    public void deleteAmenity(String amenityId) {
        logger.debug("Deleting amenity with ID: {}", amenityId);
        Optional<Amenity> amenityOpt = amenityRepository.findById(amenityId);
        if (amenityOpt.isEmpty()) {
            throw new AmenityNotFoundException("Amenity not found with ID: " + amenityId);
        }

        amenityBuildingRepository.deleteByAmenityId(amenityId);
        logger.debug("Deleted building associations for amenity ID: {}", amenityId);

        amenityRepository.deleteById(amenityId);
        logger.debug("Deleted amenity with ID: {}", amenityId);
    }

    @Override
    public Amenity updateMaintenanceStatus(String amenityId, MaintenanceStatus status) {
        logger.debug("Updating maintenance status for amenity ID: {} to {}", amenityId, status);
        Optional<Amenity> amenityOpt = amenityRepository.findById(amenityId);
        if (amenityOpt.isEmpty()) {
            throw new AmenityNotFoundException("Amenity not found with ID: " + amenityId);
        }

        Amenity amenity = amenityOpt.get();
        amenity.setMaintenanceStatus(status);
        return amenityRepository.save(amenity);
    }

    @Override
    public List<Amenity> getAmenitiesByBuildingId(String buildingId) {
        logger.debug("Fetching amenities for building ID: {}", buildingId);

        // Use AmenityBuildingRepository to fetch associations
        List<AmenityBuilding> associations = amenityBuildingRepository.findByBuildingId(buildingId);

        // Extract amenity IDs and fetch Amenities
        List<String> amenityIds = associations.stream()
                .map(AmenityBuilding::getAmenityId)
                .collect(Collectors.toList());

        return amenityRepository.findAllById(amenityIds);
    }

    @Override
    public boolean isAmenityAssociatedWithBuilding(String amenityId, String buildingId) {
        logger.debug("Checking if amenity ID: {} is associated with building ID: {}", amenityId, buildingId);
        return amenityBuildingRepository.existsByAmenityIdAndBuildingId(amenityId, buildingId);
    }

    @Override
    public Amenity addBuildingToAmenity(String amenityId, String buildingId) {
        logger.debug("Adding building ID: {} to amenity ID: {}", buildingId, amenityId);
        Optional<Amenity> amenityOpt = amenityRepository.findById(amenityId);
        if (amenityOpt.isEmpty()) {
            throw new AmenityNotFoundException("Amenity not found with ID: " + amenityId);
        }

        AmenityBuilding amenityBuilding = new AmenityBuilding();
        amenityBuilding.setAmenityId(amenityId);
        amenityBuilding.setBuildingId(buildingId);
        amenityBuildingRepository.save(amenityBuilding);
        logger.debug("Added building association for amenity ID: {} with building ID: {}", amenityId, buildingId);

        return amenityOpt.get();
    }

    @Override
    public Amenity removeBuildingFromAmenity(String amenityId, String buildingId) {
        logger.debug("Removing building ID: {} from amenity ID: {}", buildingId, amenityId);
        Optional<Amenity> amenityOpt = amenityRepository.findById(amenityId);
        if (amenityOpt.isEmpty()) {
            throw new AmenityNotFoundException("Amenity not found with ID: " + amenityId);
        }

        List<AmenityBuilding> amenityBuildings = amenityBuildingRepository.findByAmenityIdAndBuildingId(amenityId, buildingId);
        if (!amenityBuildings.isEmpty()) {
            amenityBuildingRepository.deleteAll(amenityBuildings);
            logger.debug("Removed building association for amenity ID: {} with building ID: {}", amenityId, buildingId);
        }

        return amenityOpt.get();
    }
}
