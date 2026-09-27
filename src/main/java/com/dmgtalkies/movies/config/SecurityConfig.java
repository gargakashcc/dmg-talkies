package com.dmgtalkies.movies.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain chain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable()).httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN").requestMatchers("/api/**").hasAnyRole("ADMIN", "CUSTOMER").anyRequest().permitAll())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())).build();
    }

    @Bean
    UserDetailsService users() {
        return new InMemoryUserDetailsManager(User.withUsername("admin").password("{noop}admin").roles("ADMIN").build(),
                User.withUsername("customer").password("{noop}customer").roles("CUSTOMER").build());
    }
}
