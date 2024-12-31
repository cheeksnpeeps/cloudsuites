package com.cloudsuites.framework.modules.user;

import com.cloudsuites.framework.modules.user.repository.AdminRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UserAlreadyExistsException;
import com.cloudsuites.framework.services.user.AdminService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Admin;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.services.user.entities.IdentityConstants;
import com.cloudsuites.framework.services.user.entities.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    private final AdminRepository adminRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserService userService;

    public AdminServiceImpl(AdminRepository adminRepository, UserService userService, UserRoleRepository userRoleRepository) {
        this.adminRepository = adminRepository;
        this.userService = userService;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Admin getAdminById(String adminId) throws NotFoundResponseException {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> {
                    logger.error(IdentityConstants.Admin.LOG_ADMIN_NOT_FOUND, adminId);
                    return new NotFoundResponseException("Admin not found with ID: " + adminId);
                });
    }

    @Override
    public Admin createAdmin(Admin admin) throws UserAlreadyExistsException, InvalidOperationException {
        logger.info(IdentityConstants.Admin.LOG_CREATING_ADMIN, admin);
        Admin savedIdentity = createIdentiy(admin);
        Admin savedAdmin = adminRepository.save(savedIdentity);
        logger.info(IdentityConstants.Admin.LOG_ADMIN_CREATED, savedAdmin);
        UserRole userRole = userRoleRepository.save(savedAdmin.getUserRole());
        logger.debug("User role created: {} - {}", userRole.getPersonaId(), userRole.getRole());
        return savedAdmin;
    }

    @Override
    public Admin updateAdmin(String adminId, Admin admin) throws NotFoundResponseException {
        // check if admin exists
        Admin savedAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> {
                    logger.error(IdentityConstants.Admin.LOG_ADMIN_NOT_FOUND, adminId);
                    return new NotFoundResponseException("Admin not found with ID: " + adminId);
                });

        logger.info(IdentityConstants.Admin.LOG_UPDATING_ADMIN, adminId);
        savedAdmin.getIdentity().updateIdentity(admin.getIdentity());
        userService.updateUser(savedAdmin.getIdentity().getUserId(), savedAdmin.getIdentity());
        savedAdmin.updateAdmin(admin);
        Admin updatedAdmin = adminRepository.save(savedAdmin);
        logger.info(IdentityConstants.Admin.LOG_ADMIN_UPDATED, updatedAdmin.getAdminId());
        return updatedAdmin;
    }

    @Override
    public void deleteAdmin(String adminId) throws NotFoundResponseException {
        logger.info(IdentityConstants.Admin.LOG_DELETING_ADMIN, adminId);
        Admin existingAdmin = getAdminById(adminId);
        userRoleRepository.findByPersonaId(adminId).stream().findAny()
                .ifPresent(userRoleRepository::delete);
        logger.info(IdentityConstants.Admin.LOG_ROLE_DELETED, adminId);
        adminRepository.delete(existingAdmin);
        logger.info(IdentityConstants.Admin.LOG_ADMIN_DELETED, adminId);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Admin findByEmail(String email) throws NotFoundResponseException {
        return adminRepository.findByIdentity_Email(email)
                .orElseThrow(() -> {
                    logger.error(IdentityConstants.Admin.LOG_ADMIN_NOT_FOUND_EMAIL, email);
                    return new NotFoundResponseException("Admin not found with email: " + email);
                });
    }

    @Override
    public Admin findByName(String firstName) throws NotFoundResponseException {
        return adminRepository.findByIdentity_FirstName(firstName)
                .orElseThrow(() -> {
                    logger.error(IdentityConstants.Admin.LOG_ADMIN_NOT_FOUND_NAME, firstName);
                    return new NotFoundResponseException("Admin not found with name: " + firstName);
                });
    }

    @Override
    public Admin findByUserId(String userId) throws NotFoundResponseException {
        return adminRepository.findByIdentity_UserId(userId)
                .orElseThrow(() -> {
                    logger.error(IdentityConstants.Admin.LOG_ADMIN_NOT_FOUND_USER_ID, userId);
                    return new NotFoundResponseException("Admin not found for User ID: " + userId);
                });
    }

    private Admin createIdentiy(Admin admin) throws UserAlreadyExistsException, InvalidOperationException {
        // Log the start of the tenant creation process
        logger.debug("Starting admin creation process for admin: {}", admin);
        // Step 1: Create and save the identity for the tenant
        Identity identity = admin.getIdentity();
        if (identity == null) {
            logger.error("Identity not found for admin: {}", admin);
            throw new InvalidOperationException("Identity not found for admin: " + admin);
        }
        logger.debug("Creating identity for admin with email: {}", identity.getEmail());
        if (!StringUtils.hasText(identity.getEmail())) {
            logger.error("Email is empty for admin: {}", admin);
            throw new InvalidOperationException("Email is required");
        }
        if (userService.existsByEmail(identity.getEmail())) {
            logger.error("User already exists with email: {}", identity.getEmail());
            throw new UserAlreadyExistsException("User already exists with email: " + identity.getEmail());
        }
        Identity savedIdentity = userService.createUser(identity);
        admin.setIdentity(savedIdentity);
        logger.debug("Identity created and saved: {}", savedIdentity.getUserId());
        return admin;
    }
}
