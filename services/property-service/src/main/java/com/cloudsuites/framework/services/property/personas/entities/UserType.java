package com.cloudsuites.framework.services.property.personas.entities;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types of users")
public enum UserType {
    @Schema(description = "Owner of a unit in a given property")
    OWNER,

    @Schema(description = "Tenant of a unit in a given property")
    TENANT,

    @Schema(description = "Staff of a property")
    STAFF,

    @Schema(description = "Admin of a property")
    ADMIN
}
