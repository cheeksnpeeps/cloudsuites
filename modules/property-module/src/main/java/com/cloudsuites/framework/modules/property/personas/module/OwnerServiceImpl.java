package com.cloudsuites.framework.modules.property.personas.module;

import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UserAlreadyExistsException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.entities.OwnerRole;
import com.cloudsuites.framework.services.property.personas.entities.OwnerStatus;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.services.property.personas.service.OwnerService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.services.user.entities.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;


@Component
public class OwnerServiceImpl implements OwnerService {

    private static final Logger logger = LoggerFactory.getLogger(OwnerServiceImpl.class);
    private static final String OWNER_NOT_FOUND_WITH_ID = "Owner not found with ID: {}";
    private static final String OWNER_NOT_FOUND_WITH_EMAIL = "Owner not found with email: {}";
    private static final String OWNER_NOT_FOUND_WITH_USER_ID = "Owner not found for user ID: {}";
    
    private final OwnerRepository ownerRepository;
    private final UnitService unitService;
    private final UserService userService;
    private final TenantRepository tenantRepository;
    private final UserRoleRepository userRoleRepository;

    public OwnerServiceImpl(OwnerRepository ownerRepository, UnitService unitService, UserService userService, TenantRepository tenantRepository, UserRoleRepository userRoleRepository) {
        this.ownerRepository = ownerRepository;
        this.unitService = unitService;
        this.userService = userService;
        this.tenantRepository = tenantRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Owner getOwnerById(String ownerId) throws NotFoundResponseException {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> {
                    logger.error(OWNER_NOT_FOUND_WITH_ID, ownerId);
                    return new NotFoundResponseException("Owner not found with ID: " + ownerId);
                });
    }

    @Override
    @Transactional
    public Owner createOwner(Owner newOwner) throws UserAlreadyExistsException, InvalidOperationException {
        Owner owner = createIdentity(newOwner);
        
        // Ensure role is set before saving
        if (owner.getRole() == null) {
            logger.debug("Setting default role for owner: {}", owner.getIdentity().getEmail());
            owner.setRole(OwnerRole.DEFAULT);
        }
        
        owner = ownerRepository.save(owner);
        logger.info("Owner created successfully with ID: {}", owner.getOwnerId());
        
        // Ensure user role is set and saved
        UserRole userRole = owner.getUserRole();
        if (userRole != null) {
            userRoleRepository.save(userRole);
            logger.debug("User role saved for owner: {} - {}", owner.getOwnerId(), userRole);
        } else {
            logger.warn("No user role found for owner: {}", owner.getOwnerId());
        }
        return owner;
    }

    @Override
    @Transactional
    public Owner createOwner(Owner newOwner, Building building, Unit unit) throws NotFoundResponseException, UserAlreadyExistsException, InvalidOperationException {
        Owner owner = createIdentity(newOwner);

        // validate building and unit
        if (building == null) {
            logger.error("Building not found for owner: {}", owner);
            throw new NotFoundResponseException("Building not found for owner: " + owner);
        }
        if (unit == null) {
            logger.error("Unit not found for owner: {}", owner);
            throw new NotFoundResponseException("Unit not found for owner: " + owner);
        }
        if (!unit.getBuilding().getBuildingId().equals(building.getBuildingId())) {
            logger.error("Unit does not belong to the building");
            throw new InvalidOperationException("Unit does not belong to the building");
        }
        // Save the updated unit
        logger.debug("Saving updated unit with owner");
        Unit savedUnit = unitService.saveUnit(unit);

        owner.setStatus(OwnerStatus.ACTIVE);
        if (Boolean.TRUE.equals(owner.getIsPrimaryTenant())) {
            if (unit.getTenants() != null) {
                for (Tenant existingTenant : unit.getTenants()) {
                    existingTenant.setStatus(TenantStatus.INACTIVE); // Or another appropriate status
                    tenantRepository.save(existingTenant);
                    logger.debug("Deactivated existing tenant ID: {}", existingTenant.getTenantId());
                }
            }
            Tenant tenant = createTenantFromOwnerInfo(building, savedUnit, owner.getIdentity());
            unit.addTenant(tenant);
        }

        // Set the unit in the owner object
        owner.addUnit(savedUnit);
        logger.debug("Owner unit set to: {}", unit.getUnitId());


        // Step 4: Save the owner
        logger.debug("Saving owner with unit: {}", unit.getUnitId());
        Owner savedOwner = ownerRepository.save(owner);
        savedUnit.setOwner(savedOwner);
        unitService.saveUnit(savedUnit);
        // Log success and return the saved owner
        logger.info("Owner created successfully with ID: {}", savedOwner.getOwnerId());
        
        // Ensure user role is set and saved
        UserRole userRole = owner.getUserRole();
        if (userRole != null) {
            userRoleRepository.save(userRole);
            logger.debug("User role saved for owner: {} - {}", owner.getOwnerId(), userRole);
        } else {
            logger.warn("No user role found for owner: {}", owner.getOwnerId());
        }
        return savedOwner;
    }

