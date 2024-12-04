package com.dat3.jpgroentbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Define the SecurityScheme
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // Add the SecurityScheme to the Components
        Components components = new Components()
                .addSecuritySchemes("BearerAuth", securityScheme);

        // Add the SecurityRequirement referencing the scheme
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("BearerAuth");

        // Return the OpenAPI object with the configuration
        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityRequirement);
    }
}
