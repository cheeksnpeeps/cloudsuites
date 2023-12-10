package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.FloorRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.entities.property.Floor;
import com.cloudsuites.framework.services.property.FloorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FloorServiceImpl implements FloorService {

    private final FloorRepository floorRepository;

    private static final Logger logger = LoggerFactory.getLogger(FloorServiceImpl.class);

    @Autowired
    public FloorServiceImpl(FloorRepository floorRepository) {
        this.floorRepository = floorRepository;
    }

    @Override
    public Floor getFloorById(Long buildingId, Long floorId) throws NotFoundResponseException {
        logger.debug("Entering getFloorById with buildingId: {} and floorId: {}", buildingId, floorId);

        Floor floor = floorRepository.findByBuilding_BuildingIdAndFloorId(buildingId, floorId)
                .orElseThrow(() -> {
                    logger.error("Floor not found for buildingId: {} and floorId: {}", buildingId, floorId);
                    return new NotFoundResponseException("Floor not found: " + floorId);
                });

        logger.debug("Floor found: {}", floor.getFloorNumber());
        return floor;
    }

    @Override
    public List<Floor> getAllFloors(Long buildingId) {
        logger.debug("Entering getAllFloors with buildingId: {}", buildingId);
        List<Floor> floors = floorRepository.findAll();
        logger.debug("Floors found: {}", floors.size());
        return floors;
    }

    @Override
    public List<Floor> getAllFloorsWithUnits(Long buildingId) {
        return null;
    }

    @Override
    public Floor saveFloor(Long buildingId, Floor floor) {
        logger.debug("Entering saveFloor with floor: {}", floor.getFloorNumber());
        Floor savedFloor = floorRepository.save(floor);
        logger.debug("Floor saved: {}", savedFloor.getFloorNumber());
        return savedFloor;
    }
    @Override
    public void deleteFloorById(Long buildingId, Long floorId) {
        logger.debug("Entering deleteFloorById with buildingId: {} and floorId: {}", buildingId, floorId);
        floorRepository.deleteById(floorId);
        logger.debug("Floor deleted: {}", floorId);
    }
}

