package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - DrogaRequest
 * Cuerpo de entrada para registrar una droga en el inventario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrogaRequest {

    private Integer id;
    private String nombre;
    private Double precioCompra;
    private Double precioVenta;
    private Integer unidadesDisponibles;
    private Integer unidadesVendidas;
}
