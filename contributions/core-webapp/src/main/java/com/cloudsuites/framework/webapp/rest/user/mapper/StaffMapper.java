package com.cloudsuites.framework.webapp.rest.user.mapper;

import com.cloudsuites.framework.services.property.personas.entities.Staff;
import com.cloudsuites.framework.webapp.rest.user.dto.StaffDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StaffMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public StaffMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public StaffDto convertToDTO(Staff staff) {
        return modelMapper.map(staff, StaffDto.class);
    }

    public Staff convertToEntity(StaffDto staffDTO) {
        return modelMapper.map(staffDTO, Staff.class);
    }

    public List<StaffDto> convertToDTOList(List<Staff> staffs) {
        return (staffs != null && !staffs.isEmpty()) ? staffs.stream()
                .map(this::convertToDTO)
                .toList() : null;
    }
}
