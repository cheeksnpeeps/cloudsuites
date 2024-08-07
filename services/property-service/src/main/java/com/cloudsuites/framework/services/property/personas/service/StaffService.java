package com.cloudsuites.framework.services.property.personas.service;

import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UsernameAlreadyExistsException;
import com.cloudsuites.framework.services.property.personas.entities.Staff;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StaffService {

    @Transactional(readOnly = true)
    Staff getStaffById(String staffId) throws NotFoundResponseException;

    @Transactional
    Staff updateStaff(String staffId, Staff staff) throws NotFoundResponseException;

    @Transactional(readOnly = true)
    List<Staff> getAllStaffsByBuilding(String buildingId) throws NotFoundResponseException;

    @Transactional
    Staff createStaff(Staff staff, String companyId, String buildingId) throws NotFoundResponseException, UsernameAlreadyExistsException, InvalidOperationException;

    @Transactional(readOnly = true)
    Staff findByUserId(String userId) throws NotFoundResponseException;

    @Transactional(readOnly = true)
    List<Staff> getAllStaffByCompany(String companyId) throws NotFoundResponseException;

    @Transactional
    void deleteStaff(String staffId) throws NotFoundResponseException;

    @Transactional(readOnly = true)
    boolean hasAccessToBuilding(String username, String buildingID) throws NotFoundResponseException;
}
