package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta - Dueno (resumen)
 * Datos mínimos del propietario para anidar en la ficha de una mascota:
 * no arrastra el cliente completo (ni su contraseña ni su colección de
 * mascotas).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuenoResumenDTO {

    /** Identificador del dueño. */
    private Integer id;

    /** Nombre completo del dueño. */
    private String nombre;

    /** Número de celular del dueño (puede venir nulo). */
    private String celular;

    /** Correo electrónico del dueño. */
    private String correo;
}
