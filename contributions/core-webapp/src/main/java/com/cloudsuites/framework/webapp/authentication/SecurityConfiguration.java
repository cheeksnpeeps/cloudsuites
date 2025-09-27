package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.modules.jwt.JwtTokenProvider;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.property.personas.entities.StaffRole;
import com.cloudsuites.framework.services.user.entities.AdminRole;
import com.cloudsuites.framework.webapp.authentication.filter.JwtAuthenticationFilter;
import com.cloudsuites.framework.webapp.authentication.providers.CustomAuthenticationProvider;
import com.cloudsuites.framework.webapp.authentication.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
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
        // Use LinkedHashMap to preserve order for readability
        Map<String, String[]> roleRelationships = new LinkedHashMap<>();
        
        // CLEAN ADMIN CHAIN: Linear progression without dangerous branches
        // Top Level: System Administrator - Ultimate Control
        roleRelationships.put(AdminRole.SUPER_ADMIN.name(), new String[]{
                AdminRole.BUSINESS_ADMIN.name()
        });
        
        // Executive Level: Business Operations - Strategic Oversight
        roleRelationships.put(AdminRole.BUSINESS_ADMIN.name(), new String[]{
                AdminRole.BUILDINGS_ADMIN.name()
        });
        
        // Management Level: Multi-Property Oversight
        roleRelationships.put(AdminRole.BUILDINGS_ADMIN.name(), new String[]{
                AdminRole.ALL_ADMIN.name()
        });
        
        // FUNCTIONAL DISTRIBUTION: ALL_ADMIN distributes to functional areas
        // Administrative Level: Operational Control Distribution
        roleRelationships.put(AdminRole.ALL_ADMIN.name(), new String[]{
                StaffRole.PROPERTY_MANAGER.name(),
                StaffRole.ACCOUNTING_FINANCE_MANAGER.name(),
                StaffRole.ALL_STAFF.name()
        });
        
        // PROPERTY MANAGEMENT HIERARCHY: Clear reporting lines
        // Property Level: Individual Building Management
        roleRelationships.put(StaffRole.PROPERTY_MANAGER.name(), new String[]{
                StaffRole.BUILDING_SUPERVISOR.name(),    // Direct supervisory line
                StaffRole.LEASING_AGENT.name(),          // Direct operational oversight
                StaffRole.CUSTOMER_SERVICE_REPRESENTATIVE.name(),
                StaffRole.MAINTENANCE_TECHNICIAN.name(),
                StaffRole.OTHER.name(),
                StaffRole.ALL_STAFF.name()
        });
        
        // Supervisory Level: On-Site Operations Chain
        roleRelationships.put(StaffRole.BUILDING_SUPERVISOR.name(), new String[]{
                StaffRole.BUILDING_SECURITY.name(),
                StaffRole.ALL_STAFF.name()
        });
        
        // FINANCIAL MANAGEMENT: Isolated functional role
        // Financial oversight without operational staff inheritance
        roleRelationships.put(StaffRole.ACCOUNTING_FINANCE_MANAGER.name(), new String[]{
                StaffRole.ALL_STAFF.name()  // Only baseline staff permissions
        });
        
        // BASELINE CONSISTENCY: All specific staff roles inherit ALL_STAFF
        // Operational Level: Specialized Staff - All inherit baseline permissions
        roleRelationships.put(StaffRole.LEASING_AGENT.name(), new String[]{
                StaffRole.ALL_STAFF.name()
        });
        roleRelationships.put(StaffRole.MAINTENANCE_TECHNICIAN.name(), new String[]{
                StaffRole.ALL_STAFF.name()
        });
        roleRelationships.put(StaffRole.CUSTOMER_SERVICE_REPRESENTATIVE.name(), new String[]{
                StaffRole.ALL_STAFF.name()
        });
        roleRelationships.put(StaffRole.BUILDING_SECURITY.name(), new String[]{
                StaffRole.ALL_STAFF.name()
        });
        roleRelationships.put(StaffRole.OTHER.name(), new String[]{
                StaffRole.ALL_STAFF.name()
        });
        
        // Base Staff Level: Common Permissions Foundation
        roleRelationships.put(StaffRole.ALL_STAFF.name(), new String[]{});
        
        // VENDOR ISOLATION: Third-party admin completely isolated
        // No inheritance to/from internal staff/admin roles
        roleRelationships.put(AdminRole.THIRD_PARTY_ADMIN.name(), new String[]{});
        
        // CLIENT ISOLATION: Property stakeholders completely separated
        // No inheritance relationships - resident portal access only
        // Note: OWNER and TENANT are intentionally excluded from hierarchy

        // Construct hierarchy string with duplicate prevention
        StringBuilder hierarchy = new StringBuilder();
        Set<String> seenRelationships = new HashSet<>();
        
        for (Map.Entry<String, String[]> entry : roleRelationships.entrySet()) {
            String parentRole = entry.getKey();
            for (String childRole : entry.getValue()) {
                String relationship = parentRole + " > " + childRole;
                if (seenRelationships.add(relationship)) {
                    hierarchy.append(relationship).append("\n");
                }
            }
        }

        // Use the modern constructor with hierarchy string
        return RoleHierarchyImpl.fromHierarchy(hierarchy.toString().trim());
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserRoleRepository userRoleRepository) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/v1/auth/**"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/error").permitAll()  // Allow error pages
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
