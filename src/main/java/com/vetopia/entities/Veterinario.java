package com.vetopia.entities;

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
 * ENTIDAD - Veterinario
 * Representa a un veterinario registrado en Vetopia. No contiene lógica de
 * acceso a datos ni de presentación, solo el estado y las reglas propias
 * de la entidad.
 *
 * Es una entidad persistente JPA: Hibernate genera la tabla "veterinarios"
 * a partir de las anotaciones @Column y mapea las relaciones con
 * Administrador (cada veterinario es gestionado por uno) y con
 * Tratamiento (un veterinario realiza cero o más tratamientos).
 */
@Entity
@Table(name = "veterinarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"administrador", "tratamientos"})
public class Veterinario {

    /** Identificador único del veterinario (autogenerado por la base de datos). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Número de documento de identidad del veterinario (único). */
    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    /** Contraseña de acceso del veterinario. */
    @Column(nullable = false, length = 255)
    private String contrasena;

    /** Correo electrónico del veterinario (único; es el usuario del login). */
    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    /** Especialidad del veterinario (Cirugía, Dermatología, etc.). */
    @Column(length = 100)
    private String especialidad;

    /** Cantidad de atenciones acumuladas por el veterinario. */
    @Column(nullable = false)
    private Integer numeroAtenciones;

    /** Nombre completo del veterinario. */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Estado del veterinario (Activo / Inactivo). */
    @Column(nullable = false, length = 20)
    private String estado;

    /** URL de la foto del veterinario (dato opcional). */
    @Column(length = 500)
    private String urlFoto;

    /**
     * Administrador que lo gestiona (Administrador 1 -- 0..* Veterinario).
     * Mientras no exista flujo de administradores en el portal queda en
     * null y la columna admite nulo.
     */
    @ManyToOne
    @JoinColumn(name = "administrador_id")
    private Administrador administrador;

    /**
     * Tratamientos que ha realizado (lado inverso de la relación
     * Veterinario 1 -- 0..* Tratamiento; la FK vive en
     * Tratamiento.veterinario).
     */
    @OneToMany(mappedBy = "veterinario")
    @Builder.Default
    private List<Tratamiento> tratamientos = new ArrayList<>();
}
