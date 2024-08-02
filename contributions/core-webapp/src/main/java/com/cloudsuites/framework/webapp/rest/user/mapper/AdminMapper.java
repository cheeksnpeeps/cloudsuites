package com.cloudsuites.framework.webapp.rest.user.mapper;

import com.cloudsuites.framework.services.user.entities.Admin;
import com.cloudsuites.framework.webapp.rest.user.dto.AdminDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public AdminMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public AdminDto convertToDTO(Admin admin) {
        return modelMapper.map(admin, AdminDto.class);
    }

    public Admin convertToEntity(AdminDto adminDTO) {
        return modelMapper.map(adminDTO, Admin.class);
    }

    public List<AdminDto> convertToDTOList(List<Admin> admins) {
        return (admins != null && !admins.isEmpty()) ? admins.stream()
                .map(this::convertToDTO)
                .toList() : List.of();
    }
}
