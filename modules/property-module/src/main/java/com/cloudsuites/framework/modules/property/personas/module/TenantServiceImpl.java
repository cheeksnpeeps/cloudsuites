package com.cloudsuites.framework.modules.property.personas.module;

import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.service.OwnerService;
import com.cloudsuites.framework.services.property.personas.service.TenantService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final UserService userService;
    private final UnitService unitService;

    private static final Logger logger = LoggerFactory.getLogger(TenantServiceImpl.class);
    private final OwnerService ownerService;

    public TenantServiceImpl(TenantRepository tenantRepository, UserService userService, UnitService unitService, OwnerService ownerService) {
        this.tenantRepository = tenantRepository;
        this.userService = userService;
        this.unitService = unitService;
        this.ownerService = ownerService;
    }

    @Transactional
    @Override
    public Tenant createTenant(Tenant tenant, String unitId) throws NotFoundResponseException {
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

    @Transactional
    @Override
    public Tenant findByUserId(String userId) throws NotFoundResponseException {
        return tenantRepository.findByIdentity_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("Tenant not found for user ID: {}", userId);
                    return new NotFoundResponseException("Tenant not found for User ID: " + userId);
                });
    }

    @Override
    public void deleteByTenantId(String tenantId) {
        tenantRepository.deleteById(tenantId);
    }

    @Transactional
    @Override
    public Tenant getTenantById(String tenantId) throws NotFoundResponseException {
        logger.info("Fetching tenant with ID: {}", tenantId);
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    logger.error("Tenant not found with ID: {}", tenantId);
                    return new NotFoundResponseException("Tenant not found with ID: " + tenantId);
                });
    }

    @Transactional
    @Override
    public Tenant updateTenant(String tenantId, Tenant tenant) throws NotFoundResponseException {
        // Log the start of the tenant update process
        logger.info("Updating tenant with ID: {}", tenantId);

        // Fetch the existing tenant
        Tenant existingTenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    logger.error("Tenant not found with ID: {}", tenantId);
                    return new NotFoundResponseException("Tenant not found with ID: " + tenantId);
                });

        // Log the existing tenant details
        logger.debug("Found existing tenant: {}", existingTenant);

        // Update identity if provided
        if (tenant.getIdentity() != null) {
            logger.debug("Updating tenant identity.");
            Identity savedIdentity = updateTenantIdentity(tenant, existingTenant);
            logger.debug("Updated identity: {}", savedIdentity);
            userService.updateUser(savedIdentity.getUserId(), savedIdentity);
            tenant.setIdentity(savedIdentity);
        } else {
            logger.debug("No identity update required.");
        }

        // Check and handle owner status changes
        if (Boolean.FALSE.equals(existingTenant.getIsOwner()) && Boolean.TRUE.equals(tenant.getIsOwner())) {
            logger.debug("Tenant is marked as owner. Creating or updating owner.");
            ownerService.createOrUpdateOwner(tenant);
        } else {
            tenant.getUnit().setOwner(existingTenant.getUnit().getOwner());
            logger.debug("No changes to owner status or tenant is not an owner.");
        }

        // Update other tenant fields
//        existingTenant.setIsPrimaryTenant(tenant.getIsPrimaryTenant());
        existingTenant.setIsOwner(tenant.getIsOwner());
        if (tenant.getStatus() != null) {
            logger.debug("Updating tenant status to: {}", tenant.getStatus());
            existingTenant.setStatus(tenant.getStatus());
        } else {
            logger.debug("No status update required.");
        }
        existingTenant.setUnit(tenant.getUnit());
        logger.debug("Updated tenant details: {}", existingTenant);

        // Save the updated tenant
        Tenant updatedTenant = tenantRepository.save(existingTenant);
        logger.info("Tenant updated successfully with ID: {}", updatedTenant.getTenantId());

        return updatedTenant;
    }

    private Identity updateTenantIdentity(Tenant tenant, Tenant existingTenant) {
        Identity identity = tenant.getIdentity();
        Identity existingIdentity = existingTenant.getIdentity();
        if (StringUtils.hasText(identity.getFirstName())) {
            existingIdentity.setFirstName(identity.getFirstName());
        }
        if (StringUtils.hasText(identity.getLastName())) {
            existingIdentity.setLastName(identity.getLastName());
        }
        if (StringUtils.hasText(identity.getEmail())) {
            existingIdentity.setEmail(identity.getEmail());
        }
        if (StringUtils.hasText(identity.getPhoneNumber())) {
            existingIdentity.setPhoneNumber(identity.getPhoneNumber());
        }
        if (identity.getGender() != null) {
            existingIdentity.setGender(identity.getGender());
        }
        if (StringUtils.hasText(identity.getUsername())) {
            existingIdentity.setUsername(identity.getUsername());
        }
        return existingIdentity;
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
    public List<Tenant> getAllTenantsByBuildingAndUnit(String buildingId, String unitId) throws NotFoundResponseException {
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
    public Tenant getTenantByBuildingIdAndUnitIdAndTenantId(String buildingId, String unitId, String tenantId) throws NotFoundResponseException {
        logger.info("Fetching tenant for building ID: {}, unit ID: {}, and tenant ID: {}", buildingId, unitId, tenantId);
        return tenantRepository.findByBuilding_BuildingIdAndUnit_UnitIdAndTenantId(buildingId, unitId, tenantId)
                .orElseThrow(() -> {
                    logger.error("Tenant not found for building ID: {}, unit ID: {}, and tenant ID: {}", buildingId, unitId, tenantId);
                    return new NotFoundResponseException("Tenant not found for Building ID: " + buildingId + ", Unit ID: " + unitId + " and Tenant ID: " + tenantId);
                });
    }
}