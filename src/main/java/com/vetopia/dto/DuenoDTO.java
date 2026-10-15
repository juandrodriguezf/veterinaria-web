package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta - Dueno (cliente)
 * Versión de la entidad que la API expone: deja fuera la contraseña
 * (dato que el frontend nunca necesita) y en su lugar incluye el conteo
 * de mascotas del dueño, que antes el frontend tenía que calcular
 * cruzando el listado de mascotas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuenoDTO {

    /** Identificador del dueño. */
    private Integer id;

    /** Número de documento de identidad del dueño. */
    private String cedula;

    /** Nombre completo del dueño. */
    private String nombre;

    /** Correo electrónico del dueño. */
    private String correo;

    /** Número de celular del dueño (puede venir nulo). */
    private String celular;

    /** Estado del dueño (Activo / Inactivo). */
    private String estado;

    /** Cantidad de mascotas registradas a su nombre. */
    private int mascotaCount;
}
