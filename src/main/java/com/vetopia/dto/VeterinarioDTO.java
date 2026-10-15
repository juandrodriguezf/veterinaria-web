package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta - Veterinario
 * Versión de la entidad sin la contraseña ni las colecciones anidadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioDTO {

    /** Identificador del veterinario. */
    private Integer id;

    /** Número de documento de identidad. */
    private String cedula;

    /** Correo electrónico del veterinario. */
    private String correo;

    /** Especialidad del veterinario. */
    private String especialidad;

    /** Cantidad de atenciones acumuladas. */
    private Integer numeroAtenciones;

    /** Nombre completo del veterinario. */
    private String nombre;

    /** Estado del veterinario (Activo / Inactivo). */
    private String estado;

    /** URL de la foto del veterinario (puede venir nula). */
    private String urlFoto;
}
