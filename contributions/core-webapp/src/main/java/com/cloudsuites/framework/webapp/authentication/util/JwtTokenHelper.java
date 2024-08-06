package com.cloudsuites.framework.webapp.authentication.util;

import com.cloudsuites.framework.modules.jwt.JwtTokenProvider;
import com.cloudsuites.framework.services.user.entities.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenHelper {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenHelper(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String generateToken(String personaId, UserType personaType, String buildingId, String unitId, String userId) {
        JwtBuilder claims = createClaims(personaId, buildingId, unitId, userId, personaType);
        return jwtTokenProvider.generateToken(claims);
    }

    public String generateRefreshToken(String personaId, UserType personaType, String buildingId, String unitId, String userId) {
        JwtBuilder claims = createClaims(personaId, buildingId, unitId, userId, personaType);
        return jwtTokenProvider.generateRefreshToken(claims);
    }

    public boolean validateTokenClaims(Claims claims, String buildingId, String unitId, String personaId) {
        String ownerIdClaim = claims.get(WebAppConstants.Claim.PERSONA_ID, String.class);
        if (!personaId.equals(ownerIdClaim)) {
            return false;
        }
        if (claims.get(WebAppConstants.Claim.TYPE, UserType.class) == UserType.ADMIN) {
            return true;
        } else if (claims.get(WebAppConstants.Claim.TYPE, UserType.class) == UserType.TENANT
                || claims.get(WebAppConstants.Claim.TYPE, UserType.class) == UserType.OWNER) {
            String buildingIdClaim = claims.get(WebAppConstants.Claim.BUILDING_ID, String.class);
            String unitIdClaim = claims.get(WebAppConstants.Claim.UNIT_ID, String.class);
            return buildingIdClaim.equals(buildingId) && unitIdClaim.equals(unitId);
        } else if (claims.get(WebAppConstants.Claim.TYPE, UserType.class) == UserType.STAFF) {
            if (buildingId != null) {
                String buildingIdClaim = claims.get(WebAppConstants.Claim.BUILDING_ID, String.class);
                return buildingIdClaim.equals(buildingId);
            } else {
                return true;
            }
        }
        return false;
    }

    private JwtBuilder createClaims(String personaId, String buildingId, String unitId, String userId, UserType personaType) {
        return Jwts.builder()
                .subject(personaId)
                .audience().add(WebAppConstants.Claim.AUDIENCE)
                .and()
                .claim(WebAppConstants.Claim.TYPE, personaType)
                .claim(WebAppConstants.Claim.BUILDING_ID, buildingId)
                .claim(WebAppConstants.Claim.UNIT_ID, unitId)
                .claim(WebAppConstants.Claim.USER_ID, userId);
    }

    public String generateToken(String adminId, UserType userType, String userId) {
        JwtBuilder claims = createClaims(adminId, userId, userType);
        return jwtTokenProvider.generateToken(claims);
    }

    private JwtBuilder createClaims(String adminId, String userId, UserType userType) {
        return Jwts.builder()
                .subject(adminId)
                .audience().add(WebAppConstants.Claim.AUDIENCE)
                .and()
                .claim(WebAppConstants.Claim.TYPE, userType)
                .claim(WebAppConstants.Claim.USER_ID, userId);
    }

    public String generateRefreshToken(String adminId, UserType userType, String userId) {
        JwtBuilder claims = createClaims(adminId, userId, userType);
        return jwtTokenProvider.generateRefreshToken(claims);
    }

    public boolean validateTokenClaims(Claims claims, String adminId) {
        return validateTokenClaims(claims, null, null, adminId);
    }
}
