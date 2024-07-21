package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.entities.Tenant;

import java.util.List;

public interface TenantService {
    Tenant getTenantById(Long tenantId) throws NotFoundResponseException;
    Tenant updateTenant(Long tenantId, Tenant tenant) throws NotFoundResponseException;
    List<Tenant> getAllTenants();
    List<Tenant> getAllTenantsByBuildingAndUnit(Long buildingId, Long unitId) throws NotFoundResponseException;
    List<Tenant> getAllTenantsByBuilding(Long buildingId) throws NotFoundResponseException;
    Tenant getTenantByBuildingIdAndUnitIdAndTenantId(Long buildingId, Long unitId, Long tenantId) throws NotFoundResponseException;

    Tenant createTenant(Tenant tenant, Long unitId) throws NotFoundResponseException;
    Tenant findByUserId(Long userId) throws NotFoundResponseException;
}
