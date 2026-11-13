package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - AdministradorDto
 * Respuesta plana de un administrador. No expone la cuenta de acceso ni la
 * lista de veterinarios que gestiona.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministradorDto {

    private Integer id;
    private String cedula;
    private String nombre;
    private String correo;
}
