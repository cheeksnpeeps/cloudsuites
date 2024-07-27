package com.cloudsuites.framework.modules.property.personas.module;

import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.service.OwnerService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OwnerServiceImpl implements OwnerService {

    private static final Logger logger = LoggerFactory.getLogger(OwnerServiceImpl.class);
    private final OwnerRepository ownerRepository;
    private final UnitService unitService;
    private final UserService userService;

    public OwnerServiceImpl(OwnerRepository ownerRepository, UnitService unitService, UserService userService) {
        this.ownerRepository = ownerRepository;
        this.unitService = unitService;
        this.userService = userService;
    }

    @Override
    public Owner getOwnerById(String ownerId) throws NotFoundResponseException {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> {
                    logger.error("Owner not found with ID: {}", ownerId);
                    return new NotFoundResponseException("Owner not found with ID: " + ownerId);
                });
    }

    @Override
    public Owner creatOwner(Owner owner, String buildingId, String unitId) throws NotFoundResponseException {
        // Log the start of the tenant creation process
        logger.debug("Starting owner creation process for owner: {}", owner);

        // Step 1: Create and save the identity for the tenant
        Identity identity = owner.getIdentity();
        logger.debug("Creating identity: {}", identity.getUserId());
        Identity savedIdentity = userService.createUser(identity);
        owner.setIdentity(savedIdentity);
        logger.debug("Identity created and saved: {}", savedIdentity.getUserId());

        // Step 2: Retrieve the unit by building and unit ID
        logger.debug("Fetching unit with ID: {} for building ID: {}", unitId, buildingId);
        Unit unit = unitService.getUnitById(buildingId, unitId);
        if (unit == null) {
            String errorMsg = "Unit not found for building ID: " + buildingId + " and unit ID: " + unitId;
            logger.error(errorMsg);
            throw new NotFoundResponseException(errorMsg);
        }
        // Save the updated unit
        logger.debug("Saving updated unit with owner");
        Unit savedUnit = unitService.saveUnit(buildingId, unit.getFloor().getFloorId(), unit);

        // Set the unit in the owner object
        owner.addUnit(savedUnit);
        logger.debug("Owner unit set to: {}", unit.getUnitId());

        // Step 4: Save the owner
        logger.debug("Saving owner to repository");
        Owner savedOwner = ownerRepository.save(owner);
        savedUnit.setOwner(savedOwner);
        unitService.saveUnit(buildingId, unit.getFloor().getFloorId(), unit);
        // Log success and return the saved owner
        logger.info("Tenant created successfully with ID: {}", savedOwner.getOwnerId());
        return savedOwner;
    }

    @Override
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
        Unit savedUnit = unitService.saveUnit(buildingId, unit.getFloor().getFloorId(), unit);
        tenant.setUnit(savedUnit);
        // Log the successful update
        logger.info("Owner created or updated successfully for tenant: {}", tenant);
    }

    private Owner createOwner(Tenant tenant) {
        Owner owner = new Owner();
        owner.setIdentity(tenant.getIdentity());
        return ownerRepository.save(owner);
    }

    @Override
    public Owner updateOwner(String ownerId, Owner owner) throws NotFoundResponseException {
        logger.info("Updating owner with ID: {}", ownerId);
        Owner updatedOwner = ownerRepository.save(owner);
        logger.info("Owner updated successfully with ID: {}", updatedOwner.getOwnerId());
        return updatedOwner;
    }

    @Override
    public void deleteOwner(String ownerId) throws NotFoundResponseException {
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
                    logger.error("Owner not found with email: {}", email);
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
                    logger.error("Owner not found for user ID: {}", userId);
                    return new NotFoundResponseException("Owner not found for User ID: " + userId);
                });
    }

}
