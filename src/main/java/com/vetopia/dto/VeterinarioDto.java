package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - VeterinarioDto
 * Respuesta plana del perfil de un veterinario. No expone la cuenta de
 * acceso ni los tratamientos; solo los datos del perfil y su estado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeterinarioDto {

    private Integer id;
    private String cedula;
    private String nombre;
    private String correo;
    private String especialidad;
    private String estado;
    private String urlFoto;
    private Integer numeroAtenciones;
}
