package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.entities.Tenant;

import java.util.List;

public interface TenantService {
    public Tenant getTenantById(Long tenantId) throws NotFoundResponseException;
    public Tenant updateTenant(Long tenantId, Tenant tenant) throws NotFoundResponseException;
    public List<Tenant> getAllTenants();
    public List<Tenant> getAllTenantsByBuildingAndUnit(Long buildingId, Long unitId) throws NotFoundResponseException;
    public List<Tenant> getAllTenantsByBuilding(Long buildingId) throws NotFoundResponseException;
    public Tenant getTenantByBuildingIdAndUnitIdAndTenantId(Long buildingId, Long unitId, Long tenantId) throws NotFoundResponseException;
    public Tenant createTenant(Tenant tenant) throws NotFoundResponseException;
    public Tenant findByUserId(Long userId) throws NotFoundResponseException;
}
