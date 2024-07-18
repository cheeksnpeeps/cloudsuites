package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.entities.Owner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OwnerService {

    Owner getOwnerById(Long id) throws NotFoundResponseException;

    Owner createOwner(Owner owner);

    Owner updateOwner(Long id, Owner owner) throws NotFoundResponseException;

    void deleteOwner(Long id) throws NotFoundResponseException;

    List<Owner> getAllOwners() throws NotFoundResponseException;

    Owner findByEmail(String email) throws NotFoundResponseException;

    Owner findByName(String email) throws NotFoundResponseException;

    Owner findByUserId(Long userId) throws NotFoundResponseException;
}
