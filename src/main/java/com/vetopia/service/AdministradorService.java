package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Administrador;

/**
 * SERVICIO - AdministradorService
 * Define la lógica de negocio relacionada con administradores. Es el único
 * punto al que los controladores acceden para obtener datos; a su vez,
 * delega el acceso a datos en la capa repository.
 */
public interface AdministradorService {

    /** Lista todos los administradores registrados. */
    List<Administrador> listarAdministradores();

    /** Obtiene un administrador validando previamente el identificador. */
    Administrador obtenerAdministradorPorId(Integer id);

    /**
     * Guarda (crea o actualiza) un administrador. Valida que la cédula y
     * el correo no pertenezcan a ninguna otra cuenta del sistema; al
     * editar, el registro actual no cuenta como duplicado.
     */
    void guardar(Administrador administrador);

    /** Busca un administrador por correo o cédula y contraseña (para login). */
    Administrador obtenerPorCorreoYContrasena(String identificador, String contrasena);
}
