package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.entities.Staff;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StaffService {

    Staff getStaffById(Long staffId) throws NotFoundResponseException;

    Staff updateStaff(Long staffId, Staff staff) throws NotFoundResponseException;

    List<Staff> getAllStaffsByBuilding(Long buildingId) throws NotFoundResponseException;

    Staff createStaff(Staff staff) throws NotFoundResponseException;

    Staff findByUserId(Long userId) throws NotFoundResponseException;

    @Transactional(readOnly = true)
    List<Staff> getAllStaffByCompany(Long companyId) throws NotFoundResponseException;

    void deleteStaff(Long id) throws NotFoundResponseException;
}
