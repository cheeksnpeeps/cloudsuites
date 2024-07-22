package com.cloudsuites.framework.services.property.features.service;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Floor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FloorService {

    Floor getFloorById(String buildingId, String floorId) throws NotFoundResponseException;

    List<Floor> getAllFloors(String buildingId);

    void deleteFloorById(String buildingId, String floorId);

    Floor saveFloorAndUnits(String buildingId, Floor floor);
}

