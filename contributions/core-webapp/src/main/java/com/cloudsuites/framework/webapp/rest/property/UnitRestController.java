package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.property.UnitService;
import com.cloudsuites.framework.webapp.rest.property.dto.UnitDTO;
import com.cloudsuites.framework.webapp.rest.property.mapper.UnitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings/{buildingId}")
public class UnitRestController {

    // create all crud endpoints for units here

    public final UnitService unitService;
    public final UnitMapper unitMapper;
    @Autowired
    public UnitRestController(UnitService unitService, UnitMapper unitMapper) {
        this.unitService = unitService;
        this.unitMapper = unitMapper;
    }

    @GetMapping("/floors/{floorId}/units")
    public List<UnitDTO> getAllUnitsByFloor(@PathVariable Long buildingId, @PathVariable Long floorId) {
        return unitMapper.convertToDTOList(unitService.getAllUnits(buildingId));
    }

    @PostMapping("/floors/{floorId}/units")
    public UnitDTO saveUnit(@PathVariable Long buildingId,@PathVariable Long floorId, @RequestBody UnitDTO unitDTO) {
        return unitMapper.convertToDTO(unitService.saveUnit(buildingId, floorId, unitMapper.convertToEntity(unitDTO)));
    }

    @DeleteMapping("/units/{unitId}")
    public void deleteUnitById(@PathVariable Long buildingId, @PathVariable Long unitId) {
        unitService.deleteUnitById(buildingId, unitId);
    }

    @GetMapping("/units/{unitId}")
    public UnitDTO getUnitById(@PathVariable Long buildingId, @PathVariable Long unitId) {
        return unitMapper.convertToDTO(unitService.getUnitById(buildingId, unitId));
    }

    @GetMapping("/units")
    public List<UnitDTO> getAllUnits(@PathVariable Long buildingId, @RequestParam(required = false) Integer floorNumber) {
        if(floorNumber == null) {
            return unitMapper.convertToDTOList(unitService.getAllUnits(buildingId));
        }
        return unitMapper.convertToDTOList(unitService.getAllUnitsByFloorNumber(buildingId, floorNumber));
    }


}
