package com.cloudsuites.framework.webapp.rest.property.mapper;

import com.cloudsuites.framework.services.entities.property.ManagementCompany;
import com.cloudsuites.framework.webapp.rest.property.dto.ManagementCompanyDTO;
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

    public ManagementCompanyDTO convertToDTO(ManagementCompany managementCompany) {
        return modelMapper.map(managementCompany, ManagementCompanyDTO.class);
    }

    public ManagementCompany convertToEntity(ManagementCompanyDTO managementCompanyDTO) {
        return modelMapper.map(managementCompanyDTO, ManagementCompany.class);
    }

    public List<ManagementCompanyDTO> convertToDTOList(List<ManagementCompany> managementCompanies) {
        return managementCompanies.stream()
                .map(this::convertToDTO)
                .toList();
    }
}