package com.cloudsuites.framework.services.property.personas.service;

import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TenantService {

    @Transactional(readOnly = true)
    Tenant getTenantById(String tenantId) throws NotFoundResponseException;

    @Transactional
    Tenant updateTenant(String tenantId, Tenant tenant) throws NotFoundResponseException;

    List<Tenant> getAllTenants(TenantStatus status) throws NotFoundResponseException;

    @Transactional(readOnly = true)
    List<Tenant> getAllTenantsByBuildingAndUnit(String buildingId, String unitId, TenantStatus status) throws NotFoundResponseException;

    @Transactional(readOnly = true)
    List<Tenant> getAllTenantsByBuilding(String buildingId, TenantStatus status) throws NotFoundResponseException;

    @Transactional(readOnly = true)
    Tenant getTenantByBuildingIdAndUnitIdAndTenantId(String buildingId, String unitId, String tenantId) throws NotFoundResponseException;

    @Transactional
    Tenant createTenant(Tenant tenant, String unitId) throws NotFoundResponseException;

    @Transactional(readOnly = true)
    Tenant findByUserId(String userId) throws NotFoundResponseException;

    @Transactional
    void deleteByTenantId(String tenantId);

    void transferTenant(Tenant tenant, Unit newUnit, Unit oldUnit) throws InvalidOperationException;
}
