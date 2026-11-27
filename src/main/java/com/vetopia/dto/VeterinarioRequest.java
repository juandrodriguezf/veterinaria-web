package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - VeterinarioRequest
 * Cuerpo de entrada para crear o actualizar un veterinario. La contraseña
 * solo se usa para cifrarla en la cuenta unificada.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeterinarioRequest {

    private Integer id;
    private String cedula;
    private String nombre;
    private String correo;
    private String contrasena;
    private String especialidad;
    private String estado;
    private String urlFoto;
    private Integer numeroAtenciones;
}
