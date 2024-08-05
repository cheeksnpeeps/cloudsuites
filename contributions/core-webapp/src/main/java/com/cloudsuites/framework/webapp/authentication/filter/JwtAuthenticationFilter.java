package com.cloudsuites.framework.webapp.authentication.filter;

import com.cloudsuites.framework.modules.jwt.JwtTokenProvider;
import com.cloudsuites.framework.services.user.UserRoleRepository;
import com.cloudsuites.framework.services.user.entities.UserRole;
import com.cloudsuites.framework.services.user.entities.UserType;
import com.cloudsuites.framework.webapp.authentication.service.CustomUserDetails;
import com.cloudsuites.framework.webapp.authentication.service.CustomUserDetailsService;
import com.cloudsuites.framework.webapp.authentication.util.WebAppConstants;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final UserRoleRepository userRoleRepository;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService, UserRoleRepository userRoleRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);

        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
            Claims claims = jwtTokenProvider.extractAllClaims(jwt);
            String userId = claims.get(WebAppConstants.Claim.USER_ID, String.class);
            String personaId = claims.get(WebAppConstants.Claim.PERSONA_ID, String.class);
            UserType userType = claims.get(WebAppConstants.Claim.TYPE, UserType.class);

            // Validate claims
            if (userId == null || personaId == null || userType == null) {
                logger.error("Missing claims in JWT");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }

            CustomUserDetails userDetails;
            try {
                userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(userId);
            } catch (UsernameNotFoundException e) {
                logger.error("User not found: {}", e); // Corrected logging statement
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }

            if (userDetails != null) {
                List<UserRole> roles = userRoleRepository.findUserRoleByIdentity_UserIdAndAndPersonaIdAndUserType(
                        userId, personaId, userType
                );
                if (roles.isEmpty()) {
                    logger.warn("User does not have any roles assigned");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    return; // Return early to avoid setting authentication
                }
                userDetails.setAuthorities(roles); // Only set authorities if roles are found
                logger.debug("User roles loaded");
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }


    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
