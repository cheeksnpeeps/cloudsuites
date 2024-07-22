package com.cloudsuites.framework.modules.property.features.module;

import com.cloudsuites.framework.modules.property.features.repository.FloorRepository;
import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Floor;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.BuildingService;
import com.cloudsuites.framework.services.property.features.service.FloorService;
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
    private final BuildingService buildingService;

    @Autowired
    public FloorServiceImpl(FloorRepository floorRepository, UnitRepository unitRepository, BuildingService buildingService) {
        this.floorRepository = floorRepository;
        this.unitRepository = unitRepository;
        this.buildingService = buildingService;
    }

    @Override
    public Floor getFloorById(String buildingId, String floorId) throws NotFoundResponseException {
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
    public void deleteFloorById(String buildingId, String floorId) {
        logger.debug("Entering deleteFloorById with buildingId: {} and floorId: {}", buildingId, floorId);
        floorRepository.deleteById(floorId);
        logger.debug("Floor deleted: {}", floorId);
    }

    @Transactional
    @Override
    public Floor saveFloorAndUnits(String buildingId, Floor floor) throws NotFoundResponseException {

        Building building = buildingService.getBuildingById(buildingId);
        floor.setBuilding(building);
        // Save the floor first
        floor = floorRepository.save(floor);

        // Defensive copy of units
        List<Unit> units = new ArrayList<>(floor.getUnits());

        // Set floor reference for each unit
        Floor finalFloor = floor;

        units.forEach(unit -> {
            unit.setFloor(finalFloor);
            unit.setBuilding(building);
        });

        // Save all units
        units = unitRepository.saveAll(units);

        // Set the saved units back to the floor
        floor.setUnits(units);

        // Save the floor again to update the units
        return floorRepository.save(floor);
    }

}

