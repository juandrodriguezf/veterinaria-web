package com.vetopia.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ENTIDAD - Mascota
 * Representa una mascota registrada en Vetopia. No contiene lógica de
 * acceso a datos ni de presentación, solo el estado y las reglas propias
 * de la entidad.
 *
 * Es una entidad persistente JPA: Hibernate genera la tabla "mascotas" a
 * partir de las anotaciones @Column y mapea las relaciones con Dueno
 * (cada mascota pertenece a un dueño) y con Tratamiento (una mascota
 * recibe cero o más tratamientos).
 */
@Entity
@Table(name = "mascotas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"dueno", "tratamientos"})
public class Mascota {

    /** Identificador único de la mascota (autogenerado por la base de datos). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Nombre de la mascota. */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Especie (Perro, Gato, etc.). */
    @Column(nullable = false, length = 50)
    private String especie;

    /** Raza de la mascota (dato opcional). */
    @Column(length = 100)
    private String raza;

    /** Edad en años (admite decimales para menores a 1 año). */
    private Double edad;

    /** Sexo (Macho / Hembra). */
    @Column(length = 20)
    private String sexo;

    /** URL de la imagen de la mascota (null = mostrar placeholder). */
    @Column(length = 500)
    private String imagen;

    /** Peso en kilogramos (dato opcional). */
    private Double pesoKg;

    /** Color/característica del pelaje (dato opcional). */
    @Column(length = 100)
    private String color;

    /** Fecha de ingreso a la clínica (dato opcional). */
    private LocalDate fechaIngreso;

    /**
     * Enfermedad o motivo de atención actual (dato opcional, según el
     * diagrama de clases; se documenta en la ficha clínica).
     */
    @Column(length = 500)
    private String enfermedad;

    /**
     * Dueño que posee la mascota (relación Dueno 1 -- 0..* Mascota).
     * Toda mascota requiere dueño (regla del service); si el dueño se
     * elimina, el service retira antes sus mascotas (borrado en cascada
     * por capas, como en el ejemplo del docente).
     */
    @ManyToOne
    @JoinColumn(name = "dueno_id", nullable = false)
    private Dueno dueno;

    /**
     * Estado de la mascota (Activo / Inactivo). El veterinario puede
     * cambiar este valor para darla de alta o de baja en la clínica.
     */
    @Column(nullable = false, length = 20)
    private String estado;

    /**
     * Tratamientos que ha recibido la mascota (lado inverso de la
     * relación Mascota 1 -- 0..* Tratamiento; la FK vive en
     * Tratamiento.mascota).
     */
    @OneToMany(mappedBy = "mascota")
    @Builder.Default
    private List<Tratamiento> tratamientos = new ArrayList<>();

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
