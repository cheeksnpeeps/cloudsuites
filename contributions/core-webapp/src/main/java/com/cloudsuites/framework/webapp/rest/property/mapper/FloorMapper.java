package com.cloudsuites.framework.webapp.rest.property.mapper;

import com.cloudsuites.framework.services.property.features.entities.Floor;
import com.cloudsuites.framework.webapp.rest.property.dto.FloorDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FloorMapper {
    private final ModelMapper modelMapper;

    public FloorMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public FloorDto convertToDTO(Floor floor) {
        return modelMapper.map(floor, FloorDto.class);
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
