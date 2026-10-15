package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta - Droga
 * Versión completa de la entidad para el inventario: sin las colecciones
 * anidadas (Jackson ya las ignora) y sin datos de otros recursos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrogaDTO {

    /** Identificador de la droga. */
    private Integer id;

    /** Nombre del medicamento. */
    private String nombre;

    /** Precio de compra del medicamento. */
    private Double precioCompra;

    /** Precio de venta del medicamento. */
    private Double precioVenta;

    /** Unidades disponibles en inventario. */
    private Integer unidadesDisponibles;

    /** Unidades vendidas acumuladas. */
    private Integer unidadesVendidas;
}
