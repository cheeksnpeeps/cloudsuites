package com.cloudsuites.framework.webapp.rest.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;

/**
 * Response DTO for token operations returning access and refresh tokens.
 */
@Data
@Builder
@Schema(description = "Token pair response containing access and refresh tokens")
public class TokenResponse {

    @Schema(description = "JWT access token", example = "eyJhbGciOiJSUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "Refresh token for obtaining new access tokens", example = "eyJhbGciOiJSUzI1NiJ9...")
    private String refreshToken;

    @Schema(description = "Session identifier", example = "SES-01H7XVZQ8N2K1M5P3R7S9T")
    private String sessionId;

    @Schema(description = "Access token expiration time in seconds", example = "900")
    private long accessTokenExpiresIn;

    @Schema(description = "Refresh token expiration time in seconds", example = "2592000")
    private long refreshTokenExpiresIn;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;

    public static TokenResponse from(com.cloudsuites.framework.services.user.TokenRotationService.TokenPairResponse tokenPair) {
        return TokenResponse.builder()
                .accessToken(tokenPair.accessToken())
                .refreshToken(tokenPair.refreshToken())
                .sessionId(tokenPair.sessionId())
                .accessTokenExpiresIn(tokenPair.accessTokenExpiresIn())
                .refreshTokenExpiresIn(tokenPair.refreshTokenExpiresIn())
                .tokenType(tokenPair.tokenType())
                .build();
    }
}
