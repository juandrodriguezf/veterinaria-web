package com.vetopia.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta - Tratamiento
 * El historial clínico anida solo lo que la ficha muestra: la droga y el
 * veterinario van como resúmenes (nombre y especialidad), y la mascota
 * solo por su identificador en lugar de la entidad completa con su dueño.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TratamientoDTO {

    /** Identificador del tratamiento. */
    private Integer id;

    /** Fecha en que se aplicó el tratamiento. */
    private LocalDate fecha;

    /** Identificador de la mascota tratada (nulo si quedó desvinculada). */
    private Integer mascotaId;

    /** Droga utilizada, como resumen. */
    private DrogaResumenDTO droga;

    /** Veterinario que realizó el tratamiento, como resumen. */
    private VeterinarioResumenDTO veterinario;
}
