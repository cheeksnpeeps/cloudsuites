package com.cloudsuites.framework.modules.property.personas.module.role;

import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantRole;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.services.property.personas.service.role.TenantRoleService;
import com.cloudsuites.framework.services.user.entities.UserRole;
import com.cloudsuites.framework.services.user.entities.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TenantRoleServiceImpl implements TenantRoleService {

    private static final String TENANT_NOT_FOUND_LOG = "Tenant not found for tenantId: {}";
    private static final String TENANT_NOT_FOUND_MSG = "Tenant not found";
    
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
            logger.warn(TENANT_NOT_FOUND_LOG, tenantId);
            return new NotFoundResponseException(TENANT_NOT_FOUND_MSG);
        });
        logger.info("Tenant found: {}", tenant);
        cleanupTenantRoles(List.of(tenant));
        return tenant;
    }

    @Override
    public Tenant updateTenantRole(String tenantId, TenantRole tenantRole) throws NotFoundResponseException {
        logger.debug("Updating tenant role for tenantId: {} with role: {}", tenantId, tenantRole);
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> {
            logger.warn(TENANT_NOT_FOUND_LOG, tenantId);
            return new NotFoundResponseException(TENANT_NOT_FOUND_MSG);
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
            logger.warn(TENANT_NOT_FOUND_LOG, tenantId);
            return new NotFoundResponseException(TENANT_NOT_FOUND_MSG);
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
            try {
                List<UserRole> roles = userRoleRepository.findUserRoleByPersonaId(tenant.getTenantId());
                logger.debug("TenantId: {} has {} roles", tenant.getTenantId(), roles.size());
                
                // Ensure tenant has a valid role set, defaulting if null
                if (tenant.getRole() == null) {
                    tenant.setRole(TenantRole.DEFAULT);
                    logger.debug("Set default role for tenantId: {}", tenant.getTenantId());
                }
                
                if (roles.size() > 1) {
                    logger.debug("Deleting all roles for tenantId: {}", tenant.getTenantId());
                    userRoleRepository.deleteAll(roles);
                    // Create and save new role without triggering lazy loading
                    UserRole newRole = createUserRoleForTenant(tenant);
                    userRoleRepository.save(newRole);
                    logger.info("Deleted all roles for tenantId: {} and saved new role", tenant.getTenantId());
                } else if (roles.size() == 1 && !roles.get(0).getRole().equals(tenant.getRole().name())) {
                    logger.debug("Deleting role for tenantId: {} and saving new role", tenant.getTenantId());
                    userRoleRepository.delete(roles.get(0));
                    UserRole newRole = createUserRoleForTenant(tenant);
                    userRoleRepository.save(newRole);
                    logger.info("Deleted role for tenantId: {} and saved new role", tenant.getTenantId());
                } else if (roles.isEmpty()) {
                    logger.debug("No roles found for tenantId: {} saving new role", tenant.getTenantId());
                    UserRole newRole = createUserRoleForTenant(tenant);
                    userRoleRepository.save(newRole);
                    logger.info("Saved role for tenantId: {} as no previous roles existed", tenant.getTenantId());
                }
            } catch (Exception e) {
                logger.error("Error cleaning up roles for tenantId: {} - {}", tenant.getTenantId(), e.getMessage(), e);
                // Continue processing other tenants even if one fails
            }
        });
    }
    
    private UserRole createUserRoleForTenant(Tenant tenant) {
        UserRole userRole = new UserRole();
        userRole.setIdentityId(tenant.getIdentity().getUserId());
        userRole.setPersonaId(tenant.getTenantId());
        userRole.setUserType(UserType.TENANT);
        userRole.setRole(tenant.getRole().name());
        return userRole;
    }
}
