package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.entities.property.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    public Optional<List<Unit>> findAllByFloor_FloorId(Long floorId);
}