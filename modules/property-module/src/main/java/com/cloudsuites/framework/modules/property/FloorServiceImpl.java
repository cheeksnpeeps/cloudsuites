package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.FloorRepository;
import com.cloudsuites.framework.modules.property.repository.UnitRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.FloorService;
import com.cloudsuites.framework.services.property.entities.Floor;
import com.cloudsuites.framework.services.property.entities.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class FloorServiceImpl implements FloorService {

    private final FloorRepository floorRepository;

    private static final Logger logger = LoggerFactory.getLogger(FloorServiceImpl.class);
    private final UnitRepository unitRepository;

    @Autowired
    public FloorServiceImpl(FloorRepository floorRepository, UnitRepository unitRepository) {
        this.floorRepository = floorRepository;
        this.unitRepository = unitRepository;
    }

    @Override
    public Floor getFloorById(String buildingId, Long floorId) throws NotFoundResponseException {
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
    public List<Floor> getAllFloors(String buildingId) {
        logger.debug("Entering getAllFloors with buildingId: {}", buildingId);
        List<Floor> floors = floorRepository.findAll();
        logger.debug("Floors found: {}", floors.size());
        return floors;
    }

    @Override
    public void deleteFloorById(String buildingId, Long floorId) {
        logger.debug("Entering deleteFloorById with buildingId: {} and floorId: {}", buildingId, floorId);
        floorRepository.deleteById(floorId);
        logger.debug("Floor deleted: {}", floorId);
    }

    @Transactional
    @Override
    public Floor saveFloorAndUnits(String buildingId, Floor floor) {
        // Save the floor first
        floor = floorRepository.save(floor);

        // Defensive copy of units
        List<Unit> units = new ArrayList<>(floor.getUnits());

        // Set floor reference for each unit
        Floor finalFloor = floor;

        units.forEach(unit -> unit.setFloor(finalFloor));

        // Save all units
        units = unitRepository.saveAll(units);

        // Set the saved units back to the floor
        floor.setUnits(units);

        // Save the floor again to update the units
        return floorRepository.save(floor);
    }

}

