package com.cloudsuites.framework.modules.property.personas.module;

import com.cloudsuites.framework.modules.property.features.repository.LeaseRepository;
import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.UserRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UserAlreadyExistsException;
import com.cloudsuites.framework.services.property.features.entities.Lease;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.services.property.personas.service.OwnerService;
import com.cloudsuites.framework.services.property.personas.service.TenantService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.services.user.entities.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final UserService userService;
    private final UnitService unitService;

    private static final Logger logger = LoggerFactory.getLogger(TenantServiceImpl.class);
    private final OwnerService ownerService;
    private final UnitRepository unitRepository;
    private final UserRoleRepository userRoleRepository;
    private final LeaseRepository leaseRepository;
    private final UserRepository userRepository;

    public TenantServiceImpl(TenantRepository tenantRepository, UserService userService, UnitService unitService, OwnerService ownerService, UnitRepository unitRepository, UserRoleRepository userRoleRepository, LeaseRepository leaseRepository, UserRepository userRepository) {
        this.tenantRepository = tenantRepository;
        this.userService = userService;
        this.unitService = unitService;
        this.ownerService = ownerService;
        this.unitRepository = unitRepository;
        this.userRoleRepository = userRoleRepository;
        this.leaseRepository = leaseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Tenant createTenant(Tenant tenant, Unit unit) throws NotFoundResponseException, InvalidOperationException, UserAlreadyExistsException {
        // Validate tenant and identity
        validateTenantAndIdentity(tenant);

        if (Boolean.TRUE.equals(tenant.getIsPrimaryTenant())) {
            logger.debug("Tenant is marked as primary. Clearing existing tenants for unit: {}", unit.getUnitId());
            clearExistingTenants(unit);
        }
        // Create or fetch identity for tenant
        Identity savedIdentity = createOrFetchIdentity(tenant);
        tenant.setIdentity(savedIdentity);

        // Set the unit in the tenant object
        tenant.setUnit(unit);
        logger.debug("Tenant unit set to: {}", unit.getUnitId());

        // Save or update lease if tenant is not an owner
        if (Boolean.FALSE.equals(tenant.getIsOwner())) {
            manageLeaseForTenant(tenant, unit);
        } else {
            tenant.setLease(null);
        }
        // Save the tenant
        Tenant savedTenant = saveTenantAndRole(tenant, unit);
        logger.debug("Tenant saved successfully: {}", savedTenant.getLease());
        // Handle owner registration if applicable
        handleOwnerRegistrationIfApplicable(savedTenant);

        logger.info("Tenant created successfully with ID: {}", savedTenant.getTenantId());
        return savedTenant;
    }

    private void validateTenantAndIdentity(Tenant tenant) throws InvalidOperationException, UserAlreadyExistsException {
        if (tenant.getIdentity() == null) {
            logger.error("Identity not found for tenant: {}", tenant);
            throw new InvalidOperationException("Identity not found for tenant: " + tenant);
        }
        if (!StringUtils.hasText(tenant.getIdentity().getEmail())) {
            logger.error("Email not provided for tenant: {}", tenant);
            throw new InvalidOperationException("Email is required");
        }
        if (userService.existsByEmail(tenant.getIdentity().getEmail())) {
            logger.error("User already exists with email: {}", tenant.getIdentity().getEmail());
            throw new UserAlreadyExistsException("User already exists with email: " + tenant.getIdentity().getEmail());
        }
    }

    private Identity createOrFetchIdentity(Tenant tenant) throws UserAlreadyExistsException {
        logger.debug("Creating identity with email: {}", tenant.getIdentity().getEmail());
        return userService.createUser(tenant.getIdentity());
    }

    private void manageLeaseForTenant(Tenant tenant, Unit unit) {
        logger.debug("Tenant is not an owner.");
        Owner owner = unit.getOwner();
        
        if (owner == null) {
            logger.warn("No owner found for unit: {}. Skipping lease management.", unit.getUnitId());
            // Set lease to null if no owner is associated with the unit
            tenant.setLease(null);
            return;
        }
        
        Lease savedLease = leaseRepository.findByUnitIdAndOwnerId(owner.getOwnerId(), unit.getUnitId());
        if (savedLease != null) {
            logger.info("Lease already exists for owner: {} and unit: {}. Updating lease.", owner.getOwnerId(), unit.getUnitId());
            savedLease.updateLease(tenant.getLease());
            leaseRepository.save(savedLease);
            tenant.setLease(savedLease);
        } else {
            Lease lease = tenant.getLease();
            if (lease != null) {
                lease.setUnitId(unit.getUnitId());
                lease.setOwnerId(owner.getOwnerId());
                logger.debug("Creating lease for tenant: {} and unit: {}", tenant.getTenantId(), unit.getUnitId());
                lease = leaseRepository.save(lease);
                tenant.setLease(lease);
            } else {
                logger.debug("No lease provided for tenant: {}", tenant.getTenantId());
            }
        }
    }

    private Tenant saveTenantAndRole(Tenant tenant, Unit unit) {
        logger.debug("Saving tenant to repository");
        Tenant savedTenant = tenantRepository.save(tenant);
        UserRole userRole = userRoleRepository.save(savedTenant.getUserRole());
        logger.debug("User role created: {} - {}", userRole.getPersonaId(), userRole.getRole());

        unit.addTenant(savedTenant);
        unitRepository.save(unit);

        return savedTenant;
    }

    private void handleOwnerRegistrationIfApplicable(Tenant tenant) throws NotFoundResponseException {
        if (Boolean.TRUE.equals(tenant.getIsOwner())) {
            logger.debug("Tenant is also an owner. Creating or updating owner record.");
            ownerService.createOrUpdateOwner(tenant);
            logger.debug("Owner record created or updated for tenant ID: {}", tenant.getTenantId());
        }
    }

    private void clearExistingTenants(Unit unit) {
        logger.debug("Tenant is marked as primary. Fetching existing tenants for unit: {}", unit.getUnitId());

        // Fetch existing tenants for the specified building and unit
        Optional<List<Tenant>> existingTenants = tenantRepository.findByBuilding_BuildingIdAndUnit_UnitIdAndStatus(unit.getBuilding().getBuildingId(), unit.getUnitId(), TenantStatus.ACTIVE);
        if (existingTenants.isEmpty()) {
            logger.debug("No existing tenants found for unit: {}", unit.getUnitId());
        }
        logger.debug("Existing tenants for unit {}: {}", unit.getUnitId(), existingTenants.get());
        // Deactivate existing tenants if the new tenant is primary
        for (Tenant existingTenant : existingTenants.get()) {
            existingTenant.setStatus(TenantStatus.INACTIVE);
            tenantRepository.save(existingTenant);
            logger.debug("Deactivated existing tenant ID: {}", existingTenant.getTenantId());
        }
    }

    @Override
    public void deleteByTenantId(String tenantId) {
        tenantRepository.deleteById(tenantId);
    }

    @Override
    public void transferTenant(Tenant tenant, Unit newUnit, Unit oldUnit) throws InvalidOperationException {
        if (newUnit.getTenants().stream().anyMatch(t -> t.getStatus().equals(TenantStatus.ACTIVE))) {
            throw new InvalidOperationException("Unit already contains active tenants");
        }
        if (Boolean.TRUE.equals(tenant.getIsPrimaryTenant())) {
            // transfer all active tenants to the new unit
            List<Tenant> activeTenants = oldUnit.getTenants().stream()
                    .filter(t -> t.getStatus().equals(TenantStatus.ACTIVE))
                    .toList();
            activeTenants.forEach(t -> {
                t.setUnit(newUnit);
                newUnit.getTenants().add(t);
            });
        } else {
            newUnit.getTenants().add(tenant);
        }
        unitRepository.save(newUnit);

        tenant.setUnit(newUnit);
        tenantRepository.save(tenant);
        // disable old tenant
        if (oldUnit.getTenants() == null) return;
        oldUnit.getTenants().stream()
                .filter(t -> t.getTenantId().equals(tenant.getTenantId()))
                .findFirst()
                .ifPresent(t -> t.setStatus(TenantStatus.INACTIVE));
        unitRepository.save(oldUnit);
    }

    @Override
    public void inactivateTenant(Tenant tenant) {
        if (tenant.getUnit() == null) {
            logger.error("No unit found for tenant: {}", tenant.getTenantId());
            return;
        }
        tenant.setStatus(TenantStatus.INACTIVE);
        if (Boolean.TRUE.equals(tenant.getIsPrimaryTenant())) {
            tenant.getUnit().getTenants().forEach(t -> t.setStatus(TenantStatus.INACTIVE));
        }
        tenantRepository.save(tenant);
    }

    @Override
    public void saveTenant(Tenant tenant) {
        tenantRepository.save(tenant);
        userRoleRepository.save(tenant.getUserRole());
    }

    @Override
    public Tenant getTenantById(String tenantId) throws NotFoundResponseException {
        logger.info("Fetching tenant with ID: {}", tenantId);
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    logger.error("Tenant not found with ID: {}", tenantId);
                    return new NotFoundResponseException("Tenant not found with ID: " + tenantId);
                });
    }

    @Override
    public Tenant updateTenant(String tenantId, Tenant tenant) throws NotFoundResponseException {
        logger.info("Updating tenant with ID: {}", tenantId);
        Tenant existingTenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    logger.error("Tenant not found with ID: {}", tenantId);
                    return new NotFoundResponseException("Tenant not found with ID: " + tenantId);
                });
        logger.debug("Found existing tenant: {}", existingTenant);

        if (tenant.getIdentity() != null) {
            logger.debug("Updating tenant identity.");
            existingTenant.getIdentity().updateIdentity(tenant.getIdentity());
            userService.updateUser(existingTenant.getIdentity().getUserId(), existingTenant.getIdentity());
            existingTenant.updateTenant(tenant);
            logger.debug("Updated Tenant Identity: {}", existingTenant.getIdentity().getUserId());
        } else {
            logger.debug("No identity update required.");
        }

        // Check and handle owner status changes
        if (Boolean.FALSE.equals(existingTenant.getIsOwner()) && Boolean.TRUE.equals(tenant.getIsOwner())) {
            logger.debug("Tenant is now marked as owner. Creating or updating owner.");
            ownerService.createOrUpdateOwner(existingTenant);
            unitService.setOwnerForUnit(existingTenant);
            existingTenant.setIsPrimaryTenant(true);
            existingTenant.setIsOwner(tenant.getIsOwner());
        } else {
            logger.debug("No changes to owner status or tenant is not an owner.");
        }
        if (tenant.getStatus() != null) {
            logger.debug("Updating tenant status to: {}", tenant.getStatus());
            existingTenant.setStatus(tenant.getStatus());
        } else {
            logger.debug("No status update required.");
        }
        logger.debug("Updated tenant details for: {}", existingTenant.getTenantId());
        // Save the updated tenant
        Tenant updatedTenant = tenantRepository.save(existingTenant);
        logger.info("Tenant updated successfully {} ", updatedTenant.getTenantId());

        return updatedTenant;
    }

    @Override
    public List<Tenant> getAllTenants(TenantStatus status) throws NotFoundResponseException {
        logger.info("Fetching all tenants");
        if (status != null) {
            logger.info("Fetching all tenants with status: {}", status);
            return tenantRepository.findByStatus(status).orElseThrow(() -> {
                logger.error("No tenants found with status: {}", status);
                return new NotFoundResponseException("No tenants found with status: " + status);
            });
        }
        List<Tenant> tenants = tenantRepository.findAll();
        logger.info("Total tenants found: {}", tenants.size());
        return tenants;
    }

    @Override
    public List<Tenant> findTenantsByBuildingId(String buildingId, TenantStatus status, String query) throws NotFoundResponseException {
        logger.info("Fetching tenants for building ID: {}", buildingId);

        List<Tenant> tenants;

        if (status != null && query != null) {
            logger.info("Fetching tenants with status: {} and query: {}", status, query);
            tenants = findTenantsByStatusAndQuery(buildingId, status, query);
        } else if (status != null) {
            logger.info("Fetching tenants with status: {}", status);
            tenants = tenantRepository.findByBuilding_BuildingIdAndStatus(buildingId, status).orElseThrow(() -> {
                logger.error("No tenants found with status: {}", status);
                return new NotFoundResponseException("No tenants found with status: " + status);
            });
        } else if (query != null) {
            logger.info("Fetching tenants with query: {}", query);
            tenants = findTenantsByQuery(buildingId, query);
        } else {
            tenants = tenantRepository.findAll();
        }

        logger.info("Total tenants found: {}", tenants.size());
        return tenants;
    }

    private List<Tenant> findTenantsByStatusAndQuery(String buildingId, TenantStatus status, String query) {
        Optional<List<Identity>> identities = userRepository.findByFirstNameLikeOrLastNameLikeOrEmailLikeOrPhoneNumberLike(query, query, query, query);
        if (identities.isEmpty()) {
            return new ArrayList<>();
        }
        return tenantRepository.findAllByBuilding_BuildingIdAndStatusAndIdentity_UserIdIn(
                buildingId,
                status,
                identities.get().stream().map(Identity::getUserId).toList()
        );
    }

    private List<Tenant> findTenantsByQuery(String buildingId, String query) {
        Optional<List<Identity>> identities = userRepository.findByFirstNameLikeOrLastNameLikeOrEmailLikeOrPhoneNumberLike(query, query, query, query);
        if (identities.isEmpty()) {
            return new ArrayList<>();
        }
        return tenantRepository.findAllByBuilding_BuildingIdAndIdentity_UserIdIn(
                buildingId,
                identities.get().stream().map(Identity::getUserId).toList()
        );
    }

    @Override
    public List<Tenant> getAllTenantsByBuildingAndUnit(String buildingId, String unitId, TenantStatus status) throws NotFoundResponseException {
        logger.info("Fetching all tenants for building ID: {} and unit ID: {}", buildingId, unitId);
        if (status != null) {
            logger.info("Fetching all tenants with status: {}", status);
            Optional<List<Tenant>> tenants = tenantRepository.findByBuilding_BuildingIdAndUnit_UnitIdAndStatus(buildingId, unitId, status);
        }
        return tenantRepository.findByBuilding_BuildingIdAndUnit_UnitId(buildingId, unitId).get();
    }

    @Override
    public List<Tenant> getAllTenantsByBuilding(String buildingId, TenantStatus status) throws NotFoundResponseException {
        logger.info("Fetching all tenants for building ID: {}", buildingId);
        if (status != null) {
            logger.info("Fetching all tenants with status: {}", status);
            return tenantRepository.findByBuilding_BuildingIdAndStatus(buildingId, status)
                    .orElseThrow(() -> {
                        logger.error("No tenants found for building ID: {} with status: {}", buildingId, status);
                        return new NotFoundResponseException("No tenants found for Building ID: " + buildingId + " with status: " + status);
                    });
        }
        return tenantRepository.findByBuilding_BuildingId(buildingId)
                .orElseThrow(() -> {
                    logger.error("No tenants found for building ID: {}", buildingId);
                    return new NotFoundResponseException("No tenants found for Building ID: " + buildingId);
                });
    }

    @Override
    public Tenant getTenantByBuildingIdAndUnitIdAndTenantId(String buildingId, String unitId, String tenantId) throws NotFoundResponseException {
        logger.info("Fetching tenant for building ID: {}, unit ID: {}, and tenant ID: {}", buildingId, unitId, tenantId);
        return tenantRepository.findByBuilding_BuildingIdAndUnit_UnitIdAndTenantId(buildingId, unitId, tenantId)
                .orElseThrow(() -> {
                    logger.error("Tenant not found for building ID: {}, unit ID: {}, and tenant ID: {}", buildingId, unitId, tenantId);
                    return new NotFoundResponseException("Tenant not found for Building ID: " + buildingId + ", Unit ID: " + unitId + " and Tenant ID: " + tenantId);
                });
    }

    @Override
    public Tenant findByUserId(String userId) throws NotFoundResponseException {
        return tenantRepository.findByIdentity_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("Tenant not found for user ID: {}", userId);
                    return new NotFoundResponseException("Tenant not found for User ID: " + userId);
                });
    }
}