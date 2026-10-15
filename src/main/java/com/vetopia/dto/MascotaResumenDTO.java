package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta - Mascota (resumen)
 * Versión para los listados: solo los campos que la tabla muestra, sin
 * anidar el dueño. Así el listado de 100+ mascotas no arrastra el cliente
 * completo (con su contraseña) en cada fila.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaResumenDTO {

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

    /** Estado de la mascota (Activo / Inactivo). */
    private String estado;
}
