package com.cloudsuites.framework.services.property.personas.service;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;

import java.util.List;

public interface TenantService {

    Tenant getTenantById(String tenantId) throws NotFoundResponseException;

    Tenant updateTenant(String tenantId, Tenant tenant) throws NotFoundResponseException;

    List<Tenant> getAllTenants();

    List<Tenant> getAllTenantsByBuildingAndUnit(String buildingId, String unitId) throws NotFoundResponseException;

    List<Tenant> getAllTenantsByBuilding(String buildingId) throws NotFoundResponseException;

    Tenant getTenantByBuildingIdAndUnitIdAndTenantId(String buildingId, String unitId, String tenantId) throws NotFoundResponseException;

    Tenant createTenant(Tenant tenant, String unitId) throws NotFoundResponseException;

    Tenant findByUserId(String userId) throws NotFoundResponseException;

    void deleteByTenantId(String tenantId);
}
