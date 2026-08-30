package com.vetopia.repository;

import java.util.Collection;

import com.vetopia.entities.Administrador;

/**
 * Único punto de acceso a los datos de administradores. Define las
 * operaciones de persistencia que la capa Service puede utilizar sin
 * exponer detalles de la fuente de datos (por ahora HashMap en memoria).
 */
public interface AdministradorRepository {

    /** Devuelve todos los administradores registrados. */
    Collection<Administrador> searchAll();

    /**
     * Busca un administrador por su identificador dentro del HashMap.
     */
    Administrador searchById(Integer id);

    /**
     * Guarda (persiste) un administrador en el HashMap asignándole
     * automáticamente el siguiente id disponible.
     */
    void save(Administrador administrador);

    /**
     * Busca un administrador por su correo y contraseña (credenciales de
     * inicio de sesión). Devuelve null si no existe coincidencia.
     */
    Administrador searchByCorreoYContrasena(String correo, String contrasena);
}
