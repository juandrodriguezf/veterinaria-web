package com.vetopia.service;

import com.vetopia.entities.ResultadoLogin;

/**
 * SERVICIO - LoginService
 * Define la lógica de negocio de autenticación. Como las credenciales
 * viven en tres entidades/tablas distintas (Dueño, Veterinario y
 * Administrador), el login busca en cada una de ellas hasta encontrar
 * una coincidencia de correo o cédula y contraseña, e identifica el rol
 * para que el frontend navegue a su panel correspondiente.
 */
public interface LoginService {

    /**
     * Autentica un usuario por correo o cédula y contraseña. Busca en el
     * orden Veterinario -> Administrador -> Dueño. Si encuentra coincidencia
     * devuelve un ResultadoLogin con el rol, el id y el nombre; si faltan
     * datos o no hay coincidencia lanza IllegalArgumentException con el
     * mensaje que el login debe mostrar.
     *
     * Rol: "VETERINARIO", "ADMINISTRADOR" o "CLIENTE".
     */
    ResultadoLogin autenticar(String identificador, String contrasena);
}
