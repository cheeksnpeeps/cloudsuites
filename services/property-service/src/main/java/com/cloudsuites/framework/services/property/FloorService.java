package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.entities.Floor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FloorService {

    Floor getFloorById(String buildingId, Long floorId) throws NotFoundResponseException;

    List<Floor> getAllFloors(String buildingId);

    void deleteFloorById(String buildingId, Long floorId);

    Floor saveFloorAndUnits(String buildingId, Floor floor);
}

