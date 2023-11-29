package com.cloudsuites.framework.webapp.rest.property.mapper;

import com.cloudsuites.framework.services.entities.property.PropertyManagementCompany;
import com.cloudsuites.framework.webapp.rest.property.dto.PropertyManagementCompanyDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PropertyManagementCompanyMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public PropertyManagementCompanyMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public PropertyManagementCompanyDTO convertToDTO(PropertyManagementCompany company) {
        return modelMapper.map(company, PropertyManagementCompanyDTO.class);
    }

    public PropertyManagementCompany convertToEntity(PropertyManagementCompanyDTO companyDTO) {
        return modelMapper.map(companyDTO, PropertyManagementCompany.class);
    }

    public List<PropertyManagementCompanyDTO> convertToDTOList(List<PropertyManagementCompany> companies) {
        return companies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}