package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta - Droga (resumen)
 * Solo lo indispensable para anidar dentro de un tratamiento: el
 * historial clínico necesita el nombre del medicamento, no todo el
 * inventario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrogaResumenDTO {

    /** Identificador de la droga. */
    private Integer id;

    /** Nombre del medicamento. */
    private String nombre;
}
