package com.cloudsuites.framework.webapp.rest.property.mapper;

import com.cloudsuites.framework.services.entities.property.Floor;
import com.cloudsuites.framework.webapp.rest.property.dto.FloorDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FloorMapper {
    private final ModelMapper modelMapper;

    public FloorMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public FloorDTO convertToDTO(Floor floor) {
        return modelMapper.map(floor, FloorDTO.class);
    }

    public Floor convertToEntity(FloorDTO floorDTO) {
        return modelMapper.map(floorDTO, Floor.class);
    }

    public List<FloorDTO> convertToDTOList(List<Floor> floors) {
        return floors.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
