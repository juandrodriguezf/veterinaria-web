package com.vetopia.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - TratamientoDto
 * Respuesta plana del historial clínico: datos de la droga, el veterinario
 * y la mascota como campos simples, sin entidades anidadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TratamientoDto {

    private Integer id;
    private LocalDate fecha;

    private Integer drogaId;
    private String drogaNombre;

    private Integer veterinarioId;
    private String veterinarioNombre;
    private String veterinarioEspecialidad;

    private Integer mascotaId;
    private String mascotaNombre;
}
