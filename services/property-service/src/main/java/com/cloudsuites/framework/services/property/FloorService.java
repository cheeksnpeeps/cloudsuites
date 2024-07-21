package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.entities.Floor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FloorService {

    Floor getFloorById(Long buildingId, Long floorId) throws NotFoundResponseException;

    List<Floor> getAllFloors(Long buildingId);

    void deleteFloorById(Long buildingId, Long floorId);

    Floor saveFloorAndUnits(Long buildingId, Floor floor);
}

