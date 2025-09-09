package com.cloudsuites.framework.webapp.rest.property.mapper;

import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.webapp.rest.property.dto.UnitDto;
import com.cloudsuites.framework.webapp.rest.user.dto.OwnerDto;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UnitMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public UnitMapper(ModelMapper modelMapper) {this.modelMapper = modelMapper;}

    public Unit convertToEntity(UnitDto unitDTO) {
        return modelMapper.map(unitDTO, Unit.class);
    }

    public List<UnitDto> convertToDTOList(List<Unit> units) {
        return (units != null && !units.isEmpty()) ? units.stream()
                .map(this::convertToDTO)
                .toList(): null;
    }

    public UnitDto convertToDTO(Unit unit) {
        if (unit == null) {
            return null;
        }
        
        UnitDto unitDto = new UnitDto();
        unitDto.setUnitId(unit.getUnitId());
        unitDto.setUnitNumber(unit.getUnitNumber());
        unitDto.setNumberOfBedrooms(unit.getNumberOfBedrooms());
        
        // Handle owner mapping without circular reference
        if (unit.getOwner() != null) {
            OwnerDto ownerDto = new OwnerDto();
            ownerDto.setOwnerId(unit.getOwner().getOwnerId());
            // Add other owner fields if needed - avoid unit reference to prevent circular dependency
            unitDto.setOwner(ownerDto);
        }
        
        // Handle tenants mapping without circular reference
        if (unit.getTenants() != null && !unit.getTenants().isEmpty()) {
            List<TenantDto> tenantDtos = unit.getTenants().stream()
                .map(tenant -> {
                    TenantDto tenantDto = new TenantDto();
                    tenantDto.setTenantId(tenant.getTenantId());
                    // Add other tenant fields if needed - avoid unit reference to prevent circular dependency
                    return tenantDto;
                })
                .collect(Collectors.toList());
            unitDto.setTenants(tenantDtos);
        }
        
        return unitDto;
    }

}
