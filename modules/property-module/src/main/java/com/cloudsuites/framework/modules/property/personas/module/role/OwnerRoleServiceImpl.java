package com.cloudsuites.framework.modules.property.personas.module.role;

import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.entities.OwnerRole;
import com.cloudsuites.framework.services.property.personas.entities.OwnerStatus;
import com.cloudsuites.framework.services.property.personas.service.role.OwnerRoleService;
import com.cloudsuites.framework.services.user.entities.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OwnerRoleServiceImpl implements OwnerRoleService {

    private final OwnerRepository ownerRepository;
    private final UserRoleRepository userRoleRepository;

    Logger logger = LoggerFactory.getLogger(OwnerRoleServiceImpl.class);

    public OwnerRoleServiceImpl(OwnerRepository ownerRepository, UserRoleRepository userRoleRepository) {
        this.ownerRepository = ownerRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Owner getOwnerRole(String ownerId) throws NotFoundResponseException {
        logger.debug("Fetching owner role for ownerId: {}", ownerId);
        Owner owner = ownerRepository.findById(ownerId).orElseThrow(() -> {
            logger.warn("Owner not found for ownerId: {}", ownerId);
            return new NotFoundResponseException("Owner not found");
        });
        logger.info("Owner found: {}", owner);
        cleanupOwnerRoles(List.of(owner));
        return owner;
    }

    @Override
    public Owner updateOwnerRole(String ownerId, OwnerRole ownerRole) throws NotFoundResponseException {
        logger.debug("Updating owner role for ownerId: {} with role: {}", ownerId, ownerRole);
        Owner owner = ownerRepository.findById(ownerId).orElseThrow(() -> {
            logger.warn("Owner not found for ownerId: {}", ownerId);
            return new NotFoundResponseException("Owner not found");
        });
        owner.setRole(ownerRole);
        logger.debug("Owner found: {}", owner);
        cleanupOwnerRoles(List.of(owner));
        logger.info("Owner role updated for ownerId: {}", ownerId);
        return ownerRepository.save(owner);
    }

    @Override
    public void deleteOwnerRole(String ownerId) throws NotFoundResponseException {
        logger.debug("Deleting owner role for ownerId: {}", ownerId);
        Owner owner = ownerRepository.findById(ownerId).orElseThrow(() -> {
            logger.warn("Owner not found for ownerId: {}", ownerId);
            return new NotFoundResponseException("Owner not found");
        });
        owner.setRole(OwnerRole.DELETED);
        logger.info("Owner role set to DELETED for ownerId: {}", ownerId);
        cleanupOwnerRoles(List.of(owner));
        ownerRepository.save(owner);
    }

    @Override
    public List<Owner> getOwnersByRole(OwnerRole ownerRole) {
        logger.debug("Fetching owners by role: {}", ownerRole);
        List<Owner> owners = ownerRepository.findByRole(ownerRole);
        cleanupOwnerRoles(owners);
        logger.info("Found {} owners for role: {}", owners.size(), ownerRole);
        return owners;
    }

    @Override
    public List<Owner> getOwnersByRoleAndStatus(OwnerRole ownerRole, OwnerStatus status) {
        logger.debug("Fetching owners by role: {} and status: {}", ownerRole, status);
        List<Owner> owners = ownerRepository.findByRoleAndStatus(ownerRole, status);
        cleanupOwnerRoles(owners);
        logger.info("Found {} owners for role: {} and status: {}", owners.size(), ownerRole, status);
        return owners;
    }

    @Override
    public List<Owner> getOwnersByRole() {
        logger.debug("Fetching all owners");
        List<Owner> owners = ownerRepository.findAll();
        cleanupOwnerRoles(owners);
        logger.info("Found {} owners", owners.size());
        return owners;
    }

    private void cleanupOwnerRoles(List<Owner> owners) {
        logger.debug("Cleaning up owner roles for {} owners", owners.size());
        owners.forEach(owner -> {
            List<UserRole> roles = userRoleRepository.findUserRoleByPersonaId(owner.getOwnerId());
            logger.debug("OwnerId: {} has {} roles", owner.getOwnerId(), roles.size());
            
            // Ensure owner has a role - set default if null
            if (owner.getRole() == null) {
                logger.warn("Owner {} has null role, setting to DEFAULT", owner.getOwnerId());
                owner.setRole(OwnerRole.DEFAULT);
            }
            
            if (roles.size() > 1) {
                logger.debug("Deleting all roles for ownerId: {}", owner.getOwnerId());
                userRoleRepository.deleteAll(roles);
                logger.info("Deleted all roles for ownerId: {}", owner.getOwnerId());
            } else if (roles.size() == 1 && !roles.get(0).getRole().equals(owner.getRole().name())) {
                logger.debug("Deleting role for ownerId: {} and saving new role", owner.getOwnerId());
                userRoleRepository.delete(roles.get(0));
                userRoleRepository.save(owner.getUserRole());
                logger.info("Deleted role for ownerId: {} and saved new role", owner.getOwnerId());
            } else if (roles.isEmpty()) {
                logger.debug("No roles found for ownerId: {} saving new role", owner.getOwnerId());
                userRoleRepository.save(owner.getUserRole());
                logger.info("Saved role for ownerId: {} as no previous roles existed", owner.getOwnerId());
            }
        });
    }
}
