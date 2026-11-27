package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - AdministradorRequest
 * Cuerpo de entrada para crear o actualizar un administrador.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministradorRequest {

    private Integer id;
    private String cedula;
    private String nombre;
    private String correo;
    private String contrasena;
}
