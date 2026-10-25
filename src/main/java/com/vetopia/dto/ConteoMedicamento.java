package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - ConteoMedicamento
 * Objeto de transferencia (no es una entidad del diagrama de clases):
 * representa una fila de conteo del dashboard, medicamento + cantidad. Se
 * usa tanto para la tabla de tratamientos por medicamento del último mes
 * como para el top 3 de medicamentos más vendidos, de modo que la API no
 * devuelva objetos anidados ni estructuras distintas para cada KPI.
 *
 * Lombok genera getters/setters (@Data) y los constructores vacío y
 * completo, igual que en las clases de apoyo del proyecto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConteoMedicamento {

    /** Nombre del medicamento. */
    private String nombre;

    /** Cantidad asociada al KPI (tratamientos del mes o unidades vendidas). */
    private Long cantidad;
}
