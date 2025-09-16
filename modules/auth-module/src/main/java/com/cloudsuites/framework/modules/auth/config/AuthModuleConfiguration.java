package com.cloudsuites.framework.modules.auth.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Authentication module configuration.
 * 
 * Configures component scanning, JPA repositories, and entity scanning
 * for the authentication module.
 */
@Configuration
@ComponentScan(basePackages = {
    "com.cloudsuites.framework.modules.auth.service",
    "com.cloudsuites.framework.modules.auth.config"
})
@EnableJpaRepositories(basePackages = "com.cloudsuites.framework.modules.auth.repository")
@EntityScan(basePackages = {
    "com.cloudsuites.framework.modules.auth.entity",
    "com.cloudsuites.framework.services.user.entities" // Existing entities until migration
})
@EnableTransactionManagement
public class AuthModuleConfiguration {
    
    // Configuration beans will be added here as needed
    
}
