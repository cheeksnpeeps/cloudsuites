package com.cloudsuites.framework.webapp;

import com.cloudsuites.framework.webapp.rest.ContextFilter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cloud Suites Framework")
                        .version("1.0")
                        .description("Cloud Suites Framework API Documentation")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .email("chmomar@gmail.com")
                                .name("Cheikh Lo")
                        )
                );
    }

    @Bean
    public FilterRegistrationBean<ContextFilter> correlationIdFilter() {
        FilterRegistrationBean<ContextFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ContextFilter());
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}
