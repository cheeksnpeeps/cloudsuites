package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.entities.property.Unit;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface UnitService {

    public Unit getUnitById(Long unitId);

    public List<Unit> getAllUnits();

    public Unit saveUnit(Unit unit);

    public void deleteUnitById(Long unitId);
}
