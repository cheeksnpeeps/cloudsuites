package com.cloudsuites.framework.webapp.authentication.service;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.service.ManagerService;
import com.cloudsuites.framework.services.property.personas.service.OwnerService;
import com.cloudsuites.framework.services.property.personas.service.TenantService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public CustomUserDetailsService(UserService userService, TenantService tenantService,
                                    OwnerService ownerService, ManagerService managerService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Identity identity = null;
        try {
            identity = findUser(username);
        } catch (NotFoundResponseException e) {
            throw new UsernameNotFoundException("Identity not found: " + username);
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        Map<String, Long> personaIds = new HashMap<>();
        return UserPrincipal.create(identity, personaIds, authorities);
    }

    private Identity findUser(String username) throws NotFoundResponseException {
        Identity identity = userService.findByEmail(username);
        if (identity == null) {
            identity = userService.findByPhoneNumber(username);
        }
        if (identity == null) {
            throw new NotFoundResponseException("Identity not found with username: " + username);
        }
        return identity;
    }
}
