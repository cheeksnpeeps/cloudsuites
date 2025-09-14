package com.cloudsuites.framework.modules.auth.exception;

/**
 * Exception thrown when account is locked due to security policies.
 * 
 * This includes scenarios like:
 * - Too many failed login attempts
 * - Administrative account suspension
 * - Security policy violations
 */
public class AccountLockedException extends AuthenticationException {
    
    public AccountLockedException(String message) {
        super(message);
    }
    
    public AccountLockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
