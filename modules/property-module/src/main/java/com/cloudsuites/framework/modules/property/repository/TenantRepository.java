package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.property.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {


    Optional<List<Tenant>> findByBuildingId(Long buildingId);

    Optional<List<Tenant>> findByBuildingIdAndUnit_UnitId(Long buildingId, Long unitId);

    Optional<Tenant> findByBuildingIdAndUnit_UnitIdAndTenantId(Long buildingId, Long unitId, Long tenantId);

    Optional<Tenant> findByIdentity_UserId(Long userId);
}
