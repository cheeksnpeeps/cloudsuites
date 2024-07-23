package com.cloudsuites.framework.modules.property.personas.module;

import com.cloudsuites.framework.modules.property.personas.repository.StaffRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Staff;
import com.cloudsuites.framework.services.property.personas.service.StaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {

    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);
    @Autowired
    private StaffRepository staffRepository;

    @Override
    public Staff createStaff(Staff staff) {
        logger.info("Creating new staff: {}", staff);
        Staff savedStaff = staffRepository.save(staff);
        logger.info("Staff created successfully with ID: {}", savedStaff.getStaffId());
        return savedStaff;
    }

    @Override
    public Staff findByUserId(String userId) throws NotFoundResponseException {
        return staffRepository.findByIdentity_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("Staff not found for user ID: {}", userId);
                    return new NotFoundResponseException("Staff not found for User ID: " + userId);
                });
    }

    @Override
    public Staff getStaffById(String staffId) throws NotFoundResponseException {
        logger.info("Fetching staff with ID: {}", staffId);
        return staffRepository.findById(staffId)
                .orElseThrow(() -> {
                    logger.error("Staff not found with ID: {}", staffId);
                    return new NotFoundResponseException("Staff not found with ID: " + staffId);
                });
    }

    @Override
    public Staff updateStaff(String staffId, Staff staff) throws NotFoundResponseException {
        logger.info("Updating staff with ID: {}", staffId);
        Staff existingStaff = getStaffById(staffId);
        existingStaff.setIdentity(staff.getIdentity());
        // Update other fields as necessary
        Staff updatedStaff = staffRepository.save(existingStaff);
        logger.info("Staff updated successfully with ID: {}", updatedStaff.getStaffId());
        return updatedStaff;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getAllStaffsByBuilding(String buildingId) throws NotFoundResponseException {
        logger.info("Fetching all staffs for building ID: {}", buildingId);
        return staffRepository.findByBuilding_BuildingId(buildingId)
                .orElseThrow(() -> {
                    logger.error("No staffs found for building ID: {}", buildingId);
                    return new NotFoundResponseException("No staffs found for Building ID: " + buildingId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getAllStaffByCompany(String companyId) throws NotFoundResponseException {
        logger.info("Fetching all staffs for company ID: {}", companyId);
        return staffRepository.findByCompany_CompanyId(companyId)
                .orElseThrow(() -> {
                    logger.error("No staff found for company ID: {}", companyId);
                    return new NotFoundResponseException("No staff found for company ID: " + companyId);
                });
    }

    @Override
    public void deleteStaff(String staffId) throws NotFoundResponseException {
        logger.info("Deleting staff with ID: {}", staffId);

        staffRepository.findById(staffId).ifPresent(staff -> {
            staffRepository.delete(staff);
            logger.info("Staff deleted successfully with ID: {}", staffId);
        });
    }
}