package com.cloudsuites.framework.webapp.rest.property.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FloorDTO {

    private Long floorId;

    private BuildingDTO building;

    private List<UnitDTO> units;

    private Integer floorNumber;

}
