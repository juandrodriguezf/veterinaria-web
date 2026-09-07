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
 * ENTIDAD - Administrador
 * Representa al administrador de Vetopia. No contiene lógica de acceso a
 * datos ni de presentación, solo el estado y las reglas propias de la
 * entidad.
 *
 * Es una entidad persistente JPA: Hibernate genera la tabla
 * "administradores" a partir de las anotaciones @Column y mapea la
 * relación 1 -- 0..* con Veterinario (un administrador gestiona cero o
 * más veterinarios).
 */
@Entity
@Table(name = "administradores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"veterinarios"})
public class Administrador {

    /** Identificador único del administrador (autogenerado por la base de datos). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Número de documento de identidad del administrador (único). */
    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    /** Correo electrónico del administrador (único; es el usuario del login). */
    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    /** Contraseña de acceso del administrador. */
    @Column(nullable = false, length = 255)
    private String contrasena;

    /** Nombre completo del administrador. */
    @Column(nullable = false, length = 100)
    private String nombre;

    /**
     * Veterinarios que gestiona (lado inverso de la relación
     * Administrador 1 -- 0..* Veterinario; la FK vive en
     * Veterinario.administrador).
     */
    @OneToMany(mappedBy = "administrador")
    @Builder.Default
    private List<Veterinario> veterinarios = new ArrayList<>();
}
