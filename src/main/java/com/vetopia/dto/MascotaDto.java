package com.vetopia.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - MascotaDto
 * Respuesta plana de una mascota para la API. No expone entidades anidadas:
 * el dueño viaja solo como identificador (duenoId) y el historial de
 * tratamientos se consulta aparte.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MascotaDto {

    private Integer id;
    private String nombre;
    private String especie;
    private String raza;
    private Double edad;
    private String sexo;
    private String imagen;
    private Double pesoKg;
    private String color;
    private LocalDate fechaIngreso;
    private String enfermedad;
    private String estado;

    /** Identificador del dueño (no se anida el objeto Dueno). */
    private Integer duenoId;
}
