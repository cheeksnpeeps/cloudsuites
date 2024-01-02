package com.cloudsuites.framework.webapp.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    private final String audience;

    private static final Logger logger = LoggerFactory.getLogger(AudienceValidator.class);

    AudienceValidator(String audience) {
        this.audience = audience;
        logger.debug("AudienceValidator created with audience: {}", audience);
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        logger.debug("Validating audience: {}", audience);
        OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);
        // Add additional debug logs
        logger.debug("Received token for validation: {}", jwt);

        if (jwt.getAudience().contains(audience)) {
            logger.debug("Audience is valid");
            return OAuth2TokenValidatorResult.success();
        }
        logger.debug("Audience is invalid");
        return OAuth2TokenValidatorResult.failure(error);
    }
}