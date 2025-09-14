package com.cloudsuites.framework.webapp.rest.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for token refresh operations.
 */
@Data
@Schema(description = "Request to refresh access token using refresh token")
public class TokenRefreshRequest {

    @NotBlank(message = "Refresh token is required")
    @Schema(description = "Valid refresh token", example = "eyJhbGciOiJSUzI1NiJ9...")
    private String refreshToken;

    @Schema(description = "Client IP address for session tracking", example = "192.168.1.100")
    private String ipAddress;

    @Schema(description = "User agent string for session tracking", example = "Mozilla/5.0...")
    private String userAgent;
}
