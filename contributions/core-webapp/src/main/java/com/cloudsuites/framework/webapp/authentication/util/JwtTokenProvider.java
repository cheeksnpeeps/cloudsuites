package com.cloudsuites.framework.webapp.authentication.util;

import com.cloudsuites.framework.services.user.entities.UserType;
import com.cloudsuites.framework.webapp.authentication.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final String secretKey = "your-secret-key";
    private final long accessTokenValidityMs = 1000 * 60 * 60 * 10; // 10 hours
    private final long refreshTokenValidityMs = 1000 * 60 * 60 * 24 * 7; // 7 days

    public String generateToken(UserDetails userDetails) {
        UserPrincipal userPrincipal = (UserPrincipal) userDetails;
        Map<String, Object> claims = createClaims(userPrincipal);
        return createToken(claims, userPrincipal.getUsername(), accessTokenValidityMs);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        UserPrincipal userPrincipal = (UserPrincipal) userDetails;
        Map<String, Object> claims = createClaims(userPrincipal);
        return createToken(claims, userPrincipal.getUsername(), refreshTokenValidityMs);
    }

    private Map<String, Object> createClaims(UserPrincipal userPrincipal) {
        return Map.of(
                "roles", userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()),
                "personaIds", userPrincipal.getPersonaIds()
        );
    }

    private String createToken(Map<String, Object> claims, String subject, long validityMs) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validityMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractPersonaId(String token, UserType userType) {
        Claims claims = extractAllClaims(token);

        // Extract personaIds map from claims
        Map<String, Long> personaIds = extractPersonaIds(claims);

        if (personaIds == null) {
            throw new RuntimeException("Persona IDs not found in JWT claims");
        }

        // Get persona ID based on userType
        Long personaId = personaIds.get(userType.name());

        if (personaId == null) {
            throw new RuntimeException("Persona ID for " + userType + " not found in JWT claims");
        }

        return personaId;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Long> extractPersonaIds(Claims claims) {
        try {
            return claims.get("personaIds", Map.class);
        } catch (ClassCastException e) {
            throw new RuntimeException("Invalid persona IDs format in JWT claims", e);
        }
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
