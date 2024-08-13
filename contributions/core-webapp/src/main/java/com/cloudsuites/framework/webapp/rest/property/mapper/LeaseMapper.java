package com.cloudsuites.framework.webapp.rest.property.mapper;

import com.cloudsuites.framework.services.property.features.entities.Lease;
import com.cloudsuites.framework.webapp.rest.property.dto.LeaseDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeaseMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public LeaseMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public LeaseDto convertToDTO(Lease lease) {
        return modelMapper.map(lease, LeaseDto.class);
    }

    public Lease convertToEntity(LeaseDto leaseDto) {
        return modelMapper.map(leaseDto, Lease.class);
    }

    public List<LeaseDto> convertToDtoList(List<Lease> leases) {
        return (leases != null && !leases.isEmpty()) ? leases.stream()
                .map(this::convertToDTO)
                .toList() : null;
    }

    public List<Lease> convertToEntityList(List<LeaseDto> leaseDtos) {
        return (leaseDtos != null && !leaseDtos.isEmpty()) ? leaseDtos.stream()
                .map(this::convertToEntity)
                .toList() : null;
    }
}

