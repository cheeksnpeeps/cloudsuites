package com.cloudsuites.framework.modules.user;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.user.AdminService;
import com.cloudsuites.framework.services.user.entities.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Admin getAdminById(Long id) throws NotFoundResponseException {
        return adminRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Admin not found with ID: {}", id);
                    return new NotFoundResponseException("Admin not found with ID: " + id);
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
    public Admin updateAdmin(Long id, Admin admin) throws NotFoundResponseException {
        logger.info("Updating admin with ID: {}", id);
        Admin updatedAdmin = adminRepository.save(admin);
        logger.info("Admin updated successfully with ID: {}", updatedAdmin.getAdminId());
        return updatedAdmin;
    }

    @Override
    public void deleteAdmin(Long id) throws NotFoundResponseException {
        logger.info("Deleting admin with ID: {}", id);
        Admin existingAdmin = getAdminById(id);
        adminRepository.delete(existingAdmin);
        logger.info("Admin deleted successfully with ID: {}", id);
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
    public Admin findByUserId(Long userId) throws NotFoundResponseException {
        return adminRepository.findByIdentity_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("Admin not found for user ID: {}", userId);
                    return new NotFoundResponseException("Admin not found for User ID: " + userId);
                });
    }
}
