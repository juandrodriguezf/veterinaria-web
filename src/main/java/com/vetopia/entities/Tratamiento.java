package com.vetopia.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

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
     * Mascota que recibe el tratamiento (Mascota 0..1 -- 0..* Tratamiento).
     * La columna admite nulo: si la mascota se elimina, el service
     * desvincula sus tratamientos en lugar de borrarlos, de modo que el
     * historial clinico se conserva aunque quede sin mascota.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = true)
    private Mascota mascota;

    /**
     * Droga utilizada en el tratamiento (Tratamiento 0..* -- 1 Droga).
     * Sin cascada: el historial clínico no debe borrarse si se retira
     * una droga del inventario. La relación solo se acepta al crear el
     * tratamiento; en la API viajan los datos planos (drogaId y
     * drogaNombre) para no devolver objetos anidados.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "droga_id", nullable = false)
    private Droga droga;

    /**
     * Veterinario que realizó el tratamiento (Veterinario 1 -- 0..*
     * Tratamiento). La columna admite nulo: si el administrador elimina
     * al veterinario, el service desvincula sus tratamientos en lugar de
     * borrarlos, de modo que el historial clínico se conserva aunque
     * quede sin responsable. En la API viajan los datos planos
     * (veterinarioId, veterinarioNombre y veterinarioEspecialidad).
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "veterinario_id", nullable = true)
    private Veterinario veterinario;

    /** Expone SOLO el id de la droga: el objeto completo no viaja en la API. */
    @JsonProperty("drogaId")
    public Integer getDrogaId() {
        return droga != null ? droga.getId() : null;
    }

    /** Expone el nombre de la droga para el historial, sin anidar el objeto. */
    @JsonProperty("drogaNombre")
    public String getDrogaNombre() {
        return droga != null ? droga.getNombre() : null;
    }

    /** Expone SOLO el id del veterinario responsable. */
    @JsonProperty("veterinarioId")
    public Integer getVeterinarioId() {
        return veterinario != null ? veterinario.getId() : null;
    }

    /** Expone el nombre del veterinario; es nulo si fue retirado del sistema. */
    @JsonProperty("veterinarioNombre")
    public String getVeterinarioNombre() {
        return veterinario != null ? veterinario.getNombre() : null;
    }

    /** Expone la especialidad del veterinario; es nula si fue retirado. */
    @JsonProperty("veterinarioEspecialidad")
    public String getVeterinarioEspecialidad() {
        return veterinario != null ? veterinario.getEspecialidad() : null;
    }

    /** Expone SOLO el id de la mascota tratada. */
    @JsonProperty("mascotaId")
    public Integer getMascotaId() {
        return mascota != null ? mascota.getId() : null;
    }

    /** Expone el nombre de la mascota; es nulo si la mascota se eliminó. */
    @JsonProperty("mascotaNombre")
    public String getMascotaNombre() {
        return mascota != null ? mascota.getNombre() : null;
    }
}
