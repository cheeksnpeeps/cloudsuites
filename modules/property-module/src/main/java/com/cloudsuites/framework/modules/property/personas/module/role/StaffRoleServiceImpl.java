package com.cloudsuites.framework.modules.property.personas.module.role;

import com.cloudsuites.framework.modules.property.personas.repository.StaffRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Staff;
import com.cloudsuites.framework.services.property.personas.entities.StaffRole;
import com.cloudsuites.framework.services.property.personas.entities.StaffStatus;
import com.cloudsuites.framework.services.property.personas.service.role.StaffRoleService;
import com.cloudsuites.framework.services.user.entities.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StaffRoleServiceImpl implements StaffRoleService {

    private final StaffRepository staffRepository;
    private final UserRoleRepository userRoleRepository;

    Logger logger = LoggerFactory.getLogger(StaffRoleServiceImpl.class);

    public StaffRoleServiceImpl(StaffRepository staffRepository, UserRoleRepository userRoleRepository) {
        this.staffRepository = staffRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Staff getStaffRole(String staffId) throws NotFoundResponseException {
        logger.debug("Fetching staff role for staffId: {}", staffId);
        Staff staff = staffRepository.findById(staffId).orElseThrow(() -> {
            logger.warn("Staff not found for staffId: {}", staffId);
            return new NotFoundResponseException("Staff not found");
        });
        if (staff.getRole() == null) {
            staff.setRole(StaffRole.DEFAULT);
        }
        logger.info("Staff found: {}", staff);
        cleanupStaffRoles(List.of(staff));
        return staff;
    }

    @Override
    public Staff updateStaffRole(String staffId, StaffRole staffRole) throws NotFoundResponseException {
        logger.debug("Updating staff role for staffId: {} with role: {}", staffId, staffRole);
        Staff staff = staffRepository.findById(staffId).orElseThrow(() -> {
            logger.warn("Staff not found for staffId: {}", staffId);
            return new NotFoundResponseException("Staff not found");
        });
        staff.setRole(staffRole);
        logger.debug("Staff found: {}", staff);
        cleanupStaffRoles(List.of(staff));
        logger.info("Staff role updated for staffId: {}", staffId);
        return staffRepository.save(staff);
    }

    @Override
    public void deleteStaffRole(String staffId) throws NotFoundResponseException {
        logger.debug("Deleting staff role for staffId: {}", staffId);
        Staff staff = staffRepository.findById(staffId).orElseThrow(() -> {
            logger.warn("Staff not found for staffId: {}", staffId);
            return new NotFoundResponseException("Staff not found");
        });
        staff.setRole(StaffRole.DELETED);
        logger.info("Staff role set to DELETED for staffId: {}", staffId);
        cleanupStaffRoles(List.of(staff));
        staffRepository.save(staff);
    }

    @Override
    public List<Staff> getStaffByRole(StaffRole staffRole) {
        logger.debug("Fetching staff by role: {}", staffRole);
        List<Staff> staffList = staffRepository.findByRole(staffRole);
        staffList.forEach(staff -> {
            if (staff.getRole() == null) {
                staff.setRole(StaffRole.DEFAULT);
            }
        });
        cleanupStaffRoles(staffList);
        logger.info("Found {} staff for role: {}", staffList.size(), staffRole);
        return staffList;
    }

    @Override
    public List<Staff> getStaffByRoleAndStatus(StaffRole staffRole, StaffStatus status) {
        logger.debug("Fetching staff by role: {} and status: {}", staffRole, status);
        List<Staff> staffList = staffRepository.findByRoleAndStatus(staffRole, status);
        staffList.forEach(staff -> {
            if (staff.getRole() == null) {
                staff.setRole(StaffRole.DEFAULT);
            }
        });
        cleanupStaffRoles(staffList);
        logger.info("Found {} staff for role: {} and status: {}", staffList.size(), staffRole, status);
        return staffList;
    }

    @Override
    public List<Staff> getStaffByRole() {
        logger.debug("Fetching all staff");
        List<Staff> staffList = staffRepository.findAll();
        staffList.forEach(staff -> {
            if (staff.getRole() == null) {
                staff.setRole(StaffRole.DEFAULT);
            }
        });
        cleanupStaffRoles(staffList);
        logger.info("Found {} staff", staffList.size());
        return staffList;
    }

    private void cleanupStaffRoles(List<Staff> staffList) {
        logger.debug("Cleaning up staff roles for {} staff", staffList.size());
        staffList.forEach(staff -> {
            List<UserRole> roles = userRoleRepository.findUserRoleByPersonaId(staff.getStaffId());
            logger.debug("StaffId: {} has {} roles", staff.getStaffId(), roles.size());
            if (roles.size() > 1) {
                logger.debug("Deleting all roles for staffId: {}", staff.getStaffId());
                userRoleRepository.deleteAll(roles);
                logger.info("Deleted all roles for staffId: {}", staff.getStaffId());
            } else if (roles.size() == 1 && roles.get(0).getRole() != null && !roles.get(0).getRole().equals(staff.getRole().name())) {
                logger.debug("Deleting role for staffId: {} and saving new role", staff.getStaffId());
                userRoleRepository.delete(roles.get(0));
                userRoleRepository.save(staff.getUserRole());
                logger.info("Deleted role for staffId: {} and saved new role", staff.getStaffId());
            } else if (roles.isEmpty()) {
                logger.debug("No roles found for staffId: {} saving new role", staff.getStaffId());
                userRoleRepository.save(staff.getUserRole());
                logger.info("Saved role for staffId: {} as no previous roles existed", staff.getStaffId());
            }
        });
    }
}

