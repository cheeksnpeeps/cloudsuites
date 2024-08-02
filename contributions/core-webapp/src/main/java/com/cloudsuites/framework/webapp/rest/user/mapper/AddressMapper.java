package com.cloudsuites.framework.webapp.rest.user.mapper;

import com.cloudsuites.framework.services.user.entities.Address;
import com.cloudsuites.framework.webapp.rest.user.dto.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public AddressMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public AddressDto convertToDTO(Address address) {
        return modelMapper.map(address, AddressDto.class);
    }

    public Address convertToEntity(AddressDto addressDTO) {
        return modelMapper.map(addressDTO, Address.class);
    }

    public List<AddressDto> convertToDTOList(List<Address> address) {
        return (address != null && !address.isEmpty()) ? address.stream()
                .map(this::convertToDTO)
                .toList() : List.of();
    }
}
