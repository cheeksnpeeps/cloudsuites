package com.cloudsuites.framework.services.user.entities;

/**
 * Gender enumeration for user identity information.
 * Supports inclusive gender representation.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public enum Gender {
    
    /**
     * Male gender.
     */
    MALE("Male"),
    
    /**
     * Female gender.
     */
    FEMALE("Female"),
    
    /**
     * Non-binary gender.
     */
    NON_BINARY("Non-binary"),
    
    /**
     * Prefer not to specify.
     */
    PREFER_NOT_TO_SAY("Prefer not to say"),
    
    /**
     * Other gender identification.
     */
    OTHER("Other");
    
    private final String displayName;
    
    Gender(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Get the display name of the gender.
     * 
     * @return Display name for UI presentation
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get gender from display name.
     * 
     * @param displayName The display name to match
     * @return Matching Gender enum, or null if not found
     */
    public static Gender fromDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return null;
        }
        
        for (Gender gender : values()) {
            if (gender.displayName.equalsIgnoreCase(displayName.trim())) {
                return gender;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
