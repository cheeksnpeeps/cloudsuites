package com.cloudsuites.framework.modules.user;

import com.cloudsuites.framework.modules.user.repository.AdminRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.user.AdminRoleService;
import com.cloudsuites.framework.services.user.entities.Admin;
import com.cloudsuites.framework.services.user.entities.AdminRole;
import com.cloudsuites.framework.services.user.entities.AdminStatus;
import com.cloudsuites.framework.services.user.entities.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminRoleServiceImpl implements AdminRoleService {

    private final AdminRepository adminRepository;
    private final UserRoleRepository userRoleRepository;

    Logger logger = LoggerFactory.getLogger(AdminRoleServiceImpl.class);

    public AdminRoleServiceImpl(AdminRepository adminRepository, UserRoleRepository userRoleRepository) {
        this.adminRepository = adminRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Admin getAdminRole(String adminId) throws NotFoundResponseException {
        logger.debug("Fetching admin role for adminId: {}", adminId);
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> {
            logger.warn("Admin not found for adminId: {}", adminId);
            return new NotFoundResponseException("Admin not found");
        });
        logger.info("Admin found: {}", admin);
        cleaupAdminRoles(List.of(admin));
        return admin;
    }

    @Override
    public Admin updateAdminRole(String adminId, AdminRole adminRole) throws NotFoundResponseException {
        logger.debug("Updating admin role for adminId: {} with role: {}", adminId, adminRole);
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> {
            logger.warn("Admin not found for adminId: {}", adminId);
            return new NotFoundResponseException("Admin not found");
        });
        admin.setRole(adminRole);
        logger.info("Admin role updated for adminId: {}", adminId);
        cleaupAdminRoles(List.of(admin));
        userRoleRepository.save(admin.getUserRole());
        return adminRepository.save(admin);
    }

    @Override
    public void deleteAdminRole(String adminId) throws NotFoundResponseException {
        logger.debug("Deleting admin role for adminId: {}", adminId);
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> {
            logger.warn("Admin not found for adminId: {}", adminId);
            return new NotFoundResponseException("Admin not found");
        });
        admin.setRole(AdminRole.DELETED);
        logger.info("Admin role set to DELETED for adminId: {}", adminId);
        cleaupAdminRoles(List.of(admin));
        adminRepository.save(admin);
    }

    @Override
    public List<Admin> getAdminsByRole(AdminRole adminRole) {
        logger.debug("Fetching admins by role: {}", adminRole);
        List<Admin> admins = adminRepository.findByRole(adminRole);
        cleaupAdminRoles(admins);
        logger.info("Found {} admins for role: {}", admins.size(), adminRole);
        return admins;
    }

    @Override
    public List<Admin> getAdminsByRoleAndStatus(AdminRole adminRole, AdminStatus status) {
        logger.debug("Fetching admins by role: {} and status: {}", adminRole, status);
        List<Admin> admins = adminRepository.findByRoleAndStatus(adminRole, status);
        cleaupAdminRoles(admins);
        logger.info("Found {} admins for role: {} and status: {}", admins.size(), adminRole, status);
        return admins;
    }

    @Override
    public List<Admin> getAdminsByRole() {
        logger.debug("Fetching all admins");
        List<Admin> admins = adminRepository.findAll();
        cleaupAdminRoles(admins);
        logger.info("Found {} admins", admins.size());
        return admins;
    }

    private void cleaupAdminRoles(List<Admin> admins) {
        logger.debug("Cleaning up admin roles for {} admins", admins.size());
        admins.forEach(admin -> {
            List<UserRole> roles = userRoleRepository.findUserRoleByPersonaId(admin.getAdminId());
            logger.debug("AdminId: {} has {} roles", admin.getAdminId(), roles.size());
            if (roles.size() > 1) {
                userRoleRepository.deleteAll(roles);
                logger.info("Deleted all roles for adminId: {}", admin.getAdminId());
            } else if (roles.size() == 1 && !roles.get(0).getRole().equals(admin.getRole().name())) {
                userRoleRepository.delete(roles.get(0));
                userRoleRepository.save(admin.getUserRole());
                logger.info("Deleted role for adminId: {} and saved new role", admin.getAdminId());
            } else if (roles.isEmpty()) {
                userRoleRepository.save(admin.getUserRole());
                logger.info("Saved role for adminId: {} as no previous roles existed", admin.getAdminId());
            }
        });
    }
}