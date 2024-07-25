package com.cloudsuites.framework.services.property.personas.service;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OwnerService {

    Owner getOwnerById(String ownerId) throws NotFoundResponseException;

    Owner updateOwner(String ownerId, Owner owner) throws NotFoundResponseException;

    void deleteOwner(String ownerId) throws NotFoundResponseException;

    List<Owner> getAllOwners() throws NotFoundResponseException;

    Owner findByEmail(String email) throws NotFoundResponseException;

    Owner findByName(String email) throws NotFoundResponseException;

    Owner findByUserId(String userId) throws NotFoundResponseException;

    Owner creatOwner(Owner owner, String buildingId, String unitId) throws NotFoundResponseException;

    void createOrUpdateOwner(Tenant tenant) throws NotFoundResponseException;

}
