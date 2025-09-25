package com.cloudsuites.framework.webapp.rest.authentication;

import com.cloudsuites.framework.services.user.RefreshTokenService;
import com.cloudsuites.framework.services.user.TokenRotationService;
import com.cloudsuites.framework.services.user.entities.UserSession;
import com.cloudsuites.framework.webapp.rest.authentication.dto.LogoutRequest;
import com.cloudsuites.framework.webapp.rest.authentication.dto.TokenRefreshRequest;
import com.cloudsuites.framework.webapp.rest.authentication.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for JWT token rotation and session management operations.
 * Provides endpoints for refreshing tokens, logout, and session management.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Token rotation and session management operations")
public class AuthenticationController {

    private final TokenRotationService tokenRotationService;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "Refresh access token", 
               description = "Use refresh token to obtain new access and refresh token pair")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid refresh token"),
        @ApiResponse(responseCode = "401", description = "Refresh token expired or revoked")
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request,
            HttpServletRequest httpRequest) {
        
        log.debug("Token refresh request received");
        
        try {
            // Extract IP address if not provided
            String ipAddress = request.getIpAddress() != null 
                ? request.getIpAddress() 
                : getClientIpAddress(httpRequest);
            
            // Rotate tokens (using WEB as default device type, assumed trusted for refresh)
            TokenRotationService.TokenPairResponse tokenPair = tokenRotationService.rotateTokens(
                request.getRefreshToken(), ipAddress, 
                com.cloudsuites.framework.services.user.entities.DeviceType.WEB, true
            );
            
            TokenResponse response = TokenResponse.from(tokenPair);
            
            log.info("Token refresh successful for session: {}", tokenPair.sessionId());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Unexpected error during token refresh", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Logout user", 
               description = "Revoke refresh token and optionally logout from all devices")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@Valid @RequestBody LogoutRequest request) {
        
        log.debug("Logout request received (all devices: {})", request.isLogoutFromAllDevices());
        
        try {
            int revokedSessions = 0;
            
            if (request.isLogoutFromAllDevices()) {
                // Get user ID from refresh token first
                Optional<UserSession> sessionOpt = refreshTokenService.validateRefreshToken(request.getRefreshToken());
                if (sessionOpt.isPresent()) {
                    String userId = sessionOpt.get().getUserId();
                    revokedSessions = tokenRotationService.revokeAllUserTokens(userId);
                    log.info("Logout from all devices for user: {} ({} sessions revoked)", userId, revokedSessions);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                // Single session logout
                boolean revoked = false;
                
                if (request.getRefreshToken() != null) {
                    revoked = tokenRotationService.revokeToken(request.getRefreshToken());
                } else if (request.getAccessTokenJti() != null) {
                    revoked = tokenRotationService.revokeByAccessToken(request.getAccessTokenJti());
                }
                
                if (revoked) {
                    revokedSessions = 1;
                    log.info("Single session logout successful");
                } else {
                    log.warn("Logout failed: session not found or already inactive");
                    return ResponseEntity.notFound().build();
                }
            }
            
            Map<String, Object> response = Map.of(
                "message", "Logout successful",
                "revokedSessions", revokedSessions
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Unexpected error during logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get user active sessions", 
               description = "Retrieve all active sessions for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/sessions")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<UserSession>> getUserSessions(@RequestParam String userId) {
        
        log.debug("Get sessions request for user: {}", userId);
        
        try {
            List<UserSession> sessions = refreshTokenService.getUserActiveSessions(userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            log.error("Error retrieving user sessions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Revoke specific session", 
               description = "Revoke a specific session by session ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session revoked successfully"),
        @ApiResponse(responseCode = "404", description = "Session not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/sessions/{sessionId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> revokeSession(@PathVariable String sessionId) {
        
        log.debug("Revoke session request for: {}", sessionId);
        
        try {
            boolean revoked = refreshTokenService.revokeSessionById(sessionId);
            
            if (revoked) {
                log.info("Session revoked successfully: {}", sessionId);
                return ResponseEntity.ok(Map.of("message", "Session revoked successfully"));
            } else {
                log.warn("Session not found: {}", sessionId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error revoking session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Trust device", 
               description = "Mark a device as trusted to extend session lifetime")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Device trusted successfully"),
        @ApiResponse(responseCode = "404", description = "Session not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/sessions/{sessionId}/trust")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> trustDevice(@PathVariable String sessionId) {
        
        log.debug("Trust device request for session: {}", sessionId);
        
        try {
            boolean trusted = refreshTokenService.trustDevice(sessionId);
            
            if (trusted) {
                log.info("Device trusted successfully for session: {}", sessionId);
                return ResponseEntity.ok(Map.of("message", "Device trusted successfully"));
            } else {
                log.warn("Session not found: {}", sessionId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error trusting device", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get session statistics", 
               description = "Get session statistics for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/sessions/stats")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<RefreshTokenService.SessionStats> getSessionStats(@RequestParam String userId) {
        
        log.debug("Get session stats request for user: {}", userId);
        
        try {
            RefreshTokenService.SessionStats stats = refreshTokenService.getUserSessionStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error retrieving session statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ============================================================================
    // PRIVATE HELPER METHODS
    // ============================================================================

    /**
     * Extracts the client IP address from the HTTP request.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
