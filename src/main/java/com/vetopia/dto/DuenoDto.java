package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - DuenoDto
 * Respuesta plana de un cliente. No incluye la contraseña ni la lista de
 * mascotas; solo el conteo que ya calcula el service para el listado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DuenoDto {

    private Integer id;
    private String cedula;
    private String nombre;
    private String correo;
    private String celular;
    private String estado;

    /** Cantidad de mascotas del cliente (dato calculado, no columna). */
    private Integer mascotaCount;
}
