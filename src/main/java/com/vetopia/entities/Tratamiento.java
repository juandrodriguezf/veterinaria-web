package com.vetopia.entities;

import java.time.LocalDate;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ENTIDAD - Tratamiento
 * Representa un tratamiento aplicado a una mascota en Vetopia. No contiene
 * lógica de acceso a datos ni de presentación, solo el estado y las reglas
 * propias de la entidad.
 *
 * Es la "tabla intermedia" del modelo: conecta Mascota, Droga y
 * Veterinario mediante relaciones ManyToOne (equivalente a la tabla de
 * unión del ejemplo del curso). Hibernate genera la tabla "tratamientos".
 */
@Entity
@Table(name = "tratamientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"mascota", "droga", "veterinario"})
public class Tratamiento {

    /** Identificador único del tratamiento (autogenerado por la base de datos). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Fecha en que se aplicó el tratamiento. */
    @Column(nullable = false)
    private LocalDate fecha;

    /**
     * Mascota que recibe el tratamiento (Mascota 1 -- 0..* Tratamiento).
     * Si la mascota se elimina, el service retira antes sus tratamientos.
     */
    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    /**
     * Droga utilizada en el tratamiento (Tratamiento 0..* -- 1 Droga).
     * Sin cascada: el historial clínico no debe borrarse si se retira
     * una droga del inventario.
     */
    @ManyToOne
    @JoinColumn(name = "droga_id", nullable = false)
    private Droga droga;

    /**
     * Veterinario que realizó el tratamiento (Veterinario 1 -- 0..*
     * Tratamiento). Sin cascada: el historial sobrevive a los cambios
     * de personal.
     */
    @ManyToOne
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;
}
