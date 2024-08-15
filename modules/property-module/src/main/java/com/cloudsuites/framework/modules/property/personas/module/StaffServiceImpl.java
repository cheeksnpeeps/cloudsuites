package com.cloudsuites.framework.modules.property.personas.module;

import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.CompanyRepository;
import com.cloudsuites.framework.modules.property.personas.repository.StaffRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UserAlreadyExistsException;
import com.cloudsuites.framework.services.property.personas.entities.Staff;
import com.cloudsuites.framework.services.property.personas.service.StaffService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.services.user.entities.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {

    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);

    private final StaffRepository staffRepository;
    private final UserService userService;
    private final UserRoleRepository userRoleRepository;
    private final CompanyRepository companyRepository;
    private final BuildingRepository buildingRepository;


    public StaffServiceImpl(StaffRepository staffRepository, UserService userService, UserRoleRepository userRoleRepository, CompanyRepository companyRepository, BuildingRepository buildingRepository) {
        this.staffRepository = staffRepository;
        this.userService = userService;
        this.userRoleRepository = userRoleRepository;
        this.companyRepository = companyRepository;
        this.buildingRepository = buildingRepository;
    }

    @Override
    public Staff createStaff(Staff staff, String companyId, String buildingId) throws UserAlreadyExistsException, InvalidOperationException, NotFoundResponseException {
        staff.setCompany(companyRepository.findById(companyId)
                .orElseThrow(() -> {
                    logger.error("Company not found with ID: {}", companyId);
                    return new NotFoundResponseException("Company not found with ID: " + companyId);
                }));

        if (buildingId != null) {
            staff.setBuilding(buildingRepository.findById(buildingId)
                    .orElseThrow(() -> {
                        logger.error("Building not found with ID: {}", buildingId);
                        return new NotFoundResponseException("Building not found with ID: " + buildingId);
                    }));
        }
        logger.info("Creating new staff: {}", staff);
        validateIdentity(staff.getStaffId(), staff.getIdentity());
        Identity savedIdentity = userService.createUser(staff.getIdentity());
        staff.setIdentity(savedIdentity);
        logger.debug("Identity created and saved: {}", savedIdentity.getUserId());

        Staff savedStaff = staffRepository.save(staff);
        logger.info("Staff created successfully with ID: {}", savedStaff.getStaffId());

        UserRole userRole = userRoleRepository.save(staff.getUserRole());
        logger.debug("User role created: {} - {}", userRole.getPersonaId(), userRole.getRole());

        return savedStaff;
    }

    private void validateIdentity(String staffId, Identity identity) throws UserAlreadyExistsException, InvalidOperationException {
        if (identity == null) {
            logger.error("Identity not found for staff: {}", staffId);
            throw new InvalidOperationException("Identity not found for staff: " + staffId);
        }
        logger.debug("Creating identity with enail: {}", identity.getEmail());
        if (!StringUtils.hasText(identity.getEmail())) {
            logger.error("Email not found for tenant: {}", staffId);
            throw new InvalidOperationException("Email is required");
        }
        if (userService.existsByEmail(identity.getEmail())) {
            logger.error("User already exists with email: {}", identity.getEmail());
            throw new UserAlreadyExistsException("User already exists with email: " + identity.getEmail());
        }
    }

    @Override
    public Staff findByUserId(String userId) throws NotFoundResponseException {
        return staffRepository.findByIdentity_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("Staff not found for user ID: {}", userId);
                    return new NotFoundResponseException("Staff not found for User ID: " + userId);
                });
    }

    @Transactional
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
        Staff existingStaff = staffRepository.findById(staffId)
                .orElseThrow(() -> {
                    logger.error("Staff not found with ID: {}", staffId);
                    return new NotFoundResponseException("Staff not found with ID: " + staffId);
                });
        existingStaff.getIdentity().updateIdentity(staff.getIdentity());
        userService.updateUser(existingStaff.getIdentity().getUserId(), existingStaff.getIdentity());

        existingStaff.updateStaff(staff);
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

    @Override
    public boolean hasAccessToBuilding(String staffId, String buildingID) throws NotFoundResponseException {
        // Check if the staff has access to the building
        logger.info("Checking if staff with ID: {} has access to building with ID: {}", staffId, buildingID);
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> {
                    logger.error("Staff not found with ID: {}", staffId);
                    return new NotFoundResponseException("Staff not found with ID: " + staffId);
                });
        if (staff.getBuilding() != null && staff.getBuilding().getBuildingId().equals(buildingID)) {
            logger.info("Staff has access to building with ID: {}", buildingID);
            return true;
        }
        logger.info("Staff does not have access to building with ID: {}", buildingID);
        return false;
    }
}