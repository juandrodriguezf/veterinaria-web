package com.vetopia.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ENTIDAD - Veterinario
 * Representa a un veterinario registrado en Vetopia. No contiene lógica de
 * acceso a datos ni de presentación, solo el estado y las reglas propias
 * de la entidad.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Veterinario {

    /** Identificador único del veterinario. */
    private Integer id;

    /** Número de documento de identidad del veterinario. */
    private String cedula;

    /** Contraseña de acceso del veterinario. */
    private String contrasena;

    /** Correo electrónico del veterinario. */
    private String correo;

    /** Especialidad del veterinario (Cirugía, Dermatología, etc.). */
    private String especialidad;

    /** Cantidad de atenciones acumuladas por el veterinario. */
    private Integer numeroAtenciones;

    /** Nombre completo del veterinario. */
    private String nombre;

    /** Estado del veterinario (Activo / Inactivo). */
    private String estado;

    /** URL de la foto del veterinario. */
    private String urlFoto;
}
