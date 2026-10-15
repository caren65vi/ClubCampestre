package com.clubcampestre.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Define el codificador de contraseñas BCrypt que se usa para guardar y verificar contraseñas. */
@Configuration
public class SecurityConfig {

    /** Codificador BCrypt para las contraseñas de los usuarios. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
