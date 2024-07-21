package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.TenantRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.TenantService;
import com.cloudsuites.framework.services.property.UnitService;
import com.cloudsuites.framework.services.property.entities.Tenant;
import com.cloudsuites.framework.services.property.entities.Unit;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final UserService userService;
    private final UnitService unitService;

    private static final Logger logger = LoggerFactory.getLogger(TenantServiceImpl.class);

    public TenantServiceImpl(TenantRepository tenantRepository, UserService userService, UnitService unitService) {
        this.tenantRepository = tenantRepository;
        this.userService = userService;
        this.unitService = unitService;
    }

    @Transactional
    @Override
    public Tenant createTenant(Tenant tenant, Long unitId) throws NotFoundResponseException {
        // Log the start of the tenant creation process
        logger.debug("Starting tenant creation process for tenant: {}", tenant);

        // Step 1: Create and save the identity for the tenant
        Identity identity = tenant.getIdentity();
        logger.debug("Creating identity: {}", identity.getUserId());
        Identity savedIdentity = userService.createUser(identity);
        tenant.setIdentity(savedIdentity);
        logger.debug("Identity created and saved: {}", savedIdentity.getUserId());

        // Step 2: Retrieve the unit by building and unit ID
        String buildingId = tenant.getBuilding().getBuildingId();
        logger.debug("Fetching unit with ID: {} for building ID: {}", unitId, buildingId);
        Unit unit = unitService.getUnitById(buildingId, unitId);
        if (unit == null) {
            String errorMsg = "Unit not found for building ID: " + buildingId + " and unit ID: " + unitId;
            logger.error(errorMsg);
            throw new NotFoundResponseException(errorMsg);
        }

        // Step 3: Add tenant to the unit's list of tenants
        logger.debug("Adding tenant to unit's tenant list");
        if (unit.getTenants() == null) {
            unit.setTenants(new ArrayList<>());
        }
        unit.getTenants().add(tenant);
        unit.setBuilding(tenant.getBuilding());

        // Save the updated unit
        logger.debug("Saving updated unit with tenants");
        unitService.saveUnit(buildingId, unit.getFloor().getFloorId(), unit);

        // Set the unit in the tenant object
        tenant.setUnit(unit);
        logger.debug("Tenant unit set to: {}", unit.getUnitId());

        // Step 4: Save the tenant
        logger.debug("Saving tenant to repository");
        Tenant savedTenant = tenantRepository.save(tenant);

        // Log success and return the saved tenant
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
    public List<Tenant> getAllTenantsByBuildingAndUnit(String buildingId, Long unitId) throws NotFoundResponseException {
        logger.info("Fetching all tenants for building ID: {} and unit ID: {}", buildingId, unitId);
        return tenantRepository.findByBuilding_BuildingIdAndUnit_UnitId(buildingId, unitId)
                .orElseThrow(() -> {
                    logger.error("No tenants found for building ID: {} and unit ID: {}", buildingId, unitId);
                    return new NotFoundResponseException("No tenants found for Building ID: " + buildingId + " and Unit ID: " + unitId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tenant> getAllTenantsByBuilding(String buildingId) throws NotFoundResponseException {
        logger.info("Fetching all tenants for building ID: {}", buildingId);
        return tenantRepository.findByBuilding_BuildingId(buildingId)
                .orElseThrow(() -> {
                    logger.error("No tenants found for building ID: {}", buildingId);
                    return new NotFoundResponseException("No tenants found for Building ID: " + buildingId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Tenant getTenantByBuildingIdAndUnitIdAndTenantId(String buildingId, Long unitId, Long tenantId) throws NotFoundResponseException {
        logger.info("Fetching tenant for building ID: {}, unit ID: {}, and tenant ID: {}", buildingId, unitId, tenantId);
        return tenantRepository.findByBuilding_BuildingIdAndUnit_UnitIdAndTenantId(buildingId, unitId, tenantId)
                .orElseThrow(() -> {
                    logger.error("Tenant not found for building ID: {}, unit ID: {}, and tenant ID: {}", buildingId, unitId, tenantId);
                    return new NotFoundResponseException("Tenant not found for Building ID: " + buildingId + ", Unit ID: " + unitId + " and Tenant ID: " + tenantId);
                });
    }
}