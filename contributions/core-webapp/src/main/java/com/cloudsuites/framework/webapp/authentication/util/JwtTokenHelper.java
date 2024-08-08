package com.cloudsuites.framework.webapp.authentication.util;

import com.cloudsuites.framework.modules.jwt.JwtTokenProvider;
import com.cloudsuites.framework.services.user.entities.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenHelper {

    private final JwtTokenProvider jwtTokenProvider;

    Logger logger = LoggerFactory.getLogger(JwtTokenHelper.class);

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
        logger.debug("Validating token claims. {}", claims);
        logger.debug("Validating against buildingId: {}, unitId: {}, personaId: {}", buildingId, unitId, personaId);
        String ownerIdClaim = claims.getSubject();
        if (!personaId.equals(ownerIdClaim)) {
            logger.debug("Persona ID does not match.");
            return false;
        }
        if (UserType.ADMIN.name().equals(claims.get(WebAppConstants.Claim.TYPE, String.class))) {
            logger.debug("Admin token. No building or unit validation required.");
            return true;
        } else if (UserType.TENANT.name().equals(claims.get(WebAppConstants.Claim.TYPE, String.class))
                || UserType.OWNER.name().equals(claims.get(WebAppConstants.Claim.TYPE, String.class))) {
            String buildingIdClaim = claims.get(WebAppConstants.Claim.BUILDING_ID, String.class);
            String unitIdClaim = claims.get(WebAppConstants.Claim.UNIT_ID, String.class);
            logger.debug("Tenant or Owner token. Building ID: {}, Unit ID: {}", buildingIdClaim, unitIdClaim);
            return buildingIdClaim.equals(buildingId) && unitIdClaim.equals(unitId);
        } else if (UserType.STAFF.name().equals(claims.get(WebAppConstants.Claim.TYPE, String.class))) {
            if (buildingId != null) {
                String buildingIdClaim = claims.get(WebAppConstants.Claim.BUILDING_ID, String.class);
                logger.debug("Staff token. Building ID: {}", buildingIdClaim);
                return buildingIdClaim.equals(buildingId);
            } else {
                logger.debug("Staff token. No building validation required.");
                return true;
            }
        }
        logger.debug("Invalid token type.");
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
