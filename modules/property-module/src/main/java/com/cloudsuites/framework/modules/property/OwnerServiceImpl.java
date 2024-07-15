package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.OwnerService;
import com.cloudsuites.framework.services.property.entities.Owner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OwnerServiceImpl implements OwnerService {
    @Override
    public Owner getOwnerById(Long id) throws NotFoundResponseException {
        return null;
    }

    @Override
    public Owner createOwner(Owner owner) {
        return null;
    }

    @Override
    public Owner updateOwner(Long id, Owner owner) throws NotFoundResponseException {
        return null;
    }

    @Override
    public void deleteOwner(Long id) throws NotFoundResponseException {

    }

    @Override
    public List<Owner> getAllOwners() throws NotFoundResponseException {
        return null;
    }

    @Override
    public Owner findByEmail(String email) throws NotFoundResponseException {
        return null;
    }

    @Override
    public Owner findByUserId(Long userId) throws NotFoundResponseException {
        return null;
    }
}
