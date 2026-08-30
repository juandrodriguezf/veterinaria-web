package com.vetopia.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ENTIDAD - Droga
 * Representa un medicamento/droga del inventario de Vetopia. No contiene
 * lógica de acceso a datos ni de presentación, solo el estado y las reglas
 * propias de la entidad.
 *
 * Lombok genera automáticamente getters/setters (@Data), el constructor
 * vacío (@NoArgsConstructor) y el constructor con todos los atributos
 * (@AllArgsConstructor), eliminando el código boilerplate.
 *
 * Según el diagrama de clases (docs/diagrams/script-class-diagram.txt),
 * expone operaciones de inventario (actualizarInventario, venderUnidad,
 * hayDisponibilidad, etc.) que se implementarán en la capa de servicio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Droga {

    /** Identificador único de la droga. */
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
