package com.cloudsuites.framework.services.property.features.service;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Floor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FloorService {

    Floor getFloorById(String buildingId, String floorId) throws NotFoundResponseException;

    void deleteFloorById(String buildingId, String floorId) throws NotFoundResponseException;

    Floor saveFloorAndUnits(String buildingId, Floor floor) throws NotFoundResponseException;

    List<Floor> getAllFloorsByBuildingId(String buildingId) throws NotFoundResponseException;
}

