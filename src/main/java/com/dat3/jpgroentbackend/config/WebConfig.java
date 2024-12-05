//package com.dat3.jpgroentbackend.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.cors.CorsConfiguration;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:3000")  // Tillad anmodninger fra din frontend
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("Authorization", "Content-Type")
//                .allowCredentials(true);  // Tillad credentials/cookies
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.addAllowedOrigin("http://localhost:3000"); // Tillad anmodninger fra frontend
//        configuration.addAllowedMethod("*");  // Tillad alle HTTP-metoder
//        configuration.addAllowedHeader("Authorization"); // Tillad autorisation header
//        configuration.setAllowCredentials(true); // Tillad cookies
//
//        // Registrer konfigurationen
//        return request -> configuration;
//    }
//}

package com.dat3.jpgroentbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// This class is used to configure CORS for the application
@Configuration // Configuration is used for Spring Bean configuration
public class WebConfig implements WebMvcConfigurer {
    @Override // Override is used to indicate that a method is meant to override a method in a superclass
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow all paths
                .allowedOrigins("http://localhost:3000") // Frontend URL
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow all HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow cookies and credentials
    }
}