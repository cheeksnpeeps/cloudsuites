package com.cloudsuites.framework.modules.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Enhanced JWT Token Provider with RSA-256 signing and custom claims support.
 * 
 * Key Features:
 * - RSA-256 signing (upgraded from HMAC for enhanced security)
 * - Custom claims support (userId, roles, persona, buildingContext)
 * - 15-minute access tokens / 30-day refresh tokens (REQ-003)
 * - Comprehensive token validation and claim extraction
 * - Audit-ready token generation with detailed logging
 * 
 * Security Enhancements:
 * - RSA-2048 key pairs for signing/verification
 * - Structured custom claims for fine-grained authorization
 * - Enhanced token validation with proper error handling
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    
    // Custom claim names
    public static final String CLAIM_TOKEN_TYPE = "tokenType";
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_PERSONA_TYPE = "personaType";
    public static final String CLAIM_BUILDING_CONTEXT = "buildingContext";
    public static final String CLAIM_RISK_PROFILE = "riskProfile";
    public static final String CLAIM_SESSION_ID = "sessionId";
    public static final String CLAIM_DEVICE_ID = "deviceId";
    public static final String CLAIM_AUTH_METHOD = "authMethod";

    @Autowired
    private RSAPrivateKey jwtSigningKey;
    
    @Autowired
    private RSAPublicKey jwtVerificationKey;
    
    @Autowired
    private long accessTokenValidityMs;
    
    @Autowired
    private long refreshTokenValidityMs;
    
    @Autowired
    private String jwtIssuer;
    
    @Autowired
    private String jwtAudience;

    @PostConstruct
    public void init() {
        logger.info("JwtTokenProvider initialized with RSA-256 signing");
        logger.info("Access token validity: {} ms ({} minutes)", 
            accessTokenValidityMs, accessTokenValidityMs / (1000 * 60));
        logger.info("Refresh token validity: {} ms ({} days)", 
            refreshTokenValidityMs, refreshTokenValidityMs / (1000 * 60 * 60 * 24));
        logger.debug("JWT issuer: {}, audience: {}", jwtIssuer, jwtAudience);
    }

    /**
     * Generates an access token with standard and custom claims.
     * Access tokens are short-lived (15 minutes) for enhanced security.
     * 
     * @param subject the token subject (typically user identifier)
     * @param customClaims additional custom claims for authorization
     * @return signed JWT access token
     */
    public String generateAccessToken(String subject, Map<String, Object> customClaims) {
        logger.info("Generating access token for subject: {}", subject);
        
        JwtBuilder builder = Jwts.builder()
            .subject(subject)
            .issuer(jwtIssuer)
            .audience().add(jwtAudience).and()
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + accessTokenValidityMs))
            .claim("tokenType", "access")  // Always add token type
            .signWith(jwtSigningKey);
            
        // Add custom claims if provided
        if (customClaims != null && !customClaims.isEmpty()) {
            customClaims.forEach(builder::claim);
            logger.debug("Added {} custom claims to access token", customClaims.size());
        }
        
        String token = builder.compact();
        logger.debug("Access token generated successfully for subject: {}", subject);
        return token;
    }

    /**
     * Generates a refresh token with minimal claims for security.
     * Refresh tokens are long-lived (30 days) but with limited scope.
     * 
     * @param subject the token subject 
     * @param sessionId the session identifier for token rotation tracking
     * @return signed JWT refresh token
     */
    public String generateRefreshToken(String subject, String sessionId) {
        logger.info("Generating refresh token for subject: {}", subject);
        
        String token = Jwts.builder()
            .subject(subject)
            .issuer(jwtIssuer)
            .audience().add(jwtAudience).and()
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + refreshTokenValidityMs))
            .claim(CLAIM_SESSION_ID, sessionId)
            .claim(CLAIM_TOKEN_TYPE, "refresh")
            .signWith(jwtSigningKey)
            .compact();
            
        logger.debug("Refresh token generated successfully for subject: {}", subject);
        return token;
    }

    /**
     * Creates an access token with comprehensive authentication context.
     * 
     * @param userId the unique user identifier
     * @param roles list of user roles/permissions
     * @param personaType user persona (TENANT, OWNER, STAFF, ADMIN)
     * @param buildingContext building access context
     * @param riskProfile user risk assessment level
     * @param sessionId current session identifier
     * @param deviceId device identifier for device trust
     * @param authMethod authentication method used
     * @return complete access token with all context
     */
    public String generateAccessTokenWithContext(
            String userId, 
            List<String> roles, 
            String personaType,
            String buildingContext, 
            String riskProfile,
            String sessionId, 
            String deviceId, 
            String authMethod) {
                
        logger.info("Generating context-aware access token for user: {}, persona: {}", userId, personaType);
        
        Map<String, Object> customClaims = Map.of(
            CLAIM_USER_ID, userId,
            CLAIM_ROLES, roles,
            CLAIM_PERSONA_TYPE, personaType,
            CLAIM_BUILDING_CONTEXT, buildingContext,
            CLAIM_RISK_PROFILE, riskProfile,
            CLAIM_SESSION_ID, sessionId,
            CLAIM_DEVICE_ID, deviceId,
            CLAIM_AUTH_METHOD, authMethod,
            "type", "access"
        );
        
        return generateAccessToken(userId, customClaims);
    }

    @Deprecated
    public String generateToken(JwtBuilder jwtBuilder) {
        logger.warn("Deprecated generateToken(JwtBuilder) method called. Use generateAccessToken() instead.");
        return createToken(jwtBuilder, accessTokenValidityMs);
    }

    @Deprecated
    public String generateRefreshToken(JwtBuilder jwtBuilder) {
        logger.warn("Deprecated generateRefreshToken(JwtBuilder) method called. Use generateRefreshToken(String, String) instead.");
        return createToken(jwtBuilder, refreshTokenValidityMs);
    }

    @Deprecated
    private String createToken(JwtBuilder jwtBuilder, long validityMs) {
        logger.debug("Creating deprecated token with validity: {} ms", validityMs);
        return jwtBuilder
                .issuer(jwtIssuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + validityMs))
                .signWith(jwtSigningKey)
                .compact();
    }

    /**
     * Extracts the subject from a JWT token.
     * 
     * @param token JWT token string
     * @return token subject
     */
    public String extractSubject(String token) {
        logger.debug("Extracting subject from token");
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the audience from a JWT token.
     * 
     * @param token JWT token string
     * @return token audience
     * @throws MalformedJwtException if token is invalid
     */
    public String extractAudience(String token) throws MalformedJwtException {
        logger.debug("Extracting audience from token");
        return extractClaim(token, Claims::getAudience).stream().findFirst().orElseThrow(
                () -> {
                    logger.error("MalformedJwtException: Invalid token - no audience claim");
                    return new MalformedJwtException("Invalid token - no audience claim");
                }
        );
    }

    /**
     * Extracts a specific claim from the JWT token.
     * 
     * @param token JWT token string
     * @param claimsResolver function to extract the desired claim
     * @param <T> type of the claim value
     * @return extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        logger.debug("Extracting custom claim from token");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     * Uses RSA public key for signature verification.
     * 
     * @param token JWT token string
     * @return all claims from the token
     * @throws MalformedJwtException if token is invalid or signature verification fails
     */
    public Claims extractAllClaims(String token) throws MalformedJwtException {
        logger.debug("Extracting all claims from token using RSA verification");
        try {
            return Jwts.parser()
                .verifyWith(jwtVerificationKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (MalformedJwtException e) {
            logger.error("MalformedJwtException: Invalid token - {}", e.getMessage());
            throw new MalformedJwtException("Invalid token");
        } catch (Exception e) {
            logger.error("Exception during token parsing: {}", e.getMessage());
            throw new MalformedJwtException("Invalid token");
        }
    }

    /**
     * Validates a JWT token for signature and expiration.
     * 
     * @param token JWT token string
     * @return true if token is valid and not expired
     */
    public boolean validateToken(String token) {
        logger.debug("Validating token");
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates a token and ensures it matches expected type.
     * 
     * @param token JWT token string
     * @param expectedType expected token type ("access" or "refresh")
     * @return true if token is valid and matches expected type
     */
    public boolean validateTokenWithType(String token, String expectedType) {
        logger.debug("Validating token with expected type: {}", expectedType);
        try {
            if (!validateToken(token)) {
                return false;
            }
            
            String tokenType = extractClaim(token, claims -> claims.get(CLAIM_TOKEN_TYPE, String.class));
            boolean typeMatches = expectedType.equals(tokenType);
            
            if (!typeMatches) {
                logger.warn("Token type mismatch. Expected: {}, Found: {}", expectedType, tokenType);
            }
            
            return typeMatches;
        } catch (Exception e) {
            logger.error("Token type validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the token has expired.
     * 
     * @param token JWT token string
     * @return true if token has expired
     */
    private boolean isTokenExpired(String token) {
        logger.debug("Checking if token is expired");
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT token.
     * 
     * @param token JWT token string
     * @return token expiration date
     */
    public Date extractExpiration(String token) {
        logger.debug("Extracting expiration date from token");
        return extractClaim(token, Claims::getExpiration);
    }

    // ============================================================================
    // CUSTOM CLAIM EXTRACTION METHODS
    // ============================================================================

    /**
     * Extracts the token type from the token.
     * 
     * @param token JWT token string
     * @return token type (access, refresh, etc.)
     */
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_TOKEN_TYPE));
    }

    /**
     * Extracts the user ID from the token.
     * 
     * @param token JWT token string
     * @return user ID
     */
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_USER_ID, String.class));
    }

    /**
     * Extracts the user roles from the token.
     * 
     * @param token JWT token string
     * @return list of user roles
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get(CLAIM_ROLES));
    }

    /**
     * Extracts the persona type from the token.
     * 
     * @param token JWT token string
     * @return persona type
     */
    public String extractPersonaType(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_PERSONA_TYPE, String.class));
    }

    /**
     * Extracts the building context from the token.
     * 
     * @param token JWT token string
     * @return building context
     */
    public String extractBuildingContext(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_BUILDING_CONTEXT, String.class));
    }

    /**
     * Extracts the session ID from the token.
     * 
     * @param token JWT token string
     * @return session ID
     */
    public String extractSessionId(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_SESSION_ID, String.class));
    }

    /**
     * Extracts the device ID from the token.
     * 
     * @param token JWT token string
     * @return device ID
     */
    public String extractDeviceId(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_DEVICE_ID, String.class));
    }

    /**
     * Extracts the authentication method from the token.
     * 
     * @param token JWT token string
     * @return authentication method
     */
    public String extractAuthMethod(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_AUTH_METHOD, String.class));
    }

    /**
     * Gets comprehensive token information for logging and debugging.
     * 
     * @param token JWT token string
     * @return token information string (no sensitive data)
     */
    public String getTokenInfo(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return String.format(
                "Token Info - Subject: %s, Type: %s, Issued: %s, Expires: %s, Issuer: %s",
                claims.getSubject(),
                claims.get(CLAIM_TOKEN_TYPE, String.class),
                claims.getIssuedAt(),
                claims.getExpiration(),
                claims.getIssuer()
            );
        } catch (Exception e) {
            return "Invalid token - cannot extract info";
        }
    }
}