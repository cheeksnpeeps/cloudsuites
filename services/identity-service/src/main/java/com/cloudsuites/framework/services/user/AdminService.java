package com.cloudsuites.framework.services.user;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.user.entities.Admin;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminService {

    Admin getAdminById(Long id) throws NotFoundResponseException;

    Admin createAdmin(Admin admin);

    Admin updateAdmin(Long id, Admin admin) throws NotFoundResponseException;

    void deleteAdmin(Long id) throws NotFoundResponseException;

    List<Admin> getAllAdmins() throws NotFoundResponseException;

    Admin findByEmail(String email) throws NotFoundResponseException;

    Admin findByName(String email) throws NotFoundResponseException;

    Admin findByUserId(Long userId) throws NotFoundResponseException;
}
