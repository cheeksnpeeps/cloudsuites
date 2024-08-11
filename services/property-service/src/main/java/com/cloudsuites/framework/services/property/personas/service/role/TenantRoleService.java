package com.cloudsuites.framework.services.property.personas.service.role;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantRole;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface TenantRoleService {

    @Transactional
    Tenant getTenantRole(String tenantId) throws NotFoundResponseException;

    @Transactional
    Tenant updateTenantRole(String tenantId, TenantRole tenantRole) throws NotFoundResponseException;

    @Transactional
    void deleteTenantRole(String tenantId) throws NotFoundResponseException;

    @Transactional
    List<Tenant> getTenantsByRole(TenantRole tenantRole);

    @Transactional
    List<Tenant> getTenantsByRoleAndStatus(TenantRole tenantRole, TenantStatus status);

    @Transactional
    List<Tenant> getTenantsByRole();
}
