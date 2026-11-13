package com.vetopia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * CONFIGURACIÓN - Contraseñas
 * Expone el codificador BCrypt que usan las cuentas del sistema. Se declara
 * como bean para que los servicios no creen instancias propias y para que
 * la futura configuración de Spring Security reutilice el mismo algoritmo.
 */
@Configuration
public class PasswordConfig {

    /** Codificador BCrypt: aplica sal aleatoria y hash lento por diseño. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
