package com.cloudsuites.framework.webapp.rest.amenity.mapper;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AmenityMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public AmenityMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Amenity convertToEntity(AmenityDto amenityDto) {
        return (Amenity) modelMapper.map(amenityDto, AmenityTypeRegistry.getClassForType(amenityDto.getType()));
    }

    public List<AmenityDto> convertToDTOList(List<Amenity> amenities) {
        return amenities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AmenityDto convertToDTO(Amenity amenity) {
        return modelMapper.map(amenity, AmenityDto.class);
    }
}
