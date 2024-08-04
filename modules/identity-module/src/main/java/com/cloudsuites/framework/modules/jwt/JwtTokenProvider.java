package com.cloudsuites.framework.modules.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    @Value("${jwt.secretKey}")
    private String secretKey; // Secret key from properties file
    @Value("${jwt.accessTokenValidityMs}")
    private long accessTokenValidityMs; // Access token validity from properties file
    @Value("${jwt.refreshTokenValidityMs}")
    private long refreshTokenValidityMs; // Refresh token validity from properties file
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        logger.debug("JwtTokenProvider initialized with secret key.");
    }

    public String generateToken(JwtBuilder jwtBuilder) {
        logger.info("Generating access token.");
        return createToken(jwtBuilder, accessTokenValidityMs);
    }

    public String generateRefreshToken(JwtBuilder jwtBuilder) {
        logger.info("Generating refresh token.");
        return createToken(jwtBuilder, refreshTokenValidityMs);
    }

    private String createToken(JwtBuilder jwtBuilder, long validityMs) {
        logger.debug("Creating token with validity: {} ms", validityMs);
        return jwtBuilder
                .issuer("cloudsuites")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + validityMs))
                .signWith(key)
                .compact();
    }

    public String extractSubject(String token) {
        logger.debug("Extracting subject from token.");
        return extractClaim(token, Claims::getSubject);
    }

    public String extractAudience(String refreshToken) throws MalformedJwtException {
        logger.debug("Extracting audience from refresh token.");
        return extractClaim(refreshToken, Claims::getAudience).stream().findAny().orElseThrow(
                () -> {
                    logger.error("MalformedJwtException: Invalid refresh token");
                    return new MalformedJwtException("Invalid refresh token");
                }
        );
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        logger.debug("Extracting claim from token.");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) throws MalformedJwtException {
        logger.debug("Extracting all claims from token.");
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (MalformedJwtException e) {
            logger.error("MalformedJwtException: Invalid token{} ", e.getMessage());
            throw new MalformedJwtException("Invalid token");
        } catch (Exception e) {
            logger.error("Exception: Invalid token {}", e.getMessage());
            throw new MalformedJwtException("Invalid token");
        }
    }

    public boolean validateToken(String token) {
        logger.debug("Validating token.");
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        logger.debug("Checking if token is expired.");
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        logger.debug("Extracting expiration date from token.");
        return extractClaim(token, Claims::getExpiration);
    }
}