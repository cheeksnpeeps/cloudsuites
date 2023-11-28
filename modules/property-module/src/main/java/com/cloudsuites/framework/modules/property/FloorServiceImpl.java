package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.FloorRepository;
import com.cloudsuites.framework.services.common.entities.property.Floor;
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
    public Floor getFloorById(Long floorId) {
        return floorRepository.findById(floorId).orElse(null);
    }

    @Override
    public List<Floor> getAllFloors() {
        return floorRepository.findAll();
    }

    @Override
    public Floor saveFloor(Floor floor) {
        return floorRepository.save(floor);
    }

    @Override
    public void deleteFloorById(Long floorId) {
        floorRepository.deleteById(floorId);
    }
}

