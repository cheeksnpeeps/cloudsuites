package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.property.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {


    public Optional<List<Tenant>> findByBuilding_BuildingId(Long buildingId);

    public Optional<List<Tenant>> findByBuilding_BuildingIdAndUnit_UnitId(Long buildingId, Long unitId);

    public Optional<Tenant> findByBuilding_BuildingIdAndUnit_UnitIdAndTenantId(Long buildingId, Long unitId, Long tenantId);

    public Optional<Tenant> findByIdentity_UserId(Long userId);
}
