package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - UsuarioSesion
 * Datos mínimos del usuario autenticado que el frontend necesita para
 * identificar la cuenta y navegar a su portal. No incluye contraseña ni
 * entidades anidadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSesion {

    /** Rol del usuario ("ADMINISTRADOR", "VETERINARIO" o "CLIENTE"). */
    private String rol;

    /** Identificador del perfil en su tabla de negocio. */
    private Integer id;

    /** Nombre completo del usuario autenticado. */
    private String nombre;
}
