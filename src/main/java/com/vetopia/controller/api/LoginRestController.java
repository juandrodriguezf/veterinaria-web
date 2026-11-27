package com.vetopia.controller.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetopia.dto.LoginRequest;
import com.vetopia.dto.LoginResponse;
import com.vetopia.service.LoginService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * CONTROLLER REST - Login
 * Autentica contra la cuenta unificada y devuelve un JWT firmado con el
 * rol, el id del perfil y el nombre. El controller es delgado: delega en
 * LoginService, que usa Spring Security para validar la contraseña BCrypt.
 *
 * Los errores de credenciales se traducen a 401 con un mensaje claro para
 * que el formulario de login lo muestre.
 */
@RestController
@RequestMapping("/login")
@Tag(name = "Login", description = "Autenticación por rol (veterinario, administrador y cliente).")
public class LoginRestController {

    /** Servicio de autenticación (flujo obligatorio: Controller -> Service). */
    @Autowired
    private LoginService loginService;

    @Operation(summary = "Autentica por correo o cédula y devuelve el token JWT con el rol, el id y el nombre.")
    @PostMapping
    public ResponseEntity<?> autenticar(@RequestBody LoginRequest credenciales) {
        try {
            LoginResponse resultado = loginService.autenticar(
                    credenciales.getCorreo(), credenciales.getContrasena());
            return ResponseEntity.ok(resultado);
        } catch (DisabledException excepcion) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "La cuenta se encuentra inactiva."));
        } catch (AuthenticationException excepcion) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje",
                            "Cédula, correo o contraseña incorrectos. Verifica tus credenciales."));
        } catch (IllegalArgumentException excepcion) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", excepcion.getMessage()));
        }
    }
}
