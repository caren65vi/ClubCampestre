package com.clubcampestre.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configura CORS para que el frontend (React en localhost) pueda consumir la API /api/**. */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /** Permite peticiones desde localhost y 127.0.0.1 con los métodos de la API. */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
