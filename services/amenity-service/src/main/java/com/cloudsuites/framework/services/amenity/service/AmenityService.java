package com.cloudsuites.framework.services.amenity.service;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.MaintenanceStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AmenityService {

    @Transactional(readOnly = true)
    Optional<Amenity> getAmenityById(String amenityId);

    @Transactional(readOnly = true)
    List<Amenity> getAllAmenities();

    @Transactional
    Amenity createAmenity(Amenity amenity, List<String> buildingIds);

    @Transactional
    Amenity updateAmenity(Amenity amenity, List<String> buildingIds);

    @Transactional
    void deleteAmenity(String amenityId);

    @Transactional
    Amenity updateMaintenanceStatus(String amenityId, MaintenanceStatus status);

    @Transactional(readOnly = true)
    List<Amenity> getAmenitiesByBuildingId(String buildingId);

    @Transactional(readOnly = true)
    boolean isAmenityAssociatedWithBuilding(String amenityId, String buildingId);

    @Transactional
    Amenity addBuildingToAmenity(String amenityId, String buildingId);

    @Transactional
    Amenity removeBuildingFromAmenity(String amenityId, String buildingId);
}

