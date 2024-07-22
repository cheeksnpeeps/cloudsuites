package com.cloudsuites.framework.modules.property.personas.repository;

import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {


    Optional<List<Tenant>> findByBuilding_BuildingId(String buildingId);

    Optional<List<Tenant>> findByBuilding_BuildingIdAndUnit_UnitId(String buildingId, Long unitId);

    Optional<Tenant> findByBuilding_BuildingIdAndUnit_UnitIdAndTenantId(String buildingId, Long unitId, Long tenantId);

    Optional<Tenant> findByIdentity_UserId(Long userId);
}
