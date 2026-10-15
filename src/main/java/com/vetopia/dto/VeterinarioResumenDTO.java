package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta - Veterinario (resumen)
 * Solo lo indispensable para anidar dentro de un tratamiento: el nombre y
 * la especialidad del médico tratante.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioResumenDTO {

    /** Identificador del veterinario. */
    private Integer id;

    /** Nombre completo del veterinario. */
    private String nombre;

    /** Especialidad del veterinario. */
    private String especialidad;
}
