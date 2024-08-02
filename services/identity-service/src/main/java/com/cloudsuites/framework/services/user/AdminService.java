package com.cloudsuites.framework.services.user;

import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UsernameAlreadyExistsException;
import com.cloudsuites.framework.services.user.entities.Admin;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface AdminService {

    @Transactional
    Admin getAdminById(String adminId) throws NotFoundResponseException;

    @Transactional
    Admin createAdmin(Admin admin) throws UsernameAlreadyExistsException, InvalidOperationException;

    @Transactional
    Admin updateAdmin(String adminId, Admin admin) throws NotFoundResponseException;

    @Transactional
    void deleteAdmin(String adminId) throws NotFoundResponseException;

    @Transactional
    List<Admin> getAllAdmins() throws NotFoundResponseException;

    @Transactional
    Admin findByEmail(String email) throws NotFoundResponseException;

    @Transactional
    Admin findByName(String email) throws NotFoundResponseException;

    @Transactional
    Admin findByUserId(String userId) throws NotFoundResponseException;
}
