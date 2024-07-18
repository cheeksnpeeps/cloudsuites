package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.StaffRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.StaffService;
import com.cloudsuites.framework.services.property.entities.Staff;
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
    public Staff findByUserId(Long userId) throws NotFoundResponseException {
        return staffRepository.findByIdentity_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("Staff not found for user ID: {}", userId);
                    return new NotFoundResponseException("Staff not found for User ID: " + userId);
                });
    }

    @Override
    public Staff getStaffById(Long staffId) throws NotFoundResponseException {
        logger.info("Fetching staff with ID: {}", staffId);
        return staffRepository.findById(staffId)
                .orElseThrow(() -> {
                    logger.error("Staff not found with ID: {}", staffId);
                    return new NotFoundResponseException("Staff not found with ID: " + staffId);
                });
    }

    @Override
    public Staff updateStaff(Long staffId, Staff staff) throws NotFoundResponseException {
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
    public List<Staff> getAllStaffsByBuilding(Long buildingId) throws NotFoundResponseException {
        logger.info("Fetching all staffs for building ID: {}", buildingId);
        return staffRepository.findByBuildingId(buildingId)
                .orElseThrow(() -> {
                    logger.error("No staffs found for building ID: {}", buildingId);
                    return new NotFoundResponseException("No staffs found for Building ID: " + buildingId);
                });
    }
}