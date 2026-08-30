package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Dueno;

/**
 * SERVICIO - DuenoService
 * Define la lógica de negocio relacionada con dueños. Es el único punto
 * al que los controladores acceden para obtener datos; a su vez, delega
 * el acceso a datos en la capa repository.
 */
public interface DuenoService {

    /** Lista todos los dueños registrados. */
    List<Dueno> listarDuenos();

    /** Obtiene un dueño validando previamente el identificador. */
    Dueno obtenerDuenoPorId(Integer id);

    /** Guarda (registra) un dueño nuevo. El id lo asigna el repositorio. */
    void guardar(Dueno dueno);

    /** Busca un dueño por correo y contraseña (para login). */
    Dueno obtenerPorCorreoYContrasena(String correo, String contrasena);
}
