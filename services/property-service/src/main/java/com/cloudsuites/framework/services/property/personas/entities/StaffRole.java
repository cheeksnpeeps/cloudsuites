package com.cloudsuites.framework.services.property.personas.entities;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Roles available for staff")
public enum StaffRole {
    @Schema(description = "Property Manager")
    PROPERTY_MANAGER,

    @Schema(description = "Leasing Agent")
    LEASING_AGENT,

    @Schema(description = "Maintenance Technician")
    MAINTENANCE_TECHNICIAN,

    @Schema(description = "Accounting and Finance Manager")
    ACCOUNTING_FINANCE_MANAGER,

    @Schema(description = "Customer Service Representative")
    CUSTOMER_SERVICE_REPRESENTATIVE,

    @Schema(description = "Building Supervisor")
    BUILDING_SUPERVISOR,

    @Schema(description = "Building Security")
    BUILDING_SECURITY,

    ALL_STAFF, @Schema(description = "Other role")
    OTHER
}
