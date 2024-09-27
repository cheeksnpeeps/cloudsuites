package com.cloudsuites.framework.webapp;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.cloudsuites.framework.services",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class JpaConfig {

    private final Environment env;

    public JpaConfig(Environment env) {
        this.env = env;
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       @Qualifier("dataSource") DataSource dataSource) {
        Map<String, Object> jpaProperties = new HashMap<>();

        // Explicitly setting Hibernate properties
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");  // Change to "validate" or "none" in production
        jpaProperties.put("hibernate.show_sql", "true");        // Optional: show SQL queries in logs
        jpaProperties.put("hibernate.format_sql", "true");      // Optional: pretty-print SQL

        return builder
                .dataSource(dataSource)
                .packages("com.cloudsuites.framework.services")  // Your JPA entities package
                .persistenceUnit("jpa")                          // Persistence unit name
                .properties(jpaProperties)                       // Set JPA properties
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:59665/cloudsuites")    // Explicit JDBC URL
                .username("csuser")
                .password("csPassw0rd")
                .driverClassName("org.postgresql.Driver")               // Explicit driver class
                .build();
    }
}
