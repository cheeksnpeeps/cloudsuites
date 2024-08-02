package com.cloudsuites.framework.webapp.rest.user.mapper;

import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TenantMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public TenantMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public TenantDto convertToDTO(Tenant tenant) {
        return modelMapper.map(tenant, TenantDto.class);
    }

    public Tenant convertToEntity(TenantDto tenantDTO) {
        return modelMapper.map(tenantDTO, Tenant.class);
    }

    public List<TenantDto> convertToDTOList(List<Tenant> tenants) {
        return (tenants != null && !tenants.isEmpty()) ? tenants.stream()
                .map(this::convertToDTO)
                .toList() : List.of();
    }
}
