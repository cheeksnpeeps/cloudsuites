package com.cloudsuites.framework.services.property.personas.service.role;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.entities.OwnerRole;
import com.cloudsuites.framework.services.property.personas.entities.OwnerStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface OwnerRoleService {

    @Transactional
    Owner getOwnerRole(String ownerId) throws NotFoundResponseException;

    @Transactional
    Owner updateOwnerRole(String ownerId, OwnerRole ownerRole) throws NotFoundResponseException;

    @Transactional
    void deleteOwnerRole(String ownerId) throws NotFoundResponseException;

    @Transactional
    List<Owner> getOwnersByRole(OwnerRole ownerRole);

    @Transactional
    List<Owner> getOwnersByRoleAndStatus(OwnerRole ownerRole, OwnerStatus status);

    @Transactional
    List<Owner> getOwnersByRole();
}
