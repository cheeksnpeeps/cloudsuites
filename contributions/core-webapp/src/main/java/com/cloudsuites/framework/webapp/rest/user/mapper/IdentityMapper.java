package com.cloudsuites.framework.webapp.rest.user.mapper;

import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IdentityMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public IdentityMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public IdentityDto convertToDTO(Identity identity) {
        return modelMapper.map(identity, IdentityDto.class);
    }

    public Identity convertToEntity(IdentityDto identityDTO) {
        return modelMapper.map(identityDTO, Identity.class);
    }

    public List<IdentityDto> convertToDTOList(List<Identity> identities) {
        return (identities != null && !identities.isEmpty()) ? identities.stream()
                .map(this::convertToDTO)
                .toList() : List.of();
    }
}
