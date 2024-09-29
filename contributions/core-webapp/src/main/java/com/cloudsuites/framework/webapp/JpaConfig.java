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

    private Map<String, Object> getVendorProperties() {
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", env.getProperty("spring.datasource.hibernate.dialect"));
        jpaProperties.put("url", env.getProperty("spring.datasource.url"));
        jpaProperties.put("username", env.getProperty("spring.datasource.username"));
        jpaProperties.put("password", env.getProperty("spring.datasource.password"));
        jpaProperties.put("driver-class-name", env.getProperty("spring.datasource.driver-class-name"));
        return jpaProperties;
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
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        Map<String, Object> jpaProperties = getVendorProperties();
        return DataSourceBuilder.create()
                .url(jpaProperties.get("url").toString())
                .username(jpaProperties.get("username").toString())
                .password(jpaProperties.get("password").toString())
                .driverClassName(jpaProperties.get("driver-class-name").toString())
                .build();
    }
}
