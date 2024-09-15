package com.cloudsuites.framework.webapp;

import com.cloudsuites.framework.webapp.rest.ContextFilter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "Cloud Suites Framework", version = "1.0", description = "Cloud Suites Framework API Documentation"))
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "Authorization header using the Bearer scheme. Example: \"Authorization: Bearer <token>\""
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("Cloud Suites Framework")
                        .version("1.0")
                        .description("Cloud Suites Framework API Documentation")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .email("chmomar@gmail.com")
                                .name("Cheikh Lo")
                        )
                )
                .addSecurityItem(securityRequirement);
    }

    @Bean
    public SwaggerUiConfigParameters swaggerUiConfigParameters(SwaggerUiConfigProperties swaggerUiConfigProperties) {
        SwaggerUiConfigParameters config = new SwaggerUiConfigParameters(swaggerUiConfigProperties);
        config.setDocExpansion("none"); // Collapse all sections by default
        config.setDeepLinking(true);  // Enable deep linking to specific tags or operations
        return config;
    }

    @Bean
    public FilterRegistrationBean<ContextFilter> correlationIdFilter() {
        FilterRegistrationBean<ContextFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ContextFilter());
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}