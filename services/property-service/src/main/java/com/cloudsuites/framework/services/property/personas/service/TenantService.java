package com.cloudsuites.framework.services.property.personas.service;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;

import java.util.List;

public interface TenantService {
    Tenant getTenantById(Long tenantId) throws NotFoundResponseException;
    Tenant updateTenant(Long tenantId, Tenant tenant) throws NotFoundResponseException;
    List<Tenant> getAllTenants();

    List<Tenant> getAllTenantsByBuildingAndUnit(String buildingId, Long unitId) throws NotFoundResponseException;

    List<Tenant> getAllTenantsByBuilding(String buildingId) throws NotFoundResponseException;

    Tenant getTenantByBuildingIdAndUnitIdAndTenantId(String buildingId, Long unitId, Long tenantId) throws NotFoundResponseException;

    Tenant createTenant(Tenant tenant, Long unitId) throws NotFoundResponseException;
    Tenant findByUserId(Long userId) throws NotFoundResponseException;
}
