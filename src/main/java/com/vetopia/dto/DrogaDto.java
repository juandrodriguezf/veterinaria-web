package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - DrogaDto
 * Respuesta plana del inventario: solo lo que el frontend usa (nombre y
 * stock). Los precios y las unidades vendidas quedan en el backend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrogaDto {

    private Integer id;
    private String nombre;
    private Integer unidadesDisponibles;
}
