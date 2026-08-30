package com.vetopia.entities;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ENTIDAD - Tratamiento
 * Representa un tratamiento aplicado a una mascota en Vetopia. No contiene
 * lógica de acceso a datos ni de presentación, solo el estado y las reglas
 * propias de la entidad.
 *
 * Lombok genera automáticamente getters/setters (@Data), el constructor
 * vacío (@NoArgsConstructor) y el constructor con todos los atributos
 * (@AllArgsConstructor), eliminando el código boilerplate.
 *
 * Según el diagrama de clases (docs/diagrams/script-class-diagram.txt),
 * el tratamiento está asociado a una mascota y a una droga, y expone
 * operaciones (crear, consultarDetalle) que se implementarán en la capa
 * de servicio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tratamiento {

    /** Identificador único del tratamiento. */
    private Integer id;

    /** Fecha en que se aplicó el tratamiento. */
    private LocalDate fecha;

    /**
     * Identificador de la mascota que recibe el tratamiento
     * (llave foránea a Mascota).
     */
    private Integer mascotaId;

    /**
     * Identificador de la droga utilizada en el tratamiento
     * (llave foránea a Droga).
     */
    private Integer drogaId;

    /**
     * Identificador del veterinario que realizó el tratamiento
     * (llave foránea a Veterinario).
     */
    private Integer veterinarioId;
}
