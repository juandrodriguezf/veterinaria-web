package com.vetopia.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.vetopia.dto.UsuarioSesion;
import com.vetopia.dto.LoginResponse;
import com.vetopia.security.JwtService;

/**
 * CAPA SERVICIO - Implementación de LoginService
 * Autentica contra la cuenta unificada (users/roles) con el
 * AuthenticationManager de Spring Security y devuelve un JWT firmado y,
 * por separado, el rol, el id del perfil y el nombre. El formulario sigue
 * aceptando correo o cédula: la cédula se traduce al correo de su perfil
 * antes de autenticar.
 */
@Service
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;

    private final PerfilUsuarioService perfilUsuarioService;

    private final JwtService jwtService;

    public LoginServiceImpl(AuthenticationManager authenticationManager,
            PerfilUsuarioService perfilUsuarioService,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.perfilUsuarioService = perfilUsuarioService;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse autenticar(String identificador, String contrasena) {
        // Credenciales incompletas: no se consulta la base y el mensaje
        // indica qué le falta al usuario.
        if (identificador == null || identificador.isBlank()
                || contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException(
                    "Ingresa tu cédula o correo y tu contraseña para continuar.");
        }

        // La cuenta se identifica por el correo; si llegó una cédula se
        // resuelve antes de autenticar.
        String username = perfilUsuarioService.resolverUsername(identificador.trim());
        Authentication autenticacion = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, contrasena));

        UsuarioSesion perfil = perfilUsuarioService.obtenerPorUsername(autenticacion.getName());
        String token = jwtService.generarToken(autenticacion);

        return new LoginResponse(token, "Bearer", perfil.getRol(),
                perfil.getId(), perfil.getNombre());
    }
}
