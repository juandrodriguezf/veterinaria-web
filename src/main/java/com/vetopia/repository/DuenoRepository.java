package com.vetopia.repository;

import java.util.Collection;

import com.vetopia.entities.Dueno;

/**
 * Único punto de acceso a los datos de dueños. Define las operaciones de
 * persistencia que la capa Service puede utilizar sin exponer detalles de
 * la fuente de datos (por ahora HashMap en memoria).
 */
public interface DuenoRepository {

    /** Devuelve todos los dueños registrados. */
    Collection<Dueno> searchAll();

    /**
     * Busca un dueño por su identificador dentro del HashMap.
     */
    Dueno searchById(Integer id);

    /**
     * Guarda (persiste) un dueño en el HashMap asignándole
     * automáticamente el siguiente id disponible.
     */
    void save(Dueno dueno);

    /**
     * Busca un dueño por su correo y contraseña (credenciales de
     * inicio de sesión). Devuelve null si no existe coincidencia.
     */
    Dueno searchByCorreoYContrasena(String correo, String contrasena);

    /** Cambia el estado (Activo/Inactivo) de un dueño por su id. */
    void cambiarEstado(Integer id, String estado);
}
