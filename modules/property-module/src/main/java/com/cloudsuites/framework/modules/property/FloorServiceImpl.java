package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.modules.property.repository.FloorRepository;
import com.cloudsuites.framework.services.entities.property.Floor;
import com.cloudsuites.framework.services.entities.property.Unit;
import com.cloudsuites.framework.services.property.FloorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class FloorServiceImpl implements FloorService {

    private final FloorRepository floorRepository;

    @Autowired
    public FloorServiceImpl(FloorRepository floorRepository) {
        this.floorRepository = floorRepository;
    }

    @Override
    public Floor getFloorById(Long buildingId, Long floorId) throws NotFoundResponseException {
        return floorRepository.findByBuilding_BuildingIdAndFloorId(buildingId,floorId).orElseThrow(() -> new NotFoundResponseException("Floor not found: "+floorId));
    }

    @Override
    public List<Floor> getAllFloors(Long buildingId) {
        return floorRepository.findAll();
    }

    @Override
    public List<Floor> getAllFloorsWithUnits(Long buildingId) {
        return floorRepository.findAll()
                .stream().peek(floor -> {
                    // Force fetching of units
                    List<Unit> units = floor.getUnits();
                    // Now 'units' should be populated with the actual data.
                }).toList();
    }

    @Override
    public Floor saveFloor(Long buildingId, Floor floor) {
        return floorRepository.save(floor);
    }

    @Override
    public void deleteFloorById(Long buildingId, Long floorId) {
        floorRepository.deleteById(floorId);
    }

    @Override
    public Floor getFloorByIdWithUnits(Long buildingId, Long floorId) {
        return null;
    }

}

