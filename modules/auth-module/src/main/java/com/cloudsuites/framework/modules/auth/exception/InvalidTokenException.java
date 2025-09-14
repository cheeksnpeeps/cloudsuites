package com.cloudsuites.framework.modules.auth.exception;

/**
 * Exception thrown when an invalid token is provided.
 * 
 * This includes scenarios like:
 * - Malformed JWT tokens
 * - Expired tokens
 * - Invalid signatures
 * - Revoked tokens
 */
public class InvalidTokenException extends AuthenticationException {
    
    public InvalidTokenException(String message) {
        super(message);
    }
    
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
