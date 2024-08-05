package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.services.user.entities.Identity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Map;

public class UserPrincipal implements UserDetails {

    private final Identity identity;
    private final Map<String, Long> personaIds;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Identity identity, Map<String, Long> personaIds, String username,
                         Collection<? extends GrantedAuthority> authorities) {
        this.identity = identity;
        this.personaIds = personaIds;
        this.username = username;
        this.authorities = authorities;
    }

    public static UserPrincipal create(Identity identity, Map<String, Long> personaIds, Collection<? extends GrantedAuthority> authorities) {
        return new UserPrincipal(
                identity,
                personaIds,
                identity.getPhoneNumber(),
                authorities
        );
    }

    public Map<String, Long> personaIds() {
        return personaIds;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
