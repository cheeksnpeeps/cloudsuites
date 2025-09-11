package com.cloudsuites.framework.modules.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

/**
 * JWT Configuration for CloudSuites Authentication.
 * Configures RSA-256 signing, custom claims, and token validity periods.
 * 
 * Token Strategy:
 * - Access Token: 15 minutes (short-lived for security)
 * - Refresh Token: 30 days (long-lived for user convenience)
 * - RSA-256 signing for enhanced security over HMAC
 */
@Configuration
public class JwtConfig {

    private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);

    // Access token validity: 15 minutes (REQ-003)
    @Value("${jwt.access-token-validity-minutes:15}")
    private int accessTokenValidityMinutes;

    // Refresh token validity: 30 days (REQ-003)  
    @Value("${jwt.refresh-token-validity-days:30}")
    private int refreshTokenValidityDays;

    // JWT issuer identifier
    @Value("${jwt.issuer:cloudsuites}")
    private String issuer;

    // JWT audience identifier  
    @Value("${jwt.audience:CloudSuites}")
    private String audience;

    private final RSAKeyGenerator rsaKeyGenerator;

    public JwtConfig(RSAKeyGenerator rsaKeyGenerator) {
        this.rsaKeyGenerator = rsaKeyGenerator;
        logger.info("JWT Config initialized with RSA key generator");
    }

    /**
     * Provides the RSA private key for JWT token signing.
     * 
     * @return RSA private key
     */
    @Bean
    @DependsOn("rsaKeyGenerator")
    public RSAPrivateKey jwtSigningKey() {
        logger.info("Configuring JWT signing key (RSA private key)");
        RSAPrivateKey privateKey = rsaKeyGenerator.getPrivateKey();
        logger.info("JWT signing configured: {}", rsaKeyGenerator.getKeyInfo());
        return privateKey;
    }

    /**
     * Provides the RSA public key for JWT token verification.
     * 
     * @return RSA public key
     */
    @Bean
    @DependsOn("rsaKeyGenerator") 
    public RSAPublicKey jwtVerificationKey() {
        logger.info("Configuring JWT verification key (RSA public key)");
        return rsaKeyGenerator.getPublicKey();
    }

    /**
     * Access token validity duration in milliseconds.
     * Default: 15 minutes for security (REQ-003)
     * 
     * @return access token validity in milliseconds
     */
    @Bean
    public long accessTokenValidityMs() {
        long validityMs = Duration.ofMinutes(accessTokenValidityMinutes).toMillis();
        logger.info("Access token validity configured: {} minutes ({} ms)", 
            accessTokenValidityMinutes, validityMs);
        return validityMs;
    }

    /**
     * Refresh token validity duration in milliseconds.
     * Default: 30 days for user convenience (REQ-003)
     * 
     * @return refresh token validity in milliseconds
     */
    @Bean
    public long refreshTokenValidityMs() {
        long validityMs = Duration.ofDays(refreshTokenValidityDays).toMillis();
        logger.info("Refresh token validity configured: {} days ({} ms)", 
            refreshTokenValidityDays, validityMs);
        return validityMs;
    }

    /**
     * JWT issuer identifier.
     * Used in the 'iss' claim of all tokens.
     * 
     * @return issuer string
     */
    @Bean
    public String jwtIssuer() {
        logger.info("JWT issuer configured: {}", issuer);
        return issuer;
    }

    /**
     * JWT audience identifier.
     * Used in the 'aud' claim of all tokens.
     * 
     * @return audience string
     */
    @Bean
    public String jwtAudience() {
        logger.info("JWT audience configured: {}", audience);
        return audience;
    }

    /**
     * Gets current JWT configuration summary for monitoring.
     * 
     * @return configuration summary string
     */
    public String getConfigSummary() {
        return String.format(
            "JWT Config - Access: %dm, Refresh: %dd, Issuer: %s, Audience: %s, Keys: %s",
            accessTokenValidityMinutes, 
            refreshTokenValidityDays, 
            issuer, 
            audience, 
            rsaKeyGenerator.getKeyInfo()
        );
    }

    /**
     * Validates the current JWT configuration.
     * 
     * @return true if configuration is valid
     */
    public boolean isConfigurationValid() {
        try {
            boolean valid = accessTokenValidityMinutes > 0
                && refreshTokenValidityDays > 0
                && issuer != null && !issuer.trim().isEmpty()
                && audience != null && !audience.trim().isEmpty()
                && rsaKeyGenerator.isKeyPairValid();
                
            logger.debug("JWT configuration validation result: {}", valid);
            return valid;
            
        } catch (Exception e) {
            logger.error("JWT configuration validation failed: {}", e.getMessage());
            return false;
        }
    }
}
