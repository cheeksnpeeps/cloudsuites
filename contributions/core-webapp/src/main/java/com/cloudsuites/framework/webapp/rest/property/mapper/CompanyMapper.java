package com.cloudsuites.framework.webapp.rest.property.mapper;

import com.cloudsuites.framework.services.property.features.entities.Company;
import com.cloudsuites.framework.webapp.rest.property.dto.CompanyDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompanyMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public CompanyMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public CompanyDto convertToDTO(Company company) {
        return modelMapper.map(company, CompanyDto.class);
    }

    public Company convertToEntity(CompanyDto companyDTO) {
        return modelMapper.map(companyDTO, Company.class);
    }

    public List<CompanyDto> convertToDTOList(List<Company> managementCompanies) {
        return (managementCompanies != null && !managementCompanies.isEmpty()) ? managementCompanies.stream()
                .map(this::convertToDTO)
                .toList() : null;
    }
}