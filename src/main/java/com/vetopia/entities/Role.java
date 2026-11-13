package com.vetopia.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ENTIDAD - Role
 * Rol o autoridad de una cuenta (ROLE_ADMINISTRADOR, ROLE_VETERINARIO,
 * ROLE_CLIENTE). Se relaciona con UserEntity mediante la tabla intermedia
 * user_roles, de modo que la autorización se resuelve por permisos y no por
 * el tipo de tabla donde vive el perfil.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Role {

    /** Identificador único del rol (autogenerado por la base de datos). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Nombre del rol, con el prefijo ROLE_ que exige Spring Security. */
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
}
