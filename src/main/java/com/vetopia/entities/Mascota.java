package com.vetopia.entities;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ENTIDAD - Mascota
 * Representa una mascota registrada en Vetopia. No contiene lógica de
 * acceso a datos ni de presentación, solo el estado y las reglas propias
 * de la entidad.
 *
 * Lombok genera automáticamente getters/setters (@Data), el constructor
 * vacío (@NoArgsConstructor) y el constructor con todos los atributos
 * (@AllArgsConstructor), eliminando el código boilerplate.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mascota {

    /** Identificador único de la mascota. */
    private Integer id;

    /** Nombre de la mascota. */
    private String nombre;

    /** Especie (Perro, Gato, etc.). */
    private String especie;

    /** Raza de la mascota. */
    private String raza;

    /** Edad en años (admite decimales para menores a 1 año). */
    private Double edad;

    /** Sexo (Macho / Hembra). */
    private String sexo;

    /** URL de la imagen de la mascota (null = mostrar placeholder). */
    private String imagen;

    /** Peso en kilogramos (dato opcional). */
    private Double pesoKg;

    /** Color/característica del pelaje (dato opcional). */
    private String color;

    /** Fecha de ingreso a la clínica (dato opcional). */
    private LocalDate fechaIngreso;

    /**
     * Identificador del dueño que posee la mascota (llave foránea a
     * Dueno). En una base de datos real esta es una FK; en la fake DB
     * en memoria se guarda el id y el Service resuelve el objeto
     * preguntando a DuenoRepository.
     */
    private Integer duenoId;

    /**
     * Estado de la mascota (Activo / Inactivo). El veterinario puede
     * cambiar este valor para darla de alta o de baja en la clínica.
     */
    private String estado;

    /**
     * Indica si la mascota tiene una URL de imagen disponible.
     * Las vistas usan este método para decidir entre mostrar la imagen
     * o el placeholder.
     */
    public boolean tieneImagen() {
        return imagen != null && !imagen.isBlank();
    }

    /**
     * Devuelve la edad en formato legible, por ejemplo "5 años"
     * o "8 meses" cuando la edad es menor a un año.
     */
    public String obtenerEdadDescripcion() {
        if (edad == null) {
            return "No registrada";
        }
        if (edad < 1) {
            long meses = Math.max(1, Math.round(edad * 12));
            return meses + (meses == 1 ? " mes" : " meses");
        }
        long anios = (long) Math.floor(edad);
        return anios + (anios == 1 ? " año" : " años");
    }
}
