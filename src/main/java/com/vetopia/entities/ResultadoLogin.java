package com.vetopia.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase de apoyo del proceso de login: representa el resultado de una
 * autenticación exitosa. Contiene el rol del usuario autenticado
 * ("VETERINARIO", "ADMINISTRADOR" o "CLIENTE") y su identificador en
 * la tabla correspondiente, para que el Controller pueda redirigir al
 * portal correcto.
 *
 * Lombok genera automáticamente getters/setters (@Data), el constructor
 * vacío (@NoArgsConstructor) y el constructor con todos los atributos
 * (@AllArgsConstructor), igual que en las demás entidades.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoLogin {

    /** Rol del usuario autenticado ("VETERINARIO", "ADMINISTRADOR" o "CLIENTE"). */
    private String rol;

    /** Identificador del usuario en la tabla de su rol. */
    private Integer id;
}
