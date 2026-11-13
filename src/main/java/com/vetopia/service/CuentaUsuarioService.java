package com.vetopia.service;

import com.vetopia.entities.UserEntity;

/**
 * SERVICIO - CuentaUsuarioService
 * Centraliza la creación y sincronización de la cuenta de acceso
 * (UserEntity) asociada a cada perfil de negocio. Así los servicios de
 * administrador, veterinario y cliente no repiten el cifrado de la
 * contraseña ni la asignación de roles.
 */
public interface CuentaUsuarioService {

    /** Rol del administrador del hospital. */
    String ROL_ADMINISTRADOR = "ROLE_ADMINISTRADOR";

    /** Rol del veterinario. */
    String ROL_VETERINARIO = "ROLE_VETERINARIO";

    /** Rol del cliente (dueño de mascotas). */
    String ROL_CLIENTE = "ROLE_CLIENTE";

    /**
     * Construye una cuenta nueva con la contraseña cifrada y el rol
     * indicado. No la guarda: el perfil la persiste en cascada al guardarse.
     */
    UserEntity construir(String username, String contrasena, String rol);

    /**
     * Actualiza el usuario y el rol de una cuenta existente. Si la
     * contraseña llega vacía o nula se conserva el hash actual, de modo que
     * editar un perfil no obligue a reescribir la clave.
     */
    void sincronizar(UserEntity cuenta, String username, String contrasena, String rol);

    /** Sincroniza el indicador de cuenta activa con el estado del perfil. */
    void cambiarEstado(UserEntity cuenta, boolean activo);
}
