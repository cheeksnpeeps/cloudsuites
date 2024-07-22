package com.cloudsuites.framework.modules.user;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.user.AdminService;
import com.cloudsuites.framework.services.user.entities.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Admin getAdminById(String adminId) throws NotFoundResponseException {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> {
                    logger.error("Admin not found with ID: {}", adminId);
                    return new NotFoundResponseException("Admin not found with ID: " + adminId);
                });
    }

    @Override
    public Admin createAdmin(Admin admin) {
        logger.info("Creating new admin: {}", admin);
        Admin savedAdmin = adminRepository.save(admin);
        logger.info("Admin created successfully with ID: {}", savedAdmin.getAdminId());
        return savedAdmin;
    }

    @Override
    public Admin updateAdmin(String adminId, Admin admin) throws NotFoundResponseException {
        logger.info("Updating admin with ID: {}", adminId);
        Admin updatedAdmin = adminRepository.save(admin);
        logger.info("Admin updated successfully with ID: {}", updatedAdmin.getAdminId());
        return updatedAdmin;
    }

    @Override
    public void deleteAdmin(String adminId) throws NotFoundResponseException {
        logger.info("Deleting admin with ID: {}", adminId);
        Admin existingAdmin = getAdminById(adminId);
        adminRepository.delete(existingAdmin);
        logger.info("Admin deleted successfully with ID: {}", adminId);
    }

    @Override
    public List<Admin> getAllAdmins() throws NotFoundResponseException {
        List<Admin> admins = adminRepository.findAll();
        if (admins.isEmpty()) {
            logger.error("No admins found");
            throw new NotFoundResponseException("No admins found");
        }
        return admins;
    }

    @Override
    public Admin findByEmail(String email) throws NotFoundResponseException {
        return adminRepository.findByIdentity_Email(email)
                .orElseThrow(() -> {
                    logger.error("Admin not found with email: {}", email);
                    return new NotFoundResponseException("Admin not found with email: " + email);
                });
    }

    @Override
    public Admin findByName(String firstName) throws NotFoundResponseException {
        return adminRepository.findByIdentity_FirstName(firstName)
                .orElseThrow(() -> {
                    logger.error("Admin not found with name: {}", firstName);
                    return new NotFoundResponseException("Admin not found with name: " + firstName);
                });
    }

    @Override
    public Admin findByUserId(String userId) throws NotFoundResponseException {
        return adminRepository.findByIdentity_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("Admin not found for user ID: {}", userId);
                    return new NotFoundResponseException("Admin not found for User ID: " + userId);
                });
    }
}
