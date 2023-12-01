package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.entities.property.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findAllByFloor_FloorNumber(Integer floorNumber);
}