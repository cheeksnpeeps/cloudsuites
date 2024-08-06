package com.cloudsuites.framework.webapp.authentication.service;

import com.cloudsuites.framework.modules.user.UserRoleRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.services.user.entities.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public CustomUserDetailsService(UserService userService, UserRoleRepository userRoleRepository) {
        this.userService = userService;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Identity identity = null;
        try {
            identity = findUser(username);
        } catch (NotFoundResponseException e) {
            throw new UsernameNotFoundException("Identity not found: " + username);
        }
        List<UserRole> roles = userRoleRepository.findUserRoleByIdentityId(identity.getUserId());
        return new CustomUserDetails(identity, roles);
    }

    private Identity findUser(String username) throws NotFoundResponseException {
        Identity identity = userService.getUserById(username);
        if (identity == null) {
            identity = userService.findByPhoneNumber(username);
        }
        if (identity == null) {
            throw new NotFoundResponseException("Identity not found with username: " + username);
        }
        return identity;
    }
}
