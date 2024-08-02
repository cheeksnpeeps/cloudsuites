package com.cloudsuites.framework.webapp.rest.user.mapper;

import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.webapp.rest.user.dto.OwnerDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OwnerMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public OwnerMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public OwnerDto convertToDTO(Owner owner) {
        return modelMapper.map(owner, OwnerDto.class);
    }

    public Owner convertToEntity(OwnerDto ownerDTO) {
        return modelMapper.map(ownerDTO, Owner.class);
    }

    public List<OwnerDto> convertToDTOList(List<Owner> owners) {
        return (owners != null && !owners.isEmpty()) ? owners.stream()
                .map(this::convertToDTO)
                .toList() : List.of();
    }
}
