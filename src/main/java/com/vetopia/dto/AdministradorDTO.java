package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta - Administrador
 * Versión de la entidad sin la contraseña ni la colección de veterinarios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdministradorDTO {

    /** Identificador del administrador. */
    private Integer id;

    /** Número de documento de identidad. */
    private String cedula;

    /** Correo electrónico del administrador. */
    private String correo;

    /** Nombre completo del administrador. */
    private String nombre;
}
