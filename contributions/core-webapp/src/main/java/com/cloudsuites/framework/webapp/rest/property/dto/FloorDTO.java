package com.cloudsuites.framework.webapp.rest.property.dto;

import lombok.Data;

import java.util.List;

@Data
public class FloorDTO {

    private Long floorId;

    private BuildingDTO building;

    private List<UnitDTO> units;

    private Integer floorNumber;

}
