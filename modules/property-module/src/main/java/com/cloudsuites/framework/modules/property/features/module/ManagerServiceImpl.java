package com.cloudsuites.framework.modules.property.features.module;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.service.ManagerService;
import com.cloudsuites.framework.services.property.personas.entities.Manager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ManagerServiceImpl implements ManagerService {
    @Override
    public Manager getManagerById(Long id) throws NotFoundResponseException {
        return null;
    }

    @Override
    public Manager createManager(Manager manager) {
        return null;
    }

    @Override
    public Manager updateManager(Long id, Manager manager) throws NotFoundResponseException {
        return null;
    }

    @Override
    public void deleteManager(Long id) throws NotFoundResponseException {

    }

    @Override
    public List<Manager> getAllManagers() throws NotFoundResponseException {
        return null;
    }

    @Override
    public Manager findByEmail(String email) {
        return null;
    }

    @Override
    public Manager findByUserId(Long userId) throws NotFoundResponseException {
        return null;
    }
}
