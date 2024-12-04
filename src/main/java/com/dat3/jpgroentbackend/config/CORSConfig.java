//package com.dat3.jpgroentbackend.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.filter.CorsFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//@Configuration
//public class CORSConfig {
//    @Bean
//    public CorsFilter corsFilter() {
//        System.out.println("CORS filter applied");
//        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOrigin("http://localhost:3000"); // Tillad anmodninger fra localhost:3000
//        config.addAllowedMethod("*");  // Tillad alle HTTP-metoder (GET, POST, PUT, DELETE, OPTIONS)
//        config.addAllowedHeader("Authorization");  // Tillad Authorization header
//        config.setAllowCredentials(true);  // Tillad cookies og credentials
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);  // Registrer CORS-konfigurationen for alle endpoints
//
//        return new CorsFilter(source);
//    }
//}
