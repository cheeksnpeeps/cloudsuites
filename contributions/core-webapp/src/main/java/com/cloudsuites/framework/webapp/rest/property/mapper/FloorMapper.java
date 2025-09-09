package com.cloudsuites.framework.webapp.rest.property.mapper;

import com.cloudsuites.framework.services.property.features.entities.Floor;
import com.cloudsuites.framework.webapp.rest.property.dto.FloorDto;
import com.cloudsuites.framework.webapp.rest.property.dto.UnitDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FloorMapper {
    private final ModelMapper modelMapper;

    public FloorMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Converts a Floor entity to FloorDto with manual mapping to prevent circular references.
     */
    public FloorDto convertToDTO(Floor floor) {
        FloorDto floorDto = new FloorDto();
        floorDto.setFloorId(floor.getFloorId());
        floorDto.setFloorName(floor.getFloorName());
        floorDto.setFloorNumber(floor.getFloorNumber());
        
        // Map units without floor reference to break circular dependency
        if (floor.getUnits() != null) {
            List<UnitDto> unitDtos = floor.getUnits().stream()
                .map(unit -> {
                    UnitDto unitDto = new UnitDto();
                    unitDto.setUnitId(unit.getUnitId());
                    unitDto.setUnitNumber(unit.getUnitNumber());
                    unitDto.setNumberOfBedrooms(unit.getNumberOfBedrooms());
                    // Don't set floor reference to prevent circular dependency
                    return unitDto;
                })
                .collect(Collectors.toList());
            floorDto.setUnits(unitDtos);
        }
        
        return floorDto;
    }

    public Floor convertToEntity(FloorDto floorDTO) {
        return modelMapper.map(floorDTO, Floor.class);
    }

    public List<FloorDto> convertToDTOList(List<Floor> floors) {
        return (floors != null && !floors.isEmpty()) ? floors.stream()
                .map(this::convertToDTO)
                .toList(): null;
    }
}
