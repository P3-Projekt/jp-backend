package com.dat3.jpgroentbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class JpGroentBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpGroentBackendApplication.class, args);
    }

    @Configuration
    public static class WebConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**") // Apply CORS to all endpoints
                    .allowedOrigins("http://localhost:3000") // Your frontend URL
                    .allowedMethods("GET", "POST", "PUT", "DELETE") // Allowed HTTP methods
                    .allowedHeaders("*") // Allow all headers
                    .allowCredentials(true); // Allow credentials (cookies, etc.)
        }
    }
}
