package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - DuenoRequest
 * Cuerpo de entrada para crear o actualizar un cliente. La contraseña se
 * usa únicamente para cifrarla en la cuenta unificada; en edición puede
 * llegar vacía para conservar la actual.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DuenoRequest {

    private Integer id;
    private String cedula;
    private String nombre;
    private String correo;
    private String contrasena;
    private String celular;
    private String estado;
}
