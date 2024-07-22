package com.cloudsuites.framework.services.property.personas.service;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Staff;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StaffService {

    Staff getStaffById(String staffId) throws NotFoundResponseException;

    Staff updateStaff(String staffId, Staff staff) throws NotFoundResponseException;

    List<Staff> getAllStaffsByBuilding(String buildingId) throws NotFoundResponseException;

    Staff createStaff(Staff staff) throws NotFoundResponseException;

    Staff findByUserId(String userId) throws NotFoundResponseException;

    @Transactional(readOnly = true)
    List<Staff> getAllStaffByCompany(String companyId) throws NotFoundResponseException;

    void deleteStaff(String staffId) throws NotFoundResponseException;
}
