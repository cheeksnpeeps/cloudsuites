package com.cloudsuites.framework.modules.auth.exception;

/**
 * Base exception for all authentication-related errors.
 * 
 * This exception serves as the parent class for all authentication
 * module specific exceptions.
 */
public class AuthenticationException extends RuntimeException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
