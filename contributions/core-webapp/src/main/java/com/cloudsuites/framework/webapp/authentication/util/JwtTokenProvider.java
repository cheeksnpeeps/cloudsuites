package com.cloudsuites.framework.webapp.authentication.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    private final String secretKey = "lYZGkfPC310rZ-Bbr0ZwLb-PcdVXAzJDKXj6hHBVTFs=";
    private final long accessTokenValidityMs = 1000 * 60 * 60 * 10; // 10 hours
    private final long refreshTokenValidityMs = 1000 * 60 * 60 * 24 * 7; // 7 days
    private final SecretKey key;

    public JwtTokenProvider() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(JwtBuilder jwtBuilder) {
        return createToken(jwtBuilder, accessTokenValidityMs);
    }

    public String generateRefreshToken(JwtBuilder jwtBuilder) {
        return createToken(jwtBuilder, refreshTokenValidityMs);
    }

    private String createToken(JwtBuilder jwtBuilder, long validityMs) {
        return jwtBuilder
                .issuer("cloudsuites")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + validityMs))
                .signWith(key)
                .compact();
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractAudience(String refreshToken) {
        return extractClaim(refreshToken, Claims::getAudience).stream().findAny().orElse(null);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
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
