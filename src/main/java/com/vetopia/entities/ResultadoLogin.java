package com.vetopia.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase de apoyo del proceso de login: representa el resultado de una
 * autenticación exitosa. El frontend guarda únicamente el token en
 * localStorage; el rol, el id y el nombre se mantienen en memoria para
 * navegar al portal correcto y mostrar la cuenta autenticada.
 *
 * Lombok genera automáticamente getters/setters (@Data), el constructor
 * vacío (@NoArgsConstructor) y el constructor con todos los atributos
 * (@AllArgsConstructor).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoLogin {

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
