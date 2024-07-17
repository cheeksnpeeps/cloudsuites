package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.webapp.authentication.filter.JwtAuthenticationFilter;
import com.cloudsuites.framework.webapp.authentication.providers.TenantAuthenticationProvider;
import com.cloudsuites.framework.webapp.authentication.service.CustomUserDetailsService;
import com.cloudsuites.framework.webapp.authentication.util.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final TenantAuthenticationProvider tenantAuthenticationProvider;

    public SecurityConfiguration(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService, TenantAuthenticationProvider tenantAuthenticationProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.tenantAuthenticationProvider = tenantAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable);

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService),
                UsernamePasswordAuthenticationFilter.class);

        // Register TenantAuthenticationProvider as an AuthenticationProvider
        http.authenticationProvider(tenantAuthenticationProvider);

        return http.build();
    }
}