    /**
     * Core business logic for owner creation implementing the decision table from ADR-001.
     * 
     * <p>This method handles the complex logic of Identity/Owner relationship management,
     * implementing the following decision table:</p>
     * 
     * <table border="1">
     * <tr><th>Identity Exists?</th><th>Owner Exists?</th><th>Action</th></tr>
     * <tr><td>No</td><td>No</td><td>Create new Identity and Owner</td></tr>
     * <tr><td>Yes</td><td>No</td><td>Create Owner with existing Identity</td></tr>
     * <tr><td>Yes</td><td>Yes</td><td>Throw UserAlreadyExistsException</td></tr>
     * </table>
     * 
     * <h3>Process Flow:</h3>
     * <ol>
     * <li>Validate identity and email presence</li>
     * <li>Normalize email (trim + lowercase)</li>
     * <li>Check if Identity exists by email</li>
     * <li>If Identity exists, check if Owner already exists</li>
     * <li>Create or reuse Identity as appropriate</li>
     * <li>Return Owner with proper Identity association</li>
     * </ol>
     * 
     * <h3>Email Normalization:</h3>
     * <p>All emails are normalized using {@code email.trim().toLowerCase()} to ensure
     * consistent lookup and prevent duplicate issues due to case/whitespace differences.</p>
     * 
     * <h3>Concurrency Safety:</h3>
     * <p>Database unique constraints provide ultimate consistency. Race conditions between
     * identity existence check and creation will result in constraint violations that are
     * handled appropriately by the calling method's transaction management.</p>
     * 
     * @param owner The owner entity containing identity information
     * @return Owner entity with properly associated Identity (existing or newly created)
     * @throws UserAlreadyExistsException if an Owner already exists for the given email
     * @throws InvalidOperationException if identity is null or email is missing/invalid
     * @see ADR-001-Owner-Identity-Management for detailed business rules
     */
    private Owner createIdentity(Owner owner) throws UserAlreadyExistsException, InvalidOperationException {
        // Log the start of the owner creation process
        logger.debug("Starting owner creation process for owner: {}", owner);
        // Step 1: Validate and process the identity for the owner
        Identity identity = owner.getIdentity();

        if (identity == null) {
            logger.error("Identity not found for owner: {}", owner);
            throw new InvalidOperationException("Identity not found for owner: " + owner);
        }
        logger.debug("Processing identity with email: {}", identity.getEmail());
        if (!StringUtils.hasText(identity.getEmail())) {
            logger.error("Email not found for owner: {}", owner);
            throw new InvalidOperationException("Email is required");
        }
        
        // Normalize email - trim and convert to lowercase
        String normalizedEmail = identity.getEmail().trim().toLowerCase();
        identity.setEmail(normalizedEmail);
        
        // Step 2: Check if Identity exists
        Identity existingIdentity = userService.findByEmail(normalizedEmail);
        
        if (existingIdentity != null) {
            // Identity exists - check if Owner already exists with this identity
            logger.debug("Found existing identity for email: {}, checking for existing owner", identity.getEmail());
            
            Optional<Owner> existingOwner = ownerRepository.findByIdentity_UserId(existingIdentity.getUserId());
            if (existingOwner.isPresent()) {
                // Both Identity and Owner exist - throw error
                logger.error("Owner already exists with email: {}", normalizedEmail);
                throw new UserAlreadyExistsException("Owner already exists with email: " + normalizedEmail);
            } else {
                // Identity exists but no Owner - create Owner with existing Identity
                logger.debug("Identity exists but no owner found. Creating owner with existing identity: {}", existingIdentity.getUserId());
                owner.setIdentity(existingIdentity);
                return owner;
            }
        } else {
            // No identity exists - create new Identity + Owner
            logger.debug("No existing identity found. Creating new identity and owner");
            Identity savedIdentity = userService.createUser(identity);
            owner.setIdentity(savedIdentity);
            logger.debug("New identity created and saved: {}", savedIdentity.getUserId());
            return owner;
        }
    }

    private Tenant createTenantFromOwnerInfo(Building building, Unit savedUnit, Identity savedIdentity) {
        Tenant tenant = new Tenant();
        tenant.setBuilding(building);
        tenant.setUnit(savedUnit);
        tenant.setIdentity(savedIdentity);
        tenant.setIsPrimaryTenant(true);
        tenant.setIsOwner(true);
        tenant.setStatus(TenantStatus.ACTIVE);
        return tenantRepository.save(tenant);
    }

