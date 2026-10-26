package com.vetopia.controller.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetopia.entities.LoginRequest;
import com.vetopia.entities.ResultadoLogin;
import com.vetopia.service.LoginService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * CONTROLLER REST - Login
 * Expone la autenticación como API JSON para que el frontend Angular la
 * consuma con HttpClient. Sigue el patrón de la clase de REST:
 * {@code @RestController} (devuelve datos, no vistas), {@code @CrossOrigin}
 * para permitir las peticiones desde http://localhost:4200 y las
 * anotaciones @PostMapping con @RequestBody.
 *
 * El controller es delgado: delega la autenticación en LoginService, que
 * recorre las tres tablas (Veterinario, Administrador, Dueño) aceptando
 * correo o cédula, y devuelve el rol, el id y el nombre. Cuando las credenciales no
 * coinciden, el service lanza IllegalArgumentException y aquí se traduce a
 * un 401 con el mensaje, para que el formulario de login lo muestre.
 *
 * Flujo estricto: Controller -> Service -> Repository.
 */
@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Login", description = "Autenticación por rol (veterinario, administrador y cliente).")
public class LoginRestController {

    /** Servicio de autenticación (flujo obligatorio: Controller -> Service). */
    @Autowired
    private LoginService loginService;

    @Operation(summary = "Autentica por correo o cédula y contraseña y devuelve el rol, el id y el nombre del usuario.")
    @PostMapping
    public ResponseEntity<?> autenticar(@RequestBody LoginRequest credenciales) {
        try {
            ResultadoLogin resultado = loginService.autenticar(credenciales.getCorreo(), credenciales.getContrasena());
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException excepcion) {
            // Credenciales incompletas o sin coincidencia: 401 con el
            // mensaje exacto del service para que el login lo muestre.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", excepcion.getMessage()));
        }
    }
}
