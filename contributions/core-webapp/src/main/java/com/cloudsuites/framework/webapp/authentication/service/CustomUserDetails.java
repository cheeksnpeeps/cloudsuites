package com.cloudsuites.framework.webapp.authentication.service;

import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.services.user.entities.UserRole;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CustomUserDetails implements UserDetails {

    private List<GrantedAuthority> authorities;

    private Identity identity;

    private String personaId;

    private String userType;

    private String unitId;

    private String buildingId;

    public CustomUserDetails(Identity identity, List<UserRole> roles) {
        this.identity = identity;
        setAuthorities(roles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<UserRole> roles) {
        this.authorities = roles.stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole()))
                .collect(Collectors.toList());
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
}
