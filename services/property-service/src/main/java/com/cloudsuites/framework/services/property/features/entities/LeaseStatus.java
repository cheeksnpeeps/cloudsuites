package com.cloudsuites.framework.services.property.features.entities;

import io.swagger.v3.oas.annotations.media.Schema;

public enum LeaseStatus {
    @Schema(description = "Lease is active", example = "ACTIVE")
    ACTIVE,
    @Schema(description = "Lease is expired", example = "EXPIRED")
    EXPIRED,
    @Schema(description = "Lease is terminated", example = "TERMINATED")
    TERMINATED,
    @Schema(description = "Lease is pending", example = "PENDING")
    PENDING,
    @Schema(description = "Lease is renewed", example = "RENEWED")
    RENEWED
}

