package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.entities.Manager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ManagerService {
    Manager getManagerById(Long id) throws NotFoundResponseException;

    Manager createManager(Manager manager);

    Manager updateManager(Long id, Manager manager) throws NotFoundResponseException;

    void deleteManager(Long id) throws NotFoundResponseException;

    List<Manager> getAllManagers() throws NotFoundResponseException;

    Manager findByEmail(String email);

    Manager findByUserId(Long userId) throws NotFoundResponseException;
}
