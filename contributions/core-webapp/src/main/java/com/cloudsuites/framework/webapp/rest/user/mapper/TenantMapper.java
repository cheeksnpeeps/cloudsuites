package com.cloudsuites.framework.webapp.rest.user.mapper;

import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.webapp.rest.property.mapper.LeaseMapper;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TenantMapper {

    private final ModelMapper modelMapper;
    private final LeaseMapper leaseMapper;

    @Autowired
    public TenantMapper(ModelMapper modelMapper, LeaseMapper leaseMapper) {
        this.modelMapper = modelMapper;
        this.leaseMapper = leaseMapper;
    }

    public TenantDto convertToDTO(Tenant tenant) {
        if (tenant == null) {
            return null;
        }
        
        TenantDto dto = new TenantDto();
        
        // Map basic tenant fields
        dto.setTenantId(tenant.getTenantId());
        dto.setIsOwner(tenant.getIsOwner());
        dto.setIsPrimaryTenant(tenant.getIsPrimaryTenant());
        dto.setStatus(tenant.getStatus());
        dto.setRole(tenant.getRole());
        
        // Map lease safely without Hibernate proxy issues
        if (tenant.getLease() != null) {
            try {
                dto.setLease(leaseMapper.convertToDTO(tenant.getLease()));
            } catch (Exception e) {
                // If lease can't be loaded due to Hibernate proxy, set to null
                dto.setLease(null);
            }
        } else {
            dto.setLease(null);
        }
        
        // Map identity without circular references - manual mapping to avoid Hibernate proxy issues
        if (tenant.getIdentity() != null) {
            IdentityDto identityDto = new IdentityDto();
            identityDto.setUserId(tenant.getIdentity().getUserId());
            identityDto.setGender(tenant.getIdentity().getGender());
            identityDto.setFirstName(tenant.getIdentity().getFirstName());
            identityDto.setLastName(tenant.getIdentity().getLastName());
            identityDto.setPhoneNumber(tenant.getIdentity().getPhoneNumber());
            identityDto.setEmail(tenant.getIdentity().getEmail());
            // Explicitly skip createdBy, lastModifiedBy, createdAt, lastModifiedAt to avoid Hibernate proxy serialization
            dto.setIdentity(identityDto);
        }
        
        // Map building ID only (no full building object to avoid circular references)
        if (tenant.getBuilding() != null) {
            dto.setBuildingId(tenant.getBuilding().getBuildingId());
        }
        
        // Note: Unit and Building DTOs are set to null to avoid circular references
        // These can be populated separately if needed by the calling service
        dto.setUnit(null);
        dto.setBuilding(null);
        dto.setOwner(null);
        
        return dto;
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
