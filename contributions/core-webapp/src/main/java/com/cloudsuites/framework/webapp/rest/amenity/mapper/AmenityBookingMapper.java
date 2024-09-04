package com.cloudsuites.framework.webapp.rest.amenity.mapper;

import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityBookingDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AmenityBookingMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public AmenityBookingMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public AmenityBooking convertToEntity(AmenityBookingDto amenityBookingDto) {
        return modelMapper.map(amenityBookingDto, AmenityBooking.class);
    }

    public List<AmenityBookingDto> convertToDTOList(List<AmenityBooking> amenities) {
        return amenities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AmenityBookingDto convertToDTO(AmenityBooking amenityBooking) {
        return modelMapper.map(amenityBooking, AmenityBookingDto.class);
    }
}
