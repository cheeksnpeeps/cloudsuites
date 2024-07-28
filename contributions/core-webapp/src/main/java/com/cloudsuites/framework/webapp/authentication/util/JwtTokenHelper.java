package com.cloudsuites.framework.webapp.authentication.util;

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

    public String generateToken(String personaId, String buildingId, String unitId, String userId) {
        JwtBuilder claims = createClaims(personaId, buildingId, unitId, userId);
        return jwtTokenProvider.generateToken(claims);
    }

    public String generateRefreshToken(String personaId, String buildingId, String unitId, String userId) {
        JwtBuilder claims = createClaims(personaId, buildingId, unitId, userId);
        return jwtTokenProvider.generateRefreshToken(claims);
    }

    public boolean validateTokenClaims(Claims claims, String buildingId, String unitId, String personaId) {
        String ownerIdClaim = claims.get(WebAppConstants.Claim.PERSONA_ID, String.class);
        String buildingIdClaim = claims.get(WebAppConstants.Claim.BUILDING_ID, String.class);
        String unitIdClaim = claims.get(WebAppConstants.Claim.UNIT_ID, String.class);
        return ownerIdClaim.equals(personaId) && buildingIdClaim.equals(buildingId) && unitIdClaim.equals(unitId);
    }

    private JwtBuilder createClaims(String personaId, String buildingId, String unitId, String userId) {
        return Jwts.builder()
                .subject(personaId)
                .claim(WebAppConstants.Claim.PERSONA_ID, personaId)
                .claim(WebAppConstants.Claim.BUILDING_ID, buildingId)
                .claim(WebAppConstants.Claim.UNIT_ID, unitId)
                .claim(WebAppConstants.Claim.USER_ID, userId);
    }
}
