package com.cloudsuites.framework.webapp.rest.property.mapper;

import com.cloudsuites.framework.services.entities.property.Building;
import com.cloudsuites.framework.webapp.rest.property.dto.BuildingDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BuildingMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public BuildingMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public BuildingDTO convertToDTO(Building building) {
        return modelMapper.map(building, BuildingDTO.class);
    }

    public Building convertToEntity(BuildingDTO buildingDTO) {
        return modelMapper.map(buildingDTO, Building.class);
    }

    public List<BuildingDTO> convertToDTOList(List<Building> buildings) {
        return buildings.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
