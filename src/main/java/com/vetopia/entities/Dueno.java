package com.vetopia.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ENTIDAD - Dueno
 * Representa el dueño (cliente) de una o varias mascotas registradas en
 * Vetopia. No contiene lógica de acceso a datos ni de presentación, solo
 * el estado y las reglas propias de la entidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dueno {

    /** Identificador único del dueño. */
    private Integer id;

    /** Número de documento de identidad del dueño. */
    private String cedula;

    /** Nombre completo del dueño. */
    private String nombre;

    /** Correo electrónico del dueño. */
    private String correo;

    /** Contraseña de acceso del dueño. */
    private String contrasena;

    /** Número de celular del dueño. */
    private String celular;

    /** Estado del dueño (Activo / Inactivo). */
    private String estado;
}
