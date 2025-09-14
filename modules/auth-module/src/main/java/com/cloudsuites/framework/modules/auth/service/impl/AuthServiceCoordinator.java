package com.cloudsuites.framework.modules.auth.service.impl;

import com.cloudsuites.framework.services.user.RefreshTokenService;
import com.cloudsuites.framework.services.user.TokenRotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Placeholder implementation for authentication services.
 * 
 * This class serves as a bridge between the auth-module structure
 * and the existing authentication services until full migration.
 * 
 * Future authentication services will be implemented here following
 * the proper module structure.
 */
@Service
public class AuthServiceCoordinator {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceCoordinator.class);
    
    @Autowired(required = false)
    private RefreshTokenService refreshTokenService;
    
    @Autowired(required = false) 
    private TokenRotationService tokenRotationService;
    
    /**
     * Validates that the authentication services are properly wired.
     * This method can be used for health checks and debugging.
     */
    public boolean validateAuthenticationServices() {
        logger.debug("Validating authentication services configuration");
        
        boolean refreshTokenServiceAvailable = refreshTokenService != null;
        boolean tokenRotationServiceAvailable = tokenRotationService != null;
        
        logger.info("Authentication services status - RefreshToken: {}, TokenRotation: {}", 
                   refreshTokenServiceAvailable, tokenRotationServiceAvailable);
        
        return refreshTokenServiceAvailable && tokenRotationServiceAvailable;
    }
    
    /**
     * Gets the refresh token service.
     * @return RefreshTokenService instance if available
     */
    public RefreshTokenService getRefreshTokenService() {
        return refreshTokenService;
    }
    
    /**
     * Gets the token rotation service.
     * @return TokenRotationService instance if available
     */
    public TokenRotationService getTokenRotationService() {
        return tokenRotationService;
    }
}
