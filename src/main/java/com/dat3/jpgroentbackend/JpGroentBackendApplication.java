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
}