    @Override
    @Transactional
    public void createOrUpdateOwner(Tenant tenant) throws NotFoundResponseException {
        // Log the initial state of the tenant
        logger.info("Creating or updating owner for tenant: {}", tenant);

        // Find or create the owner based on tenant's identity
        String userId = tenant.getIdentity().getUserId();
        logger.debug("Finding or creating owner with user ID: {}", userId);
        Owner savedOwner = ownerRepository.findByIdentity_UserId(userId)
                .orElseGet(() -> {
                    logger.debug("Owner not found for user ID: {}. Creating new owner.", userId);
                    return createOwner(tenant);
                });
        // Log the owner details after creation or finding
        logger.debug("Owner found or created: {}", savedOwner);

        // Retrieve building and unit IDs
        String buildingId = tenant.getBuilding().getBuildingId();
        String unitId = tenant.getUnit().getUnitId();
        logger.debug("Building ID: {}, Unit ID: {}", buildingId, unitId);

        // Retrieve the unit
        logger.debug("Fetching unit with ID: {} for building ID: {}", unitId, buildingId);
        Unit unit = unitService.getUnitById(buildingId, unitId);

        // Log the unit details
        logger.debug("Unit retrieved: {}", unit);

        // Update the unit with the saved owner
        logger.debug("Setting owner for unit: {}", savedOwner);
        unit.setOwner(savedOwner);
        logger.debug("Saving updated unit with owner.");
        Unit savedUnit = unitService.saveUnit(unit);
        tenant.setUnit(savedUnit);
        // Log the successful update
        logger.info("Owner created or updated successfully for tenant: {}", tenant);
    }

    private Owner createOwner(Tenant tenant) {
        Owner owner = new Owner();
        owner.setIdentity(tenant.getIdentity());
        owner.setStatus(OwnerStatus.ACTIVE);
        owner.setIsPrimaryTenant(tenant.getIsPrimaryTenant());
        
        // Ensure role is set
        if (owner.getRole() == null) {
            logger.debug("Setting default role for owner created from tenant: {}", tenant.getTenantId());
            owner.setRole(OwnerRole.DEFAULT);
        }
        
        Owner savedOwner = ownerRepository.save(owner);
        
        // Ensure user role is set and saved
        UserRole userRole = savedOwner.getUserRole();
        if (userRole != null) {
            userRoleRepository.save(userRole);
            logger.debug("User role saved for owner created from tenant: {} - {}", savedOwner.getOwnerId(), userRole);
        } else {
            logger.warn("No user role found for owner created from tenant: {}", savedOwner.getOwnerId());
        }
        
        return savedOwner;
    }

    @Override
    @Transactional
    public Owner updateOwner(String ownerId, Owner owner) throws NotFoundResponseException {
        Owner existingOwner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> {
                    logger.error(OWNER_NOT_FOUND_WITH_ID, ownerId);
                    return new NotFoundResponseException("Owner not found with ID: " + ownerId);
                });
        existingOwner.getIdentity().updateIdentity(owner.getIdentity());
        userService.updateUser(existingOwner.getIdentity().getUserId(), existingOwner.getIdentity());

        existingOwner.updateOwner(owner);
        logger.info("Updating owner with ID: {}", ownerId);
        Owner updatedOwner = ownerRepository.save(existingOwner);
        logger.info("Owner updated successfully with ID: {}", updatedOwner.getOwnerId());
        return updatedOwner;
    }

    @Override
    @Transactional
    public void deleteOwner(String ownerId) throws NotFoundResponseException, InvalidOperationException {
        Optional<Owner> owner = ownerRepository.findById(ownerId);
        if (owner.isEmpty()) {
            logger.error(OWNER_NOT_FOUND_WITH_ID, ownerId);
            throw new NotFoundResponseException("Owner not found with ID: " + ownerId);
        }
        if (owner.get().getUnits() != null) {
            throw new InvalidOperationException("Owner has units. Cannot delete owner with units.");
        }
        logger.info("Deleting owner with ID: {}", ownerId);
        Owner existingOwner = getOwnerById(ownerId);
        ownerRepository.delete(existingOwner);
        logger.info("Owner deleted successfully with ID: {}", ownerId);
    }

    @Override
    public List<Owner> getAllOwners() throws NotFoundResponseException {
        List<Owner> owners = ownerRepository.findAll();
        if (owners.isEmpty()) {
            logger.error("No owner found");
            throw new NotFoundResponseException("No owner found");
        }
        return owners;
    }

    @Override
    public Owner findByEmail(String email) throws NotFoundResponseException {
        return ownerRepository.findByIdentity_Email(email)
                .orElseThrow(() -> {
                    logger.error(OWNER_NOT_FOUND_WITH_EMAIL, email);
                    return new NotFoundResponseException("Owner not found with email: " + email);
                });
    }

    @Override
    public Owner findByName(String firstName) throws NotFoundResponseException {
        return ownerRepository.findByIdentity_FirstName(firstName)
                .orElseThrow(() -> {
                    logger.error("Owner not found with name: {}", firstName);
                    return new NotFoundResponseException("Owner not found with name: " + firstName);
                });
    }

    @Override
    public Owner findByUserId(String userId) throws NotFoundResponseException {
        return ownerRepository.findByIdentity_UserId(userId)
                .orElseThrow(() -> {
                    logger.error(OWNER_NOT_FOUND_WITH_USER_ID, userId);
                    return new NotFoundResponseException("Owner not found for User ID: " + userId);
                });
    }

}
