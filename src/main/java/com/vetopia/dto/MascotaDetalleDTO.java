package com.vetopia.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta - Mascota (detalle)
 * Versión para la ficha clínica y el detalle del cliente: el resumen más
 * los datos clínicos (fecha de ingreso y enfermedad) y el dueño como
 * resumen, que es lo único que la ficha muestra del propietario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaDetalleDTO {

    /** Identificador de la mascota. */
    private Integer id;

    /** Nombre de la mascota. */
    private String nombre;

    /** Especie (Perro, Gato, etc.). */
    private String especie;

    /** Raza de la mascota (puede venir nula). */
    private String raza;

    /** Edad en años (puede venir nula). */
    private Double edad;

    /** Sexo (Macho / Hembra). */
    private String sexo;

    /** URL de la imagen de la mascota (puede venir nula). */
    private String imagen;

    /** Peso en kilogramos (puede venir nulo). */
    private Double pesoKg;

    /** Color/característica del pelaje (puede venir nulo). */
    private String color;

    /** Fecha de ingreso a la clínica (puede venir nula). */
    private LocalDate fechaIngreso;

    /** Enfermedad o motivo de atención (puede venir nulo). */
    private String enfermedad;

    /** Estado de la mascota (Activo / Inactivo). */
    private String estado;

    /** Dueño de la mascota, como resumen. */
    private DuenoResumenDTO dueno;
}
