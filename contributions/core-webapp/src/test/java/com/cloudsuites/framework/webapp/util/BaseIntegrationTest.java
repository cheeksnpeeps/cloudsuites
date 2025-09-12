package com.cloudsuites.framework.webapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for integration tests with proper database cleanup.
 * Provides common setup and utilities for all test classes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TestDatabaseCleanup testDatabaseCleanup;

    /**
     * Setup method that runs before each test.
     * Ensures clean database state for each test.
     */
    @BeforeEach
    void setUp() {
        testDatabaseCleanup.cleanAllData();
        setupTestData();
    }

    /**
     * Override this method in subclasses to set up specific test data.
     * Called after database cleanup in setUp().
     */
    protected void setupTestData() {
        // Default implementation - override in subclasses
    }

    /**
     * Generate unique email for tests to avoid conflicts.
     */
    protected String generateUniqueEmail(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "@test.com";
    }

    /**
     * Generate unique phone number for tests.
     */
    protected String generateUniquePhone() {
        return "+1416" + String.format("%07d", System.currentTimeMillis() % 10000000);
    }
}
