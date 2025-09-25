package com.cloudsuites.framework.modules.auth.repository;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Test configuration for JPA repository tests.
 * 
 * Provides minimal Spring Boot configuration needed for @DataJpaTest
 * to work properly with repository integration tests.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
@SpringBootApplication
@EntityScan("com.cloudsuites.framework.modules.auth.entity")
@EnableJpaRepositories("com.cloudsuites.framework.modules.auth.repository")
public class TestJpaConfig {
    // Minimal test configuration - no beans needed
}
