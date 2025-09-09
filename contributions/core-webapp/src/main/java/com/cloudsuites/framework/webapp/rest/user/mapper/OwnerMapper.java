package com.cloudsuites.framework.webapp.rest.user.mapper;

import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.webapp.rest.property.dto.UnitDto;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import com.cloudsuites.framework.webapp.rest.user.dto.OwnerDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OwnerMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public OwnerMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public OwnerDto convertToDTO(Owner owner) {
        if (owner == null) {
            return null;
        }
        
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setOwnerId(owner.getOwnerId());
        ownerDto.setIsPrimaryTenant(owner.getIsPrimaryTenant());
        ownerDto.setStatus(owner.getStatus());
        ownerDto.setRole(owner.getRole());
        
        // Handle identity mapping
        if (owner.getIdentity() != null) {
            IdentityDto identityDto = modelMapper.map(owner.getIdentity(), IdentityDto.class);
            ownerDto.setIdentity(identityDto);
        }
        
        // Handle units mapping without circular reference  
        if (owner.getUnits() != null && !owner.getUnits().isEmpty()) {
            List<UnitDto> unitDtos = owner.getUnits().stream()
                .map(unit -> {
                    UnitDto unitDto = new UnitDto();
                    unitDto.setUnitId(unit.getUnitId());
                    unitDto.setUnitNumber(unit.getUnitNumber());
                    unitDto.setNumberOfBedrooms(unit.getNumberOfBedrooms());
                    // Exclude owner reference to prevent circular dependency
                    return unitDto;
                })
                .collect(Collectors.toList());
            ownerDto.setUnits(unitDtos);
        }
        
        return ownerDto;
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
