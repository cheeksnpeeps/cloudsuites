package com.cloudsuites.framework.services.user;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.user.entities.Admin;
import com.cloudsuites.framework.services.user.entities.AdminRole;
import com.cloudsuites.framework.services.user.entities.AdminStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface AdminRoleService {

    @Transactional
    Admin getAdminRole(String adminId) throws NotFoundResponseException;

    @Transactional
    Admin updateAdminRole(String adminId, AdminRole adminRole) throws NotFoundResponseException;

    @Transactional
    void deleteAdminRole(String adminId) throws NotFoundResponseException;

    @Transactional
    List<Admin> getAdminsByRole(AdminRole adminRole);

    @Transactional
    List<Admin> getAdminsByRoleAndStatus(AdminRole adminRole, AdminStatus status);

    @Transactional
    List<Admin> getAdminsByRole();
}
