package com.cloudsuites.framework.modules.property.personas.module;

import com.cloudsuites.framework.modules.property.personas.repository.StaffRepository;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UsernameAlreadyExistsException;
import com.cloudsuites.framework.services.property.personas.entities.Staff;
import com.cloudsuites.framework.services.property.personas.service.StaffService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {

    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);
    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserService userService;

    @Override
    public Staff createStaff(Staff staff) throws UsernameAlreadyExistsException, InvalidOperationException {
        logger.info("Creating new staff: {}", staff);
        createIdentiy(staff);
        Staff savedStaff = staffRepository.save(staff);
        logger.info("Staff created successfully with ID: {}", savedStaff.getStaffId());
        return savedStaff;
    }

    private void createIdentiy(Staff staff) throws UsernameAlreadyExistsException, InvalidOperationException {
        // Log the start of the staff creation process
        logger.debug("Starting staff creation process for owner: {}", staff);
        // Step 1: Create and save the identity for the staff
        Identity identity = staff.getIdentity();

        if (identity == null) {
            logger.error("Identity not found for staff: {}", staff);
            throw new InvalidOperationException("Identity not found for staff: " + staff);
        }
        logger.debug("Creating identity with username: {}", identity.getUsername());
        if (!StringUtils.hasText(identity.getUsername())) {
            logger.error("Username not found for staff: {}", staff);
            throw new InvalidOperationException("Username is required");
        }
        if (userService.existsByUsername(identity.getUsername())) {
            logger.error("User already exists with username: {}", identity.getUsername());
            throw new UsernameAlreadyExistsException("User already exists with username: " + identity.getUsername());
        }
        Identity savedIdentity = userService.createUser(identity);
        staff.setIdentity(savedIdentity);
        logger.debug("Identity created and saved: {}", savedIdentity.getUserId());
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
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> {
                    logger.error("Staff found with ID: {}", staffId);
                    return new NotFoundResponseException("No staff found with ID: " + staffId);
                });
        staffRepository.delete(staff);
        logger.info("Staff deleted successfully with ID: {}", staffId);
    }
}