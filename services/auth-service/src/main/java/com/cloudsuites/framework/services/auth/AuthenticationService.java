package com.cloudsuites.framework.services.auth;

/**
 * Base authentication service interface.
 * 
 * This interface serves as a foundation for authentication services
 * that will be implemented in the auth-module.
 * 
 * Future authentication services (PasswordService, OtpService, etc.)
 * will extend or complement this interface.
 */
public interface AuthenticationService {
    
    /**
     * Validates if the authentication system is properly configured.
     * 
     * @return true if authentication system is ready, false otherwise
     */
    boolean isAuthenticationSystemReady();
    
    /**
     * Gets the authentication system status for health checks.
     * 
     * @return String describing the current authentication system status
     */
    String getAuthenticationStatus();
}
