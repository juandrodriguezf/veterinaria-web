package com.vetopia.repository;

import java.util.Collection;

import com.vetopia.entities.Veterinario;

/**
 * Único punto de acceso a los datos de veterinarios. Define las
 * operaciones de persistencia que la capa Service puede utilizar sin
 * exponer detalles de la fuente de datos (por ahora HashMap en memoria).
 */
public interface VeterinarioRepository {

    /** Devuelve todos los veterinarios registrados. */
    Collection<Veterinario> searchAll();

    /**
     * Busca un veterinario por su identificador dentro del HashMap.
     */
    Veterinario searchById(Integer id);

    /**
     * Guarda (persiste) un veterinario en el HashMap asignándole
     * automáticamente el siguiente id disponible.
     */
    void save(Veterinario veterinario);

    /**
     * Busca un veterinario por su correo y contraseña (credenciales de
     * inicio de sesión). Devuelve null si no existe coincidencia.
     */
    Veterinario searchByCorreoYContrasena(String correo, String contrasena);
}
