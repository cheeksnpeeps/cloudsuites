package com.cloudsuites.framework.modules.auth.service.impl;

import com.cloudsuites.framework.services.auth.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of AuthenticationService.
 * 
 * This service provides basic authentication system status and health checks.
 * It coordinates with other authentication services to provide overall system status.
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    
    @Autowired
    private AuthServiceCoordinator authServiceCoordinator;
    
    @Override
    public boolean isAuthenticationSystemReady() {
        logger.debug("Checking authentication system readiness");
        
        try {
            // Check if core authentication services are available
            boolean coordinatorReady = authServiceCoordinator.validateAuthenticationServices();
            
            logger.info("Authentication system readiness check completed: {}", coordinatorReady);
            return coordinatorReady;
            
        } catch (Exception e) {
            logger.error("Error checking authentication system readiness", e);
            return false;
        }
    }
    
    @Override
    public String getAuthenticationStatus() {
        logger.debug("Getting authentication system status");
        
        try {
            boolean isReady = isAuthenticationSystemReady();
            
            if (isReady) {
                return "Authentication system is operational - all services available";
            } else {
                return "Authentication system has issues - some services may be unavailable";
            }
            
        } catch (Exception e) {
            logger.error("Error getting authentication status", e);
            return "Authentication system status unknown - error occurred: " + e.getMessage();
        }
    }
}
