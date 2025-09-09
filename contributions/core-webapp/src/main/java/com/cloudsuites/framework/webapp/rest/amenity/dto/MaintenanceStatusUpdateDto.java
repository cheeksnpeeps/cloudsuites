package com.cloudsuites.framework.webapp.rest.amenity.dto;

import com.cloudsuites.framework.services.amenity.entities.MaintenanceStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for updating amenity maintenance status
 */
public class MaintenanceStatusUpdateDto {
    
    @NotNull(message = "Maintenance status is required")
    private MaintenanceStatus maintenanceStatus;
    
    private String reason; // Optional reason for the status change
    
    public MaintenanceStatusUpdateDto() {}
    
    public MaintenanceStatusUpdateDto(MaintenanceStatus maintenanceStatus, String reason) {
        this.maintenanceStatus = maintenanceStatus;
        this.reason = reason;
    }
    
    public MaintenanceStatus getMaintenanceStatus() {
        return maintenanceStatus;
    }
    
    public void setMaintenanceStatus(MaintenanceStatus maintenanceStatus) {
        this.maintenanceStatus = maintenanceStatus;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
