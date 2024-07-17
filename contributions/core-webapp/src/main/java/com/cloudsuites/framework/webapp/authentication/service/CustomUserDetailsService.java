package com.cloudsuites.framework.webapp.authentication.service;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.ManagerService;
import com.cloudsuites.framework.services.property.OwnerService;
import com.cloudsuites.framework.services.property.TenantService;
import com.cloudsuites.framework.services.property.entities.Manager;
import com.cloudsuites.framework.services.property.entities.Owner;
import com.cloudsuites.framework.services.property.entities.Tenant;
import com.cloudsuites.framework.services.property.entities.UserType;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.UserPrincipal;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private final TenantService tenantService;
    private final OwnerService ownerService;
    private final ManagerService managerService;

    @Autowired
    public CustomUserDetailsService(UserService userService, TenantService tenantService,
                                    OwnerService ownerService, ManagerService managerService) {
        this.userService = userService;
        this.tenantService = tenantService;
        this.ownerService = ownerService;
        this.managerService = managerService;
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

        addTenantDetails(identity.getUserId(), authorities, personaIds);
        addOwnerDetails(identity.getUserId(), authorities, personaIds);
        addManagerDetails(identity.getUserId(), authorities, personaIds);

        if (authorities.isEmpty()) {
            throw new UsernameNotFoundException("Identity has no roles assigned: " + username);
        }

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

    @SneakyThrows
    private void addTenantDetails(Long userId, List<GrantedAuthority> authorities, Map<String, Long> personaIds) {
        Tenant tenant = tenantService.findByUserId(userId);
        if (tenant != null) {
            authorities.add(new SimpleGrantedAuthority(UserType.TENANT.name()));
            personaIds.put(UserType.TENANT.name(), tenant.getTenantId());
        }
    }

    @SneakyThrows
    private void addOwnerDetails(Long userId, List<GrantedAuthority> authorities, Map<String, Long> personaIds) {
        Owner owner = ownerService.findByUserId(userId);
        if (owner != null) {
            authorities.add(new SimpleGrantedAuthority(UserType.OWNER.name()));
            personaIds.put(UserType.OWNER.name(), owner.getOwnerId());
        }
    }

    @SneakyThrows
    private void addManagerDetails(Long userId, List<GrantedAuthority> authorities, Map<String, Long> personaIds) {
        Manager manager = managerService.findByUserId(userId);
        if (manager != null) {
            authorities.add(new SimpleGrantedAuthority(UserType.MANAGER.name()));
            personaIds.put(UserType.MANAGER.name(), manager.getManagerId());
        }
    }
}
