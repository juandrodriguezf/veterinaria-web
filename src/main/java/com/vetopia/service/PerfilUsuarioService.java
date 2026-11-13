package com.vetopia.service;

import com.vetopia.dto.UsuarioSesion;

/**
 * SERVICIO - PerfilUsuarioService
 * Resuelve la identidad de negocio (rol, id y nombre) a partir del nombre
 * de usuario de la cuenta unificada, y traduce una cédula o un correo al
 * username que espera Spring Security.
 */
public interface PerfilUsuarioService {

    /**
     * Devuelve el perfil (rol, id y nombre) asociado al correo indicado.
     * Lanza UsernameNotFoundException si no corresponde a ningún perfil.
     */
    UsuarioSesion obtenerPorUsername(String username);

    /**
     * Traduce el identificador del formulario (correo o cédula) al nombre
     * de usuario de la cuenta. Lanza BadCredentialsException si la cédula
     * no pertenece a ninguna cuenta.
     */
    String resolverUsername(String identificador);
}
