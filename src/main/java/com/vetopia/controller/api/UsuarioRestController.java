package com.vetopia.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetopia.dto.UsuarioSesion;
import com.vetopia.service.PerfilUsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * CONTROLLER REST - Usuario
 * Expone la identidad del usuario autenticado. Angular lo consulta al
 * abrir la aplicación para restaurar la sesión desde el token y redirigir
 * al portal que corresponde, sin guardar ids manipulables en localStorage.
 */
@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuario", description = "Identidad del usuario autenticado.")
public class UsuarioRestController {

    /** Servicio que resuelve el perfil (rol, id y nombre) de la cuenta. */
    @Autowired
    private PerfilUsuarioService perfilUsuarioService;

    @Operation(summary = "Devuelve el rol, el id y el nombre del usuario autenticado.")
    @GetMapping("/me")
    public UsuarioSesion obtenerSesion(@AuthenticationPrincipal UserDetails usuario) {
        return perfilUsuarioService.obtenerPorUsername(usuario.getUsername());
    }
}
