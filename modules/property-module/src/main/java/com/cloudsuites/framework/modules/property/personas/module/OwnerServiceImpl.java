package com.cloudsuites.framework.modules.property.personas.module;

import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.service.OwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OwnerServiceImpl implements OwnerService {

    private static final Logger logger = LoggerFactory.getLogger(OwnerServiceImpl.class);
    @Autowired
    private OwnerRepository ownerRepository;

    @Override
    public Owner getOwnerById(Long id) throws NotFoundResponseException {
        return ownerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Owner not found with ID: {}", id);
                    return new NotFoundResponseException("Owner not found with ID: " + id);
                });
    }

    @Override
    public Owner createOwner(Owner owner) {
        logger.info("Creating new owner: {}", owner);
        Owner savedOwner = ownerRepository.save(owner);
        logger.info("Owner created successfully with ID: {}", savedOwner.getOwnerId());
        return savedOwner;
    }

    @Override
    public Owner updateOwner(Long id, Owner owner) throws NotFoundResponseException {
        logger.info("Updating owner with ID: {}", id);
        Owner updatedOwner = ownerRepository.save(owner);
        logger.info("Owner updated successfully with ID: {}", updatedOwner.getOwnerId());
        return updatedOwner;
    }

    @Override
    public void deleteOwner(Long id) throws NotFoundResponseException {
        logger.info("Deleting owner with ID: {}", id);
        Owner existingOwner = getOwnerById(id);
        ownerRepository.delete(existingOwner);
        logger.info("Owner deleted successfully with ID: {}", id);
    }

    @Override
    public List<Owner> getAllOwners() throws NotFoundResponseException {
        List<Owner> owners = ownerRepository.findAll();
        if (owners.isEmpty()) {
            logger.error("No owners found");
            throw new NotFoundResponseException("No owners found");
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
    public Owner findByUserId(Long userId) throws NotFoundResponseException {
        return ownerRepository.findByIdentity_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("Owner not found for user ID: {}", userId);
                    return new NotFoundResponseException("Owner not found for User ID: " + userId);
                });
    }
}
