package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - LoginResponse
 * Respuesta del login: token JWT firmado más los datos mínimos que el
 * frontend necesita para navegar al portal correcto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /** Token JWT firmado que el frontend enviará en cada petición. */
    private String token;

    /** Tipo de token (siempre "Bearer"). */
    private String tipo;

    /** Rol del usuario autenticado ("VETERINARIO", "ADMINISTRADOR" o "CLIENTE"). */
    private String rol;

    /** Identificador del usuario en la tabla de su rol. */
    private Integer id;

    /** Nombre completo del usuario autenticado. */
    private String nombre;
}
