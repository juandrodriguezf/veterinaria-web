package com.vetopia.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ENTIDAD - Droga
 * Representa un medicamento/droga del inventario de Vetopia. No contiene
 * lógica de acceso a datos ni de presentación, solo el estado y las reglas
 * propias de la entidad.
 *
 * Es una entidad persistente JPA: Hibernate genera la tabla "drogas" a
 * partir de las anotaciones @Column y mapea la relación 1 -- 0..* con
 * Tratamiento (una droga se utiliza en cero o más tratamientos).
 */
@Entity
@Table(name = "drogas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"tratamientos"})
public class Droga {

    /** Identificador único de la droga (autogenerado por la base de datos). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Nombre del medicamento (único en el inventario). */
    @Column(nullable = false, unique = true, length = 150)
    private String nombre;

    /** Precio de compra del medicamento. */
    @Column(nullable = false)
    private Double precioCompra;

    /** Precio de venta del medicamento. */
    @Column(nullable = false)
    private Double precioVenta;

    /** Unidades disponibles en inventario. */
    @Column(nullable = false)
    private Integer unidadesDisponibles;

    /** Unidades vendidas acumuladas. */
    @Column(nullable = false)
    private Integer unidadesVendidas;

    /**
     * Tratamientos que utilizan la droga (lado inverso de la relación
     * Tratamiento 0..* -- 1 Droga; la FK vive en Tratamiento.droga).
     */
    @OneToMany(mappedBy = "droga")
    @Builder.Default
    private List<Tratamiento> tratamientos = new ArrayList<>();
}
