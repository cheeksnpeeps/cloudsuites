package com.cloudsuites.framework.webapp.rest.property.mapper;

import com.cloudsuites.framework.services.property.entities.Building;
import com.cloudsuites.framework.webapp.rest.property.dto.BuildingDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BuildingMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public BuildingMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Building convertToEntity(BuildingDto buildingDto) {
        return modelMapper.map(buildingDto, Building.class);
    }

    public List<BuildingDto> convertToDTOList(List<Building> buildings) {
        return buildings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BuildingDto convertToDTO(Building building) {
        return modelMapper.map(building, BuildingDto.class);
    }
}
