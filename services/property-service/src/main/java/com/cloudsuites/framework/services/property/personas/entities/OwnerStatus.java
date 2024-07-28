package com.cloudsuites.framework.services.property.personas.entities;

import io.swagger.v3.oas.annotations.media.Schema;

public enum OwnerStatus {
    @Schema(description = "Owner owns the unit", example = "ACTIVE")
    ACTIVE,
    @Schema(description = "Owner is not longer an owner in the building", example = "INACTIVE")
    INACTIVE,
    @Schema(description = "Owner is not fully registered yet", example = "PENDING")
    PENDING,
    @Schema(description = "Owner is deleted", example = "DELETED")
    DELETED
}
