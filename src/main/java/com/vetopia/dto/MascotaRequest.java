package com.vetopia.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - MascotaRequest
 * Cuerpo de entrada para crear o actualizar una mascota. El dueño viaja
 * solo como identificador (duenoId), no como entidad anidada.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MascotaRequest {

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
    private Integer duenoId;
    private String estado;
}
