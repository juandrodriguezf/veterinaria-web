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
 * ENTIDAD - Dueno
 * Representa el dueño (cliente) de una o varias mascotas registradas en
 * Vetopia. No contiene lógica de acceso a datos ni de presentación, solo
 * el estado y las reglas propias de la entidad.
 *
 * Es una entidad persistente JPA: Hibernate genera la tabla "duenos" a
 * partir de las anotaciones @Column (restricciones de nulidad, unicidad
 * y longitud) y mapea la relación 1 -- 0..* con Mascota.
 */
@Entity
@Table(name = "duenos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"mascotas"})
public class Dueno {

    /** Identificador único del dueño (autogenerado por la base de datos). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Número de documento de identidad del dueño (único). */
    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    /** Nombre completo del dueño. */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Correo electrónico del dueño (único; es el usuario del login). */
    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    /** Contraseña de acceso del dueño. */
    @Column(nullable = false, length = 255)
    private String contrasena;

    /** Número de celular del dueño (dato opcional). */
    @Column(length = 20)
    private String celular;

    /** Estado del dueño (Activo / Inactivo). */
    @Column(nullable = false, length = 20)
    private String estado;

    /**
     * Mascotas que posee el dueño (lado inverso de la relación
     * Dueno 1 -- 0..* Mascota; la FK vive en Mascota.dueno).
     */
    @OneToMany(mappedBy = "dueno")
    @Builder.Default
    private List<Mascota> mascotas = new ArrayList<>();
}
