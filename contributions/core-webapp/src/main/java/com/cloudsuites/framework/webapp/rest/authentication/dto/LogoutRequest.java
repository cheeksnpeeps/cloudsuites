package com.cloudsuites.framework.webapp.rest.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Request DTO for logout operations.
 */
@Data
@Schema(description = "Request to logout and revoke tokens")
public class LogoutRequest {

    @Schema(description = "Refresh token to revoke", example = "eyJhbGciOiJSUzI1NiJ9...")
    private String refreshToken;

    @Schema(description = "Access token JTI for session identification", example = "jti-123456")
    private String accessTokenJti;

    @Schema(description = "Whether to logout from all devices", example = "false")
    private boolean logoutFromAllDevices = false;
}
