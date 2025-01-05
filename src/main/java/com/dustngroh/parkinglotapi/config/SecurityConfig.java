package com.dustngroh.parkinglotapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for testing purposes
                .authorizeHttpRequests(auth -> auth
                        //.requestMatchers("/api/parkinglots/**").permitAll() // Allow public access to parking lot endpoints
                        .anyRequest().authenticated() // Protect all other endpoints
                )
                .httpBasic(httpBasic -> {}); // Enable HTTP Basic authentication
        return http.build();
    }
}