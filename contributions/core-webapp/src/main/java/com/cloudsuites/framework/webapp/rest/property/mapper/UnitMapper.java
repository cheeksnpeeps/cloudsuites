package com.cloudsuites.framework.webapp.rest.property.mapper;

import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.webapp.rest.property.dto.UnitDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
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
        return modelMapper.map(unit, UnitDto.class);
    }

}
