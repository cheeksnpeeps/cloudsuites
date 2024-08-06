package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.modules.jwt.JwtTokenProvider;
import com.cloudsuites.framework.modules.user.UserRoleRepository;
import com.cloudsuites.framework.services.property.personas.entities.StaffRole;
import com.cloudsuites.framework.services.user.entities.AdminRole;
import com.cloudsuites.framework.services.user.entities.UserType;
import com.cloudsuites.framework.webapp.authentication.filter.JwtAuthenticationFilter;
import com.cloudsuites.framework.webapp.authentication.providers.CustomAuthenticationProvider;
import com.cloudsuites.framework.webapp.authentication.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationProvider customAuthenticationProvider;

    public SecurityConfiguration(JwtTokenProvider jwtTokenProvider,
                                 CustomUserDetailsService userDetailsService,
                                 CustomAuthenticationProvider customAuthenticationProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();

        Map<String, String[]> roleRelationships = new HashMap<>();
        roleRelationships.put(AdminRole.SUPER_ADMIN.name(), new String[]{
                AdminRole.BUSINESS_ADMIN.name(),
                AdminRole.BUILDINGS_ADMIN.name()}
        );
        roleRelationships.put(AdminRole.BUILDINGS_ADMIN.name(), new String[]{
                AdminRole.THIRD_PARTY_ADMIN.name(),
        });
        roleRelationships.put(AdminRole.THIRD_PARTY_ADMIN.name(), new String[]{
                StaffRole.PROPERTY_MANAGER.name()
        });
        roleRelationships.put(StaffRole.PROPERTY_MANAGER.name(), new String[]{
                StaffRole.BUILDING_SUPERVISOR.name()
        });
        roleRelationships.put(StaffRole.BUILDING_SUPERVISOR.name(), new String[]{
                StaffRole.BUILDING_SECURITY.name()
        });
        roleRelationships.put(StaffRole.BUILDING_SECURITY.name(), new String[]{});
        roleRelationships.put(UserType.OWNER.name(), new String[]{});
        roleRelationships.put(UserType.TENANT.name(), new String[]{});
        roleRelationships.put(StaffRole.ACCOUNTING_FINANCE_MANAGER.name(), new String[]{
                StaffRole.LEASING_AGENT.name(),
                StaffRole.CUSTOMER_SERVICE_REPRESENTATIVE.name(),
                StaffRole.MAINTENANCE_TECHNICIAN.name(),
                StaffRole.OTHER.name()
        });


        // Construct hierarchy string
        StringBuilder hierarchy = new StringBuilder();
        for (Map.Entry<String, String[]> entry : roleRelationships.entrySet()) {
            String parentRole = entry.getKey();
            for (String childRole : entry.getValue()) {
                hierarchy.append(parentRole).append(" > ").append(childRole).append("\n");
            }
        }

        roleHierarchy.setHierarchy(hierarchy.toString().trim());
        return roleHierarchy;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserRoleRepository userRoleRepository) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/companies/**").authenticated()
                        .requestMatchers("/api/v1/**").authenticated()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );


        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService,
                userRoleRepository), UsernamePasswordAuthenticationFilter.class);

        // Register CustomAuthenticationProvider
        http.authenticationProvider(customAuthenticationProvider);

        return http.build();
    }
}
