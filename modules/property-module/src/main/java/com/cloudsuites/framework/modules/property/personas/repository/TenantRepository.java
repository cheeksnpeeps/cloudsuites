package com.cloudsuites.framework.modules.property.personas.repository;

import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantRole;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {

    @Transactional(readOnly = true)
    Optional<List<Tenant>> findByBuilding_BuildingId(String buildingId);

    @Transactional(readOnly = true)
    Optional<List<Tenant>> findByBuilding_BuildingIdAndUnit_UnitId(String buildingId, String unitId);

    @Transactional(readOnly = true)
    Optional<Tenant> findByBuilding_BuildingIdAndUnit_UnitIdAndTenantId(String buildingId, String unitId, String tenantId);

    @Transactional(readOnly = true)
    Optional<Tenant> findByIdentity_UserId(String userId);

    @Transactional(readOnly = true)
    Optional<List<Tenant>> findByStatus(TenantStatus status);

    @Transactional(readOnly = true)
    Optional<List<Tenant>> findByBuilding_BuildingIdAndUnit_UnitIdAndStatus(String buildingId, String unitId, TenantStatus status);

    @Transactional(readOnly = true)
    Optional<List<Tenant>> findByBuilding_BuildingIdAndStatus(String buildingId, TenantStatus status);

    @Transactional(readOnly = true)
    List<Tenant> findByRole(TenantRole tenantRole);

    @Transactional(readOnly = true)
    List<Tenant> findByRoleAndStatus(TenantRole tenantRole, TenantStatus status);

    @Transactional(readOnly = true)
    List<Tenant> findAllByBuilding_BuildingIdAndIdentity_UserIdIn(String buildingId, List<String> tenantIds);

    @Transactional(readOnly = true)
    List<Tenant> findAllByBuilding_BuildingIdAndStatusAndIdentity_UserIdIn(String buildingId, TenantStatus status, List<String> tenantIds);
}
