package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.UnitService;
import com.cloudsuites.framework.webapp.rest.property.dto.UnitDto;
import com.cloudsuites.framework.webapp.rest.property.mapper.UnitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings/{buildingId}")
public class UnitRestController {

    public final UnitService unitService;
    public final UnitMapper unitMapper;
    @Autowired
    public UnitRestController(UnitService unitService, UnitMapper unitMapper) {
        this.unitService = unitService;
        this.unitMapper = unitMapper;
    }

    @GetMapping("/floors/{floorId}/units")
    public ResponseEntity<List<UnitDto>> getAllUnitsByFloor(@PathVariable Long buildingId, @PathVariable Long floorId) throws NotFoundResponseException {
        List<UnitDto> units = unitMapper.convertToDTOList(unitService.getAllUnitsByFloor(buildingId, floorId));
        return ResponseEntity.ok().body(units);
    }
    @PostMapping("/floors/{floorId}/units")
    public ResponseEntity<UnitDto> saveUnit(@PathVariable Long buildingId, @PathVariable Long floorId, @RequestBody UnitDto unitDTO) {
        UnitDto unit = unitMapper.convertToDTO(unitService.saveUnit(buildingId, floorId, unitMapper.convertToEntity(unitDTO)));
        return ResponseEntity.ok().body(unit);
    }

    @DeleteMapping("/units/{unitId}")
    public ResponseEntity<Void> deleteUnitById(@PathVariable Long buildingId, @PathVariable Long unitId) {
        unitService.deleteUnitById(buildingId, unitId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/units/{unitId}")
    public ResponseEntity<UnitDto> getUnitById(@PathVariable Long buildingId, @PathVariable Long unitId) throws NotFoundResponseException {
        return ResponseEntity.ok().body(unitMapper.convertToDTO(unitService.getUnitById(buildingId, unitId)));
    }

    @GetMapping("/units")
    public ResponseEntity<List<UnitDto>> getAllUnits(@PathVariable Long buildingId) {
        return ResponseEntity.ok().body(unitMapper.convertToDTOList(unitService.getAllUnits(buildingId)));
    }
}
