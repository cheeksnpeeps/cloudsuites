package com.cloudsuites.framework.services.user.entities;

/**
 * Enumeration for risk levels in security and audit events.
 * Matches the risk_level check constraint in the database (V4 migration).
 */
public enum RiskLevel {
    /**
     * Low risk - Normal operations, successful authentications
     */
    LOW(0, 25),
    
    /**
     * Medium risk - Minor security concerns, failed attempts
     */
    MEDIUM(26, 60),
    
    /**
     * High risk - Significant security concerns, suspicious activity
     */
    HIGH(61, 85),
    
    /**
     * Critical risk - Severe security threats, immediate attention required
     */
    CRITICAL(86, 100);

    private final int minScore;
    private final int maxScore;

    RiskLevel(int minScore, int maxScore) {
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    /**
     * Gets the minimum risk score for this level.
     */
    public int getMinScore() {
        return minScore;
    }

    /**
     * Gets the maximum risk score for this level.
     */
    public int getMaxScore() {
        return maxScore;
    }

    /**
     * Gets the default risk score for this level (middle of range).
     */
    public int getDefaultScore() {
        return (minScore + maxScore) / 2;
    }

    /**
     * Determines the risk level based on a numeric risk score.
     */
    public static RiskLevel fromScore(int score) {
        if (score < 0) score = 0;
        if (score > 100) score = 100;
        
        for (RiskLevel level : values()) {
            if (score >= level.minScore && score <= level.maxScore) {
                return level;
            }
        }
        return LOW; // Default fallback
    }

    /**
     * Determines if this risk level requires immediate attention.
     */
    public boolean requiresImmediateAttention() {
        return this == HIGH || this == CRITICAL;
    }

    /**
     * Determines if this risk level should trigger an alert.
     */
    public boolean shouldTriggerAlert() {
        return this == CRITICAL;
    }

    /**
     * Gets a user-friendly description of this risk level.
     */
    public String getDescription() {
        return switch (this) {
            case LOW -> "Normal activity with minimal security concerns";
            case MEDIUM -> "Moderate risk requiring monitoring";
            case HIGH -> "Elevated risk requiring investigation";
            case CRITICAL -> "Critical security threat requiring immediate action";
        };
    }
}
