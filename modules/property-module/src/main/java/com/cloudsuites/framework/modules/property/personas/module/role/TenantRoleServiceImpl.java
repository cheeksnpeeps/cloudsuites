package com.cloudsuites.framework.modules.property.personas.module.role;

import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantRole;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.services.property.personas.service.role.TenantRoleService;
import com.cloudsuites.framework.services.user.entities.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TenantRoleServiceImpl implements TenantRoleService {

    private final TenantRepository tenantRepository;
    private final UserRoleRepository userRoleRepository;

    Logger logger = LoggerFactory.getLogger(TenantRoleServiceImpl.class);

    public TenantRoleServiceImpl(TenantRepository tenantRepository, UserRoleRepository userRoleRepository) {
        this.tenantRepository = tenantRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Tenant getTenantRole(String tenantId) throws NotFoundResponseException {
        logger.debug("Fetching tenant role for tenantId: {}", tenantId);
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> {
            logger.warn("Tenant not found for tenantId: {}", tenantId);
            return new NotFoundResponseException("Tenant not found");
        });
        logger.info("Tenant found: {}", tenant);
        cleanupTenantRoles(List.of(tenant));
        return tenant;
    }

    @Override
    public Tenant updateTenantRole(String tenantId, TenantRole tenantRole) throws NotFoundResponseException {
        logger.debug("Updating tenant role for tenantId: {} with role: {}", tenantId, tenantRole);
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> {
            logger.warn("Tenant not found for tenantId: {}", tenantId);
            return new NotFoundResponseException("Tenant not found");
        });
        tenant.setRole(tenantRole);
        logger.debug("Tenant found: {}", tenant);
        cleanupTenantRoles(List.of(tenant));
        logger.info("Tenant role updated for tenantId: {}", tenantId);
        return tenantRepository.save(tenant);
    }

    @Override
    public void deleteTenantRole(String tenantId) throws NotFoundResponseException {
        logger.debug("Deleting tenant role for tenantId: {}", tenantId);
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> {
            logger.warn("Tenant not found for tenantId: {}", tenantId);
            return new NotFoundResponseException("Tenant not found");
        });
        tenant.setRole(TenantRole.DELETED);
        logger.info("Tenant role set to DELETED for tenantId: {}", tenantId);
        cleanupTenantRoles(List.of(tenant));
        tenantRepository.save(tenant);
    }

    @Override
    public List<Tenant> getTenantsByRole(TenantRole tenantRole) {
        logger.debug("Fetching tenants by role: {}", tenantRole);
        List<Tenant> tenants = tenantRepository.findByRole(tenantRole);
        cleanupTenantRoles(tenants);
        logger.info("Found {} tenants for role: {}", tenants.size(), tenantRole);
        return tenants;
    }

    @Override
    public List<Tenant> getTenantsByRoleAndStatus(TenantRole tenantRole, TenantStatus status) {
        logger.debug("Fetching tenants by role: {} and status: {}", tenantRole, status);
        List<Tenant> tenants = tenantRepository.findByRoleAndStatus(tenantRole, status);
        cleanupTenantRoles(tenants);
        logger.info("Found {} tenants for role: {} and status: {}", tenants.size(), tenantRole, status);
        return tenants;
    }

    @Override
    public List<Tenant> getTenantsByRole() {
        logger.debug("Fetching all tenants");
        List<Tenant> tenants = tenantRepository.findAll();
        cleanupTenantRoles(tenants);
        logger.info("Found {} tenants", tenants.size());
        return tenants;
    }

    private void cleanupTenantRoles(List<Tenant> tenants) {
        logger.debug("Cleaning up tenant roles for {} tenants", tenants.size());
        tenants.forEach(tenant -> {
            List<UserRole> roles = userRoleRepository.findUserRoleByPersonaId(tenant.getTenantId());
            logger.debug("TenantId: {} has {} roles", tenant.getTenantId(), roles.size());
            if (roles.size() > 1) {
                logger.debug("Deleting all roles for tenantId: {}", tenant.getTenantId());
                userRoleRepository.deleteAll(roles);
                logger.info("Deleted all roles for tenantId: {}", tenant.getTenantId());
            } else if (roles.size() == 1 && !roles.get(0).getRole().equals(tenant.getRole().name())) {
                logger.debug("Deleting role for tenantId: {} and saving new role", tenant.getTenantId());
                userRoleRepository.delete(roles.get(0));
                userRoleRepository.save(tenant.getUserRole());
                logger.info("Deleted role for tenantId: {} and saved new role", tenant.getTenantId());
            } else if (roles.isEmpty()) {
                logger.debug("No roles found for tenantId: {} saving new role", tenant.getTenantId());
                userRoleRepository.save(tenant.getUserRole());
                logger.info("Saved role for tenantId: {} as no previous roles existed", tenant.getTenantId());
            }
        });
    }
}
