package com.cloudsuites.framework.webapp.authentication.service;

import com.cloudsuites.framework.services.property.personas.entities.StaffRole;
import com.cloudsuites.framework.services.property.personas.entities.UserType;
import com.cloudsuites.framework.services.user.entities.AdminRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final List<StaffRole> staffRoles; // Add staff roles
    private final List<AdminRole> adminRoles; // Add admin roles
    private final UserType userType; // Add user type

    public CustomUserDetails(String username, String password, UserType userType,
                             List<StaffRole> staffRoles, List<AdminRole> adminRoles) {
        this.userType = userType;
        this.staffRoles = staffRoles;
        this.adminRoles = adminRoles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Add user type authority
        authorities.add(new SimpleGrantedAuthority(userType.name()));

        // Add staff roles if present
        if (staffRoles != null) {
            staffRoles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.name())));
        }
        // Add admin roles if present
        if (adminRoles != null) {
            adminRoles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.name())));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    // Other methods...
}
