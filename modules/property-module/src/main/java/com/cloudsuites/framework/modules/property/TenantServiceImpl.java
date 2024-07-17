package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.TenantRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.TenantService;
import com.cloudsuites.framework.services.property.entities.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    private static final Logger logger = LoggerFactory.getLogger(TenantServiceImpl.class);

    @Override
    public Tenant createTenant(Tenant tenant) {
        logger.info("Creating new tenant: {}", tenant);
        Tenant savedTenant = tenantRepository.save(tenant);
        logger.info("Tenant created successfully with ID: {}", savedTenant.getTenantId());
        return savedTenant;
    }

    @Override
    public Tenant findByUserId(Long userId) throws NotFoundResponseException {
        return tenantRepository.findByIdentity_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("Tenant not found for user ID: {}", userId);
                    return new NotFoundResponseException("Tenant not found for User ID: " + userId);
                });
    }

    @Override
    public Tenant getTenantById(Long tenantId) throws NotFoundResponseException {
        logger.info("Fetching tenant with ID: {}", tenantId);
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    logger.error("Tenant not found with ID: {}", tenantId);
                    return new NotFoundResponseException("Tenant not found with ID: " + tenantId);
                });
    }

    @Override
    public Tenant updateTenant(Long tenantId, Tenant tenant) throws NotFoundResponseException {
        logger.info("Updating tenant with ID: {}", tenantId);
        Tenant existingTenant = getTenantById(tenantId);
        existingTenant.setIdentity(tenant.getIdentity());
        existingTenant.setUnit(tenant.getUnit());
        // Update other fields as necessary
        Tenant updatedTenant = tenantRepository.save(existingTenant);
        logger.info("Tenant updated successfully with ID: {}", updatedTenant.getTenantId());
        return updatedTenant;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tenant> getAllTenants() {
        logger.info("Fetching all tenants");
        List<Tenant> tenants = tenantRepository.findAll();
        logger.info("Total tenants found: {}", tenants.size());
        return tenants;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tenant> getAllTenantsByBuildingAndUnit(Long buildingId, Long unitId) throws NotFoundResponseException {
        logger.info("Fetching all tenants for building ID: {} and unit ID: {}", buildingId, unitId);
        return tenantRepository.findByBuildingIdAndUnit_UnitId(buildingId, unitId)
                .orElseThrow(() -> {
                    logger.error("No tenants found for building ID: {} and unit ID: {}", buildingId, unitId);
                    return new NotFoundResponseException("No tenants found for Building ID: " + buildingId + " and Unit ID: " + unitId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tenant> getAllTenantsByBuilding(Long buildingId) throws NotFoundResponseException {
        logger.info("Fetching all tenants for building ID: {}", buildingId);
        return tenantRepository.findByBuildingId(buildingId)
                .orElseThrow(() -> {
                    logger.error("No tenants found for building ID: {}", buildingId);
                    return new NotFoundResponseException("No tenants found for Building ID: " + buildingId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Tenant getTenantByBuildingIdAndUnitIdAndTenantId(Long buildingId, Long unitId, Long tenantId) throws NotFoundResponseException {
        logger.info("Fetching tenant for building ID: {}, unit ID: {}, and tenant ID: {}", buildingId, unitId, tenantId);
        return tenantRepository.findByBuildingIdAndUnit_UnitIdAndTenantId(buildingId, unitId, tenantId)
                .orElseThrow(() -> {
                    logger.error("Tenant not found for building ID: {}, unit ID: {}, and tenant ID: {}", buildingId, unitId, tenantId);
                    return new NotFoundResponseException("Tenant not found for Building ID: " + buildingId + ", Unit ID: " + unitId + " and Tenant ID: " + tenantId);
                });
    }
}