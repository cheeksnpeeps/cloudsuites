package com.cloudsuites.framework.services.auth.dto;

import java.time.LocalDateTime;

/**
 * Authentication response DTO.
 * 
 * Contains the result of an authentication attempt including
 * success status, tokens, and additional security information.
 */
public class AuthenticationResponse {
    
    private boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresAt;
    private String userId;
    private String sessionId;
    
    // Default constructor
    public AuthenticationResponse() {
    }
    
    // Constructor for successful authentication
    public AuthenticationResponse(boolean success, String accessToken, String refreshToken, 
                                 LocalDateTime expiresAt, String userId, String sessionId) {
        this.success = success;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.userId = userId;
        this.sessionId = sessionId;
        this.message = "Authentication successful";
    }
    
    // Constructor for failed authentication
    public AuthenticationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    // Static factory methods
    public static AuthenticationResponse success(String accessToken, String refreshToken, 
                                               LocalDateTime expiresAt, String userId, String sessionId) {
        return new AuthenticationResponse(true, accessToken, refreshToken, expiresAt, userId, sessionId);
    }
    
    public static AuthenticationResponse failure(String message) {
        return new AuthenticationResponse(false, message);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    @Override
    public String toString() {
        return "AuthenticationResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", accessToken='[PROTECTED]'" +
                ", refreshToken='[PROTECTED]'" +
                ", expiresAt=" + expiresAt +
                ", userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
