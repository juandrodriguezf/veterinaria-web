package com.vetopia.service;

/**
 * SERVICIO - LoginService
 * Define la lógica de negocio de autenticación. Como las credenciales
 * viven en tres entidades/tablas distintas (Dueño, Veterinario y
 * Administrador), el login busca en cada una de ellas hasta encontrar
 * una coincidencia de correo y contraseña, e identifica el rol para
 * redirigir al usuario a su panel correspondiente.
 */
public interface LoginService {

    /**
     * Autentica un usuario por correo y contraseña. Busca en el orden
     * Veterinario -> Administrador -> Dueño. Si encuentra coincidencia
     * devuelve un ResultadoLogin con el rol y el id; si no hay
     * coincidencia devuelve null.
     *
     * Rol: "VETERINARIO", "ADMINISTRADOR" o "CLIENTE".
     */
    ResultadoLogin autenticar(String correo, String contrasena);

    /**
     * Resultado de una autenticación exitosa: el rol del usuario
     * autenticado y su identificador en la tabla correspondiente.
     */
    record ResultadoLogin(String rol, Integer id) {
    }
}
