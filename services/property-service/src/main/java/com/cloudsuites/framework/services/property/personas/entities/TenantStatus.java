package com.cloudsuites.framework.services.property.personas.entities;

import io.swagger.v3.oas.annotations.media.Schema;

public enum TenantStatus {
    @Schema(description = "Tenant is actively living in the unit", example = "ACTIVE")
    ACTIVE,
    @Schema(description = "Tenant is not currently living in the unit", example = "INACTIVE")
    INACTIVE,
    @Schema(description = "Tenant is pending to move in", example = "PENDING")
    PENDING,
    @Schema(description = "Tenant is deleted", example = "DELETED")
    DELETED
}
