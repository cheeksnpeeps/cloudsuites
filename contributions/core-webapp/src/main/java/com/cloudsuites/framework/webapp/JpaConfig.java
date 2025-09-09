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

import jakarta.persistence.EntityManagerFactory;
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

    private Map<String, Object> getVendorProperties() {
        Map<String, Object> p = new HashMap<>();

        // Dialect is optional on Hibernate 6, but harmless
        p.put("hibernate.dialect",
              env.getProperty("spring.jpa.database-platform", "org.hibernate.dialect.PostgreSQLDialect"));

        // Map Spring's ddl-auto to BOTH Hibernate & Jakarta knobs
        String ddl = env.getProperty("spring.jpa.hibernate.ddl-auto",
                        env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto", "update"));

        p.put("hibernate.hbm2ddl.auto", ddl); // Hibernate legacy switch still supported
        p.put("jakarta.persistence.schema-generation.database.action",
              ddlToJakartaAction(ddl));        // Hibernate 6+ standard key

        // Your other flags
        p.put("hibernate.show_sql", env.getProperty("spring.jpa.properties.hibernate.show_sql", "true"));
        p.put("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql", "true"));
        p.put("hibernate.enable_lazy_load_no_trans",
              env.getProperty("spring.jpa.properties.hibernate.enable_lazy_load_no_trans", "true"));
        p.put("hibernate.use_sql_comments",
              env.getProperty("spring.jpa.properties.hibernate.use_sql_comments", "true"));

        return p;
    }

    // helper
    private static String ddlToJakartaAction(String ddl) {
        return switch (ddl.toLowerCase()) {
            case "create", "create-drop" -> "drop-and-create";
            case "update" -> "update";
            case "none", "validate" -> "none";
            default -> "update";
        };
    }
    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       @Qualifier("dataSource") DataSource dataSource) {
        Map<String, Object> jpaProperties = getVendorProperties();
        return builder
                .dataSource(dataSource)
                .packages("com.cloudsuites.framework.services")
                .persistenceUnit("jpa")
                .properties(jpaProperties)
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        EntityManagerFactory emf = entityManagerFactory.getObject();
        if (emf == null) {
            throw new IllegalStateException("EntityManagerFactory not available");
        }
        return new JpaTransactionManager(emf);
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(env.getProperty("spring.datasource.url"))
                .username(env.getProperty("spring.datasource.username"))
                .password(env.getProperty("spring.datasource.password"))
                .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
                .build();
    }
}
