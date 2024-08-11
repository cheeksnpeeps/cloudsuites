package com.cloudsuites.framework.services.property.personas.service.role;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Staff;
import com.cloudsuites.framework.services.property.personas.entities.StaffRole;
import com.cloudsuites.framework.services.property.personas.entities.StaffStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface StaffRoleService {

    @Transactional
    Staff getStaffRole(String staffId) throws NotFoundResponseException;

    @Transactional
    Staff updateStaffRole(String staffId, StaffRole staffRole) throws NotFoundResponseException;

    @Transactional
    void deleteStaffRole(String staffId) throws NotFoundResponseException;

    @Transactional
    List<Staff> getStaffByRole(StaffRole staffRole);

    @Transactional
    List<Staff> getStaffByRoleAndStatus(StaffRole staffRole, StaffStatus status);

    @Transactional
    List<Staff> getStaffByRole();
}

