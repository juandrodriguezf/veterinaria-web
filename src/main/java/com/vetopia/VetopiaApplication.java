package com.vetopia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de Vetopia. http://localhost:8080/
 * Spring Boot escanea el paquete com.vetopia y sus subpaquetes
 * (controller, service, repository, entities) para registrar los beans
 * y gestionar la inyección de dependencias automáticamente.
 */
@SpringBootApplication
public class VetopiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetopiaApplication.class, args);
    }
}
