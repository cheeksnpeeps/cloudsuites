package com.cloudsuites.framework.webapp.rest.user.mapper;

import com.cloudsuites.framework.services.property.personas.entities.Manager;
import com.cloudsuites.framework.webapp.rest.user.dto.ManagerDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ManagerMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public ManagerMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ManagerDto convertToDTO(Manager manager) {
        return modelMapper.map(manager, ManagerDto.class);
    }

    public Manager convertToEntity(ManagerDto managerDTO) {
        return modelMapper.map(managerDTO, Manager.class);
    }

    public List<ManagerDto> convertToDTOList(List<Manager> managers) {
        return (managers != null && !managers.isEmpty()) ? managers.stream()
                .map(this::convertToDTO)
                .toList() : null;
    }
}
