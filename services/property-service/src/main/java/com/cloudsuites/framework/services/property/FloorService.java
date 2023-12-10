package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.entities.property.Floor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FloorService {

    public Floor getFloorById(Long buildingId, Long floorId) throws NotFoundResponseException;

    public List<Floor> getAllFloors(Long buildingId);

    List<Floor> getAllFloorsWithUnits(Long buildingId);

    Floor saveFloor(Long buildingId, Floor floor);

    public void deleteFloorById(Long buildingId, Long floorId);
}

