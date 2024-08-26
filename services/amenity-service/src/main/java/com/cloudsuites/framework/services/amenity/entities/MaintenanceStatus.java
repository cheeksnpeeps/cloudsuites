package com.cloudsuites.framework.services.amenity.entities;

public enum MaintenanceStatus {
    OPERATIONAL, // The amenity is available for use.
    UNDER_MAINTENANCE, //The amenity is currently under maintenance and not available for use.
    OUT_OF_SERVICE, // The amenity is out of service due to repairs or other issues.
    SCHEDULED_FOR_MAINTENANCE, // The amenity is scheduled for maintenance at a future date and time.
    CLOSED, // The amenity is closed and not available for use.
}
