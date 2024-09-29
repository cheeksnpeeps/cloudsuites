package com.cloudsuites.framework.webapp;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import java.util.HashMap;
import java.util.Map;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
public class R2dbcConfig {

    private final Environment env;

    public R2dbcConfig(Environment env) {
        this.env = env;
    }

    private Map<String, Object> getVendorProperties() {
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("host", env.getProperty("spring.r2dbc.host"));
        jpaProperties.put("username", env.getProperty("spring.r2dbc.username"));
        jpaProperties.put("password", env.getProperty("spring.r2dbc.password"));
        jpaProperties.put("driver-class-name", env.getProperty("spring.r2dbc.driver-class-name"));
        jpaProperties.put("pools.initial-size", env.getProperty("spring.r2dbc.pools.initial-size"));
        jpaProperties.put("pools.max-size", env.getProperty("spring.r2dbc.pools.max-size"));
        jpaProperties.put("database", env.getProperty("spring.r2dbc.database"));
        jpaProperties.put("port", env.getProperty("spring.r2dbc.port"));

        return jpaProperties;
    }

    @Bean
    @ConfigurationProperties("spring.r2dbc")
    public ConnectionFactory connectionFactory() {
        Map<String, Object> jpaProperties = getVendorProperties();
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, jpaProperties.get("driver-class-name").toString())
                .option(HOST, jpaProperties.get("host").toString())
                .option(PORT, Integer.valueOf(jpaProperties.get("port").toString()))
                .option(USER, jpaProperties.get("username").toString())
                .option(PASSWORD, jpaProperties.get("password").toString())
                .option(DATABASE, jpaProperties.get("database").toString())
                .build());
    }

    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
}

