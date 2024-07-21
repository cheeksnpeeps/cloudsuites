package com.cloudsuites.framework.webapp.authentication.providers;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.TenantService;
import com.cloudsuites.framework.services.property.entities.Tenant;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class TenantAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenProvider jwtTokenProvider;
    private final TenantService tenantService;
    private final UserService userService;

    @Autowired
    public TenantAuthenticationProvider(JwtTokenProvider jwtTokenProvider, TenantService tenantService, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tenantService = tenantService;
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String jwtToken = authentication.getCredentials().toString();
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new BadCredentialsException("Invalid or expired JWT Token");
        }
        Claims claims = jwtTokenProvider.extractAllClaims(jwtToken);
        Long tenantId = claims.get("personaId", Long.class);
        String buildingId = claims.get("buildingId", String.class);
        Long unitId = claims.get("unitId", Long.class);

        Identity identity = userService.getUserById(claims.get("userId", Long.class));
        Tenant tenant = null;
        try {
            tenant = tenantService.getTenantByBuildingIdAndUnitIdAndTenantId(buildingId, unitId, tenantId);
        } catch (NotFoundResponseException e) {
            throw new BadCredentialsException("Invalid or expired JWT Token");
        }
        if (!tenant.getIdentity().getUserId().equals(identity.getUserId())) {
            throw new BadCredentialsException("Invalid or expired JWT Token");
        }
        return new UsernamePasswordAuthenticationToken(tenant, null, tenant.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
