package com.cloudsuites.framework.webapp.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

/**
 * Utility class for proper database cleanup in tests.
 * Uses native SQL for efficient cleanup to avoid foreign key issues.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TestDatabaseCleanup {

    private final EntityManager entityManager;

    /**
     * Cleans all test data using native SQL to avoid foreign key constraints.
     */
    @Transactional
    public void cleanAllData() {
        try {
            log.debug("Starting test database cleanup with native SQL");
            
            // Use TRUNCATE CASCADE for more efficient cleanup
            // This resets sequences and handles foreign keys
            truncateAllTables();
            
            log.debug("Test database cleanup completed successfully");
            
        } catch (Exception e) {
            log.error("Error during database cleanup", e);
            // Fallback to individual DELETE operations
            try {
                log.debug("Falling back to DELETE operations");
                cleanWithDeleteOperations();
            } catch (Exception fallbackError) {
                log.error("Fallback cleanup also failed", fallbackError);
                throw new RuntimeException("Could not clean test database", fallbackError);
            }
        }
    }

    /**
     * Truncate all tables using CASCADE to handle foreign keys efficiently.
     */
    private void truncateAllTables() {
        // List of main tables in dependency order (child tables first)
        String[] tables = {
            "amenity_booking", "user_roles", "auth_audit_events", 
            "user_sessions", "otp_codes", "tenant", "owner", 
            "staff", "admin", "unit", "floor", "amenity", 
            "building", "identity"
        };
        
        for (String table : tables) {
            try {
                String sql = "TRUNCATE TABLE " + table + " RESTART IDENTITY CASCADE";
                entityManager.createNativeQuery(sql).executeUpdate();
                log.debug("Truncated table: {}", table);
            } catch (Exception e) {
                log.debug("Could not truncate {}: {}", table, e.getMessage());
                // Continue with other tables
            }
        }
    }

    /**
     * Fallback method using DELETE operations with disabled foreign key checks.
     */
    private void cleanWithDeleteOperations() {
        // Disable foreign key checks temporarily (PostgreSQL approach)
        entityManager.createNativeQuery("SET session_replication_role = replica").executeUpdate();
        
        // Clean all tables in any order
        String[] tables = {
            "amenity_booking", "amenity", "user_roles", 
            "tenant", "owner", "staff", "admin",
            "unit", "floor", "building", "identity",
            "auth_audit_events", "user_sessions", "otp_codes"
        };
        
        for (String table : tables) {
            executeNativeDelete("DELETE FROM " + table);
        }
        
        // Re-enable foreign key checks
        entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();
    }

    private void executeNativeDelete(String sql) {
        try {
            int deletedRows = entityManager.createNativeQuery(sql).executeUpdate();
            log.debug("Executed: {} - Deleted {} rows", sql, deletedRows);
        } catch (Exception e) {
            log.debug("Failed to execute: {} - Error: {}", sql, e.getMessage());
        }
    }

    /**
     * Clean only user-related data.
     */
    @Transactional
    public void cleanUserData() {
        try {
            entityManager.createNativeQuery("SET session_replication_role = replica").executeUpdate();
            
            String[] userTables = {"user_roles", "tenant", "owner", "staff", "admin", "identity"};
            for (String table : userTables) {
                executeNativeDelete("DELETE FROM " + table);
            }
            
            entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();
        } catch (Exception e) {
            log.error("Error cleaning user data", e);
        }
    }

    /**
     * Clean only property-related data.
     */
    @Transactional
    public void cleanPropertyData() {
        try {
            entityManager.createNativeQuery("SET session_replication_role = replica").executeUpdate();
            
            String[] propertyTables = {"amenity_booking", "amenity", "unit", "floor", "building"};
            for (String table : propertyTables) {
                executeNativeDelete("DELETE FROM " + table);
            }
            
            entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();
        } catch (Exception e) {
            log.error("Error cleaning property data", e);
        }
    }
}
