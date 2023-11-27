package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.entities.property.Floor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface FloorService {

    public Floor getFloorById(Long floorId);

    public List<Floor> getAllFloors();

    public Floor saveFloor(Floor floor);

    public void deleteFloorById(Long floorId);
}

