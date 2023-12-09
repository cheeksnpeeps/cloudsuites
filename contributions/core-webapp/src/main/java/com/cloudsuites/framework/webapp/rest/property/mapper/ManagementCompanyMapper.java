package com.cloudsuites.framework.webapp.rest.property.mapper;

import com.cloudsuites.framework.services.entities.property.ManagementCompany;
import com.cloudsuites.framework.webapp.rest.property.dto.ManagementCompanyDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ManagementCompanyMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public ManagementCompanyMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ManagementCompanyDto convertToDTO(ManagementCompany managementCompany) {
        return modelMapper.map(managementCompany, ManagementCompanyDto.class);
    }

    public ManagementCompany convertToEntity(ManagementCompanyDto managementCompanyDTO) {
        return modelMapper.map(managementCompanyDTO, ManagementCompany.class);
    }

    public List<ManagementCompanyDto> convertToDTOList(List<ManagementCompany> managementCompanies) {
        return (managementCompanies != null && !managementCompanies.isEmpty()) ? managementCompanies.stream()
                .map(this::convertToDTO)
                .toList(): null;
    }
}