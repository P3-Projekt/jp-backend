package com.dat3.jpgroentbackend.config;

import com.dat3.jpgroentbackend.model.repositories.utils.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

// This class is used to configure the security of the application
@Configuration // Configuration is used for Spring Bean configuration
public class SecurityConfig {

    // CorsConfigurationSource is an interface that provides a way to configure CORS
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * This constructor is used to inject the CorsConfigurationSource
     * @param corsConfigurationSource The CorsConfigurationSource to be injected
     */
    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    /**
     * This method is used to create a SecurityFilterChain
     * @param http The HttpSecurity object
     * @param jwtRequestFilter The JwtRequestFilter object
     * @return The SecurityFilterChain object
     * @throws Exception If an error occurs
     */
    @Bean // Bean is used to define a Spring Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disables due to authentication in next line
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class) // Adds user authentication filter
            .authorizeHttpRequests(auth -> auth // Authorizes HTTP requests
                .requestMatchers("/api/auth/**").permitAll() // Permits all requests to /api/auth/**
                .requestMatchers("/swagger-ui/**").permitAll() // Permits all requests to /swagger-ui/**
                .requestMatchers("/v3/api-docs/**").permitAll() // Permits all requests to /v3/api-docs/**
                .anyRequest().authenticated() // Requires authentication for all other requests
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Sets session creation policy to stateless, meaning no session is created
        http.cors(cors -> cors.configurationSource(corsConfigurationSource)); // Configures CORS
        return http.build(); // Builds the HttpSecurity object
    }

    /**
     * Defines a Spring Bean for password encoding using BCrypt.
     * @return A BCryptPasswordEncoder instance for password hashing and verification.
     */
    @Bean // Bean is used to define a Spring Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines a Spring Bean for the AuthenticationManager.
     * @param http HttpSecurity instance used to retrieve shared security objects.
     * @return Configured AuthenticationManager instance.
     * @throws Exception if an error occurs while building the AuthenticationManager.
     */
    @Bean // Bean is used to define a Spring Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // Gets the AuthenticationManagerBuilder from the HttpSecurity object
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.build(); // Builds the AuthenticationManager object
    }
}