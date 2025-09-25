package com.cloudsuites.framework.services.user.entities;

/**
 * Enumeration for device types in user sessions.
 * Matches the device_type check constraint in the database (V3 migration).
 */
public enum DeviceType {
    /**
     * Web browser session
     */
    WEB("Web Browser"),
    
    /**
     * iOS mobile device
     */
    MOBILE_IOS("iPhone/iPad"),
    
    /**
     * Android mobile device
     */
    MOBILE_ANDROID("Android Device"),
    
    /**
     * Tablet device
     */
    TABLET("Tablet"),
    
    /**
     * Desktop application
     */
    DESKTOP("Desktop Application");

    private final String displayName;

    DeviceType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the user-friendly display name for this device type.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Determines if this device type is mobile.
     */
    public boolean isMobile() {
        return this == MOBILE_IOS || this == MOBILE_ANDROID;
    }

    /**
     * Determines if this device type is a tablet.
     */
    public boolean isTablet() {
        return this == TABLET;
    }

    /**
     * Determines if this device type is desktop-based.
     */
    public boolean isDesktop() {
        return this == DESKTOP || this == WEB;
    }
}
